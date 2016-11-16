package TelnetIBM;

import android.view.KeyEvent;

import com.cipherlab.terminalemulation.R;
import com.te.UI.CipherUtility;
import com.te.UI.ServerKeyEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import Terminals.KeyMapItem;
import Terminals.KeyMapList;
import Terminals.KeyMapUtility;
import Terminals.stdActivityRef;

import static TelnetIBM.IBMHost3270.IBmOrder3270.*;
import static TelnetIBM.IBMHost3270.IBmStates.*;

public class IBMHost3270 extends IBMHostBase {
    enum IBmStates {
        IBMS_None,
        IBMS_Ground,
        IBMS_Command,
        IBMS_CommandEnd,
        IBMS_CommandEndEx,
        IBMS_Orders,
        IBMS_OrdersEx,
        IBMS_Anywhere,
    }

    enum IBmActions {
        IBMA_None,
        IBMA_ParseCommand,
        IBMA_ParseWCC,
        IBMA_RecordData,
        IBMA_ParseData,
        IBMA_OrdersRecord,
        IBMA_OrdersParse,
    }

    class IBMCommand3270 {
        public static final char IBMCm_Write = '\u0001';
        public static final char IBMCm_ReadBuffer = '\u0002';
        public static final char IBMCm_NonAction = '\u0003';
        public static final char IBMCm_Sense = '\u0004';
        public static final char IBMCm_EraseWrite = '\u0005';
        public static final char IBMCm_ReadMod = '\u0006';
        public static final char IBMCm_Select = '\u000b';
        public static final char IBMCm_WriteField = '\u0011';
    }

    class IBmOrder3270 {
        public static final char EraseUnprotectedToAddress = '\u0012' ;
        public static final char InsertCursor = '\u0013' ;
        public static final char ModifyField = '\u002c' ;
        public static final char ProgramTab = '\u0005' ;
        public static final char RepeatToAddress = '\u003c' ;
        public static final char SetAttribute = '\u0028' ;
        public static final char SetBufferAddress = '\u0011' ;
        public static final char StartField = '\u001d';
        public static final char StartFieldExtended = '\u0029';
    }

    private static class TN_StateChangeInfo {
        IBmStates mIbmState;
        Transitions mTransition;
        IBmActions mAction;
        TN_StateChangeInfo(IBmStates ibmState, Transitions tran, IBmActions act) {
            mIbmState = ibmState;
            mTransition = tran;
            mAction = act;
        }
    }

    private static class TN_StateEventInfo {
        public IBmStates mPreState;
        public char mCharFrom;
        public char mCharTo;
        public IBmActions mAction;
        public IBmStates mState; // the state we are going to

        TN_StateEventInfo(IBmStates preState, char charFrom, char charTo, IBmActions action, IBmStates state) {
            mPreState = preState;
            mCharFrom = charFrom;
            mCharTo = charTo;
            mAction = action;
            mState = state;
        }
    }

    private class tagField {
        public byte cOrder;                             //field order
//        byte cRESQ;                                   //resequence order
        public byte x;
        public byte y;                                  //start positon of a field
        public byte [] szFFW = new byte[2];             //FFW, "00" if not defined
        public byte [] szFCW = new byte[2];             //FCW, "00" if not defined
        public byte cAttrib;                            //Attribute
        public int  nLen;                               //Field Length
        public tagField pNext;
        public tagField pPrev;
        public tagField() {

        }

        public void copy(tagField aField) {
            cOrder = aField.cOrder;
            x = aField.x;
            y = aField.y;
            szFFW[0] = aField.szFFW[0];
            szFFW[1] = aField.szFFW[1];
            szFCW[0] = aField.szFCW[0];
            szFCW[1] = aField.szFCW[1];
            cAttrib = aField.cAttrib;
            nLen = aField.nLen;
            pNext = aField.pNext;
            pPrev = aField.pPrev;
        }

        /*-----------------------------------------------------------------------------
         -Purpose: check if the value of current field is valid
         -Param  : aField: the field to check
         -Return : true: valid; false: invalid
         -Remark :
         -----------------------------------------------------------------------------*/
        public boolean valid() {
            if (cOrder == 0 && nLen == 0)
                return false;
            else
                return true;
        }

        public int getShiftSpec() {
            return -1;
        }

        /*-----------------------------------------------------------------------------
         -Purpose: Check if the poing (x,y) is in aField
         -Return : true: pt is in the field; false: pt is not in the field
         -Remark :
         -----------------------------------------------------------------------------*/
        public boolean ptInField(int x, int y) {
            if (!valid())
                return false;
            boolean bRet = false;
            int nRow = (x + nLen) / _cols;

            if (y == this.y) {
                //the first line
                if (nRow > 0 && x >= this.x) {
                    //multy lines
                    bRet = true;
                } else if (x >= this.x && x < this.x + this.nLen) {
                    //only one line
                    bRet = true;
                }
            } else if (y == this.y + nRow) {
                //the last line
                if (x < (this.x + this.nLen) % _cols)
                    bRet = true;
            } else if (y > this.y && y < this.y + nRow) {
                //middle lines
                bRet = true;
            }

            return bRet;
        }

        public boolean canExit() {
            boolean bRet = true;
            if (getBit(szFFW[1], 5) == true && //MF
                    getBit(szFFW[1], 6) == true &&
                    getBit(szFFW[1], 7) == true) {
                boolean bFirst = (CharGrid[y][x] == 0);
                for (int idxX = x; idxX < x + nLen; idxX++) {
                    boolean bItr = (CharGrid[y][idxX] == 0);
                    if (bFirst != bItr) {
                        bRet = false;
                        break;
                    }
                }
            } else if (getBit(szFFW[1], 4)) {//ME
                bRet = false;
                for (int idxX = x; idxX < x + nLen; idxX++) {
                    if (CharGrid[y][idxX] != 0) {
                        bRet = true;
                        break;
                    }
                }
            }
            return bRet;
        }
    }

    private class CTNTag {
        private tagField pHead;
        private tagField pEnd;
        private tagField pCurr;
        private tagField pSave;
        private tagField CurrAdd;
        private ArrayList<tagField> ListMem = new ArrayList();

        CTNTag() {
            pHead = pEnd = pCurr = pSave = CurrAdd = null;
        }

        void initList() {
            ListMem.clear();
            pHead = pEnd = pCurr = null;
        }

        boolean addMember(tagField aField) {
            tagField newField = new tagField();
            newField.copy(aField);
            if(pHead == null) {
                pHead = pEnd = pCurr = CurrAdd = aField;
                pHead.pPrev = null;
                ListMem.add(newField);
            } else {
                tagField pSearchField = pHead;
                boolean bMatch = false;
                while (pSearchField != null) {
                    if (pSearchField.x == newField.x && pSearchField.y == newField.y) {
                        pSearchField.cAttrib = newField.cAttrib;
                        pSearchField.szFCW[0] = newField.szFCW[0];
                        pSearchField.szFCW[1] = newField.szFCW[1];
                        pSearchField.szFFW[0] = newField.szFFW[0];
                        pSearchField.szFFW[1] = newField.szFFW[1];
                        bMatch = true;
                        CurrAdd = newField;
                        break;
                    }
                    pSearchField = pSearchField.pNext;
                }

                if (!bMatch) {
                    CurrAdd = newField;
                    pEnd.pNext = newField;
                    newField.pPrev = pEnd;
                    pEnd = newField;
                    pEnd.pNext = null;
                    ListMem.add(newField);
                }
            }
            return true;
        }

        boolean isEmpty() {
            if (pHead == null)
                return true;
            return false;
        }
        boolean isHead() {
            if (pCurr.pPrev == null)
                return true;
            else
                return false;
        }
        boolean isLast() {
            if (pCurr.pNext == null)
                return true;
            else
                return false;
        }

        boolean toFirst() {
            if (pHead != null) {
                pCurr = pHead;
                return true;
            }
            else
                return false;
        }
        boolean toLast() {
            if (pEnd != null) {
                pCurr = pEnd;
                return true;
            }
            else
                return false;
        }
        boolean toNext() {
            if(pCurr == null || pCurr.pNext == null)
                return false;
            else {
                pCurr = pCurr.pNext;
                return true;
            }
        }
        boolean toPrev() {
            if(pCurr == null || pCurr.pPrev == null)
                return false;
            else {
                pCurr = pCurr.pPrev;
                return true;
            }
        }

        boolean readNext(tagField aField) {
            if (pHead == null)
                return false;
            else {
                if(toNext() == false)
                    return false;

                aField.copy(pCurr);
                toPrev();
                return true;
            }
        }

        boolean getPrev(tagField pField) {
            if (pHead == null)
                return false;
            else {
                if(toPrev() == false)
                    return false;
                pField.copy(pCurr);
                return true;
            }
        }

        boolean getCurr(tagField aField) {
            if (pHead == null)
                return false;
            else {
                aField.copy(pCurr);
                return true;
            }
        }

        boolean setCurr(tagField aField) {
            if (pCurr == null)
                return false;
            else {
                pCurr.copy(aField);
                return true;
            }
        }

        boolean getCurrAdd(tagField aField) {
            if (CurrAdd == null)
                return false;
            else {
                aField.copy(CurrAdd);
                return true;
            }
        }
        boolean setCurrAdd(tagField pField) {
            if (CurrAdd == null)
                return false;
            else {
                CurrAdd.copy(pField);
                return true;
            }
        }

        void saveListPos() {
            pSave = pCurr;
        }
        void restoreListPos() {
            pCurr = pSave;
        }
    }

    //Tables
    final static private char[] szEBCDIC = {
        0x00, 0x01, 0x02, 0x03, 0x00, 0x09, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0b, 0x0c, 0x0d, 0x20, 0x20, 0x10, 0x11, 0x12, 0x13, 0x00, 0x00, 0x08, 0x00, 0x18, 0x19, 0x00, 0x00, 0x1c, 0x1d, 0x1e, 0x1f, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0a, 0x17, 0x1b, 0x00, 0x00, 0x00, 0x00, 0x00, 0x05, 0x06, 0x07, 0x00, 0x00, 0x16, 0x00, 0x00, 0x00, 0x00, 0x04, 0x00, 0x00, 0x00, 0x00, 0x14, 0x15, 0x00, 0x00,
        0x20, 0x20, 0xe2, 0xe4, 0xe0, 0xe1, 0xe3, 0xe5, 0xe7, 0xf1, 0x5b, 0x2e, 0x3C, 0x28, 0x2B, 0x21, 0x26, 0xe9, 0xea, 0xeb, 0xe8, 0xed, 0xee, 0xef, 0xec, 0xdf, 0x5d, 0x24, 0x2A, 0x29, 0x3B, 0x5e, 0x2D, 0x2F, 0xc2, 0xc4, 0xc0, 0xc1, 0xc3, 0xc5, 0xc7, 0xd1, 0xa6, 0x2c, 0x25, 0x5F, 0x3E, 0x3F, 0xf8, 0xc9, 0xca, 0xcb, 0xc8, 0xcd, 0xce, 0xcf, 0xcc, 0x60, 0x3A, 0x23, 0x40, 0x27, 0x3D, 0x22,
        0xd8, 0x61, 0x62, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0xab, 0xbb, 0xf0, 0xfd, 0xde, 0xb1, 0xb0, 0x6A, 0x6B, 0x6C, 0x6D, 0x6E, 0x6F, 0x70, 0x71, 0x72, 0xaa, 0xba, 0xe6, 0xb8, 0xc6, 0x80, 0xb5, 0x7e, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7A, 0xa1, 0xbf, 0xd0, 0xdd, 0xfe, 0xae, 0xa2, 0xa3, 0xa5, 0xb7, 0xd7, 0xa7, 0xb6, 0xbc, 0xbd, 0xbe, 0xac, 0x7c, 0xaf, 0xa8, 0xb4, 0xa9,
        0x7b, 0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0xad, 0xf4, 0xf6, 0xf2, 0xf3, 0xf5, 0x7d, 0x4A, 0x4B, 0x4C, 0x4D, 0x4E, 0x4F, 0x50, 0x51, 0x52, 0xb9, 0xfb, 0xfc, 0xf9, 0xfa, 0xff, 0x5c, 0xf7, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58, 0x59, 0x5A, 0xb2, 0xd4, 0xd6, 0xd2, 0xd3, 0xd5, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0xb3, 0xdb, 0xdc, 0xd9, 0xda, 0xa0
    };
    final static private int [][] BufferAddrMap = {
        {0x4040,0xC150	,0xC260	,0xC3F0	,0xC540	,0xC650	,0xC760	,0xC8F0	,0x4A40	,0x4B50	,0x4C60	,0x4DF0	,0x4F40	,0x5050	,0xD160	,0xD2F0	,0xD440	,0xD550	,0xD660	,0xD7F0	,0xD940	,0x5A50	,0x5B60	,0x5CF0},
        {0x40C1,0xC1D1	,0xC261	,0xC3F1	,0xC5C1	,0xC6D1	,0xC761	,0xC8F1	,0x4AC1	,0x4BD1	,0x4C61	,0x4DF1	,0x4FC1	,0x50D1	,0xD161	,0xD2F1	,0xD4C1	,0xD5D1	,0xD661	,0xD7F1	,0xD9C1	,0x5AD1	,0x5B61	,0x5CF1},
        {0x40C2,0xC1D2	,0xC2E2	,0xC3F2	,0xC5C2	,0xC6D2	,0xC7E2	,0xC8F2	,0x4AC2	,0x4BD2	,0x4CE2	,0x4DF2	,0x4FC2	,0x50D2	,0xD1E2	,0xD2F2	,0xD4C2	,0xD5D2	,0xD6E2	,0xD7F2	,0xD9C2	,0x5AD2	,0x5BE2	,0x5CF2},
        {0x40C3,0xC1D3	,0xC2E3	,0xC3F3	,0xC5C3	,0xC6D3	,0xC7E3	,0xC8F3	,0x4AC3	,0x4BD3	,0x4CE3	,0x4DF3	,0x4FC3	,0x50D3	,0xD1E3	,0xD2F3	,0xD4C3	,0xD5D3	,0xD6E3	,0xD7F3	,0xD9C3	,0x5AD3	,0x5BE3	,0x5CF3},
        {0x40C4,0xC1D4	,0xC2E4	,0xC3F4	,0xC5C4	,0xC6D4	,0xC7E4	,0xC8F4	,0x4AC4	,0x4BD4	,0x4CE4	,0x4DF4	,0x4FC4	,0x50D4	,0xD1E4	,0xD2F4	,0xD4C4	,0xD5D4	,0xD6E4	,0xD7F4	,0xD9C4	,0x5AD4	,0x5BE4	,0x5CF4},
        {0x40C5,0xC1D5	,0xC2E5	,0xC3F5	,0xC5C5	,0xC6D5	,0xC7E5	,0xC8F5	,0x4AC5	,0x4BD5	,0x4CE5	,0x4DF5	,0x4FC5	,0x50D5	,0xD1E5	,0xD2F5	,0xD4C5	,0xD5D5	,0xD6E5	,0xD7F5	,0xD9C5	,0x5AD5	,0x5BE5	,0x5CF5},
        {0x40C6,0xC1D6	,0xC2E6	,0xC3F6	,0xC5C6	,0xC6D6	,0xC7E6	,0xC8F6	,0x4AC6	,0x4BD6	,0x4CE6	,0x4DF6	,0x4FC6	,0x50D6	,0xD1E6	,0xD2F6	,0xD4C6	,0xD5D6	,0xD6E6	,0xD7F6	,0xD9C6	,0x5AD6	,0x5BE6	,0x5CF6},
        {0x40C7,0xC1D7	,0xC2E7	,0xC3F7	,0xC5C7	,0xC6D7	,0xC7E7	,0xC8F7	,0x4AC7	,0x4BD7	,0x4CE7	,0x4DF7	,0x4FC7	,0x50D7	,0xD1E7	,0xD2F7	,0xD4C7	,0xD5D7	,0xD6E7	,0xD7F7	,0xD9C7	,0x5AD7	,0x5BE7	,0x5CF7},
        {0x40C8,0xC1D8	,0xC2E8	,0xC3F8	,0xC5C8	,0xC6D8	,0xC7E8	,0xC8F8	,0x4AC8	,0x4BD8	,0x4CE8	,0x4DF8	,0x4FC8	,0x50D8	,0xD1E8	,0xD2F8	,0xD4C8	,0xD5D8	,0xD6E8	,0xD7F8	,0xD9C8	,0x5AD8	,0x5BE8	,0x5CF8},
        {0x40C9,0xC1D9	,0xC2E9	,0xC3F9	,0xC5C9	,0xC6D9	,0xC7E9	,0xC8F9	,0x4AC9	,0x4BD9	,0x4CE9	,0x4DF9	,0x4FC9	,0x50D9	,0xD1E9	,0xD2F9	,0xD4C9	,0xD5D9	,0xD6E9	,0xD7F9	,0xD9C9	,0x5AD9	,0x5BE9	,0x5CF9},
        {0x404A,0xC15A	,0xC26A	,0xC37A	,0xC54A	,0xC65A	,0xC76A	,0xC87A	,0x4A4A	,0x4B5A	,0x4C6A	,0x4D7A	,0x4F4A	,0x505A	,0xD16A	,0xD27A	,0xD44A	,0xD55A	,0xD66A	,0xD77A	,0xD94A	,0x5A5A	,0x5B6A	,0x5C7A},
        {0x404B,0xC15B	,0xC26B	,0xC37B	,0xC54B	,0xC65B	,0xC76B	,0xC87B	,0x4A4B	,0x4B5B	,0x4C6B	,0x4D7B	,0x4F4B	,0x505B	,0xD16B	,0xD27B	,0xD44B	,0xD55B	,0xD66B	,0xD77B	,0xD94B	,0x5A5B	,0x5B6B	,0x5C7B},
        {0x404C,0xC15C	,0xC26C	,0xC37C	,0xC54C	,0xC65C	,0xC76C	,0xC87C	,0x4A4C	,0x4B5C	,0x4C6C	,0x4D7C	,0x4F4C	,0x505C	,0xD16C	,0xD27C	,0xD44C	,0xD55C	,0xD66C	,0xD77C	,0xD94C	,0x5A5C	,0x5B6C	,0x5C7C},
        {0x404D,0xC15D	,0xC26D	,0xC37D	,0xC54D	,0xC65D	,0xC76D	,0xC87D	,0x4A4D	,0x4B5D	,0x4C6D	,0x4D7D	,0x4F4D	,0x505D	,0xD16D	,0xD27D	,0xD44D	,0xD55D	,0xD66D	,0xD77D	,0xD94D	,0x5A5D	,0x5B6D	,0x5C7D},
        {0x404E,0xC15E	,0xC26E	,0xC37E	,0xC54E	,0xC65E	,0xC76E	,0xC87E	,0x4A4E	,0x4B5E	,0x4C6E	,0x4D7E	,0x4F4E	,0x505E	,0xD16E	,0xD27E	,0xD44E	,0xD55E	,0xD66E	,0xD77E	,0xD94E	,0x5A5E	,0x5B6E	,0x5C7E},
        {0x404F,0xC15F	,0xC26F	,0xC37F	,0xC54F	,0xC65F	,0xC76F	,0xC87F	,0x4A4F	,0x4B5F	,0x4C6F	,0x4D7F	,0x4F4F	,0x505F	,0xD16F	,0xD27F	,0xD44F	,0xD55F	,0xD66F	,0xD77F	,0xD94F	,0x5A5F	,0x5B6F	,0x5C7F},
        {0x4050,0xC160	,0xC2F0	,0xC440	,0xC550	,0xC660	,0xC7F0	,0xC940	,0x4A50	,0x4B60	,0x4CF0	,0x4E40	,0x4F50	,0x5060	,0xD1F0	,0xD340	,0xD450	,0xD560	,0xD6F0	,0xD840	,0xD950	,0x5A60	,0x5BF0	,0x5D40},
        {0x40D1,0xC161	,0xC2F1	,0xC4C1	,0xC5D1	,0xC661	,0xC7F1	,0xC9C1	,0x4AD1	,0x4B61	,0x4CF1	,0x4EC1	,0x4FD1	,0x5061	,0xD1F1	,0xD3C1	,0xD4D1	,0xD561	,0xD6F1	,0xD8C1	,0xD9D1	,0x5A61	,0x5BF1	,0x5DC1},
        {0x40D2,0xC1E2	,0xC2F2	,0xC4C2	,0xC5D2	,0xC6E2	,0xC7F2	,0xC9C2	,0x4AD2	,0x4BE2	,0x4CF2	,0x4EC2	,0x4FD2	,0x50E2	,0xD1F2	,0xD3C2	,0xD4D2	,0xD5E2	,0xD6F2	,0xD8C2	,0xD9D2	,0x5AE2	,0x5BF2	,0x5DC2},
        {0x40D3,0xC1E3	,0xC2F3	,0xC4C3	,0xC5D3	,0xC6E3	,0xC7F3	,0xC9C3	,0x4AD3	,0x4BE3	,0x4CF3	,0x4EC3	,0x4FD3	,0x50E3	,0xD1F3	,0xD3C3	,0xD4D3	,0xD5E3	,0xD6F3	,0xD8C3	,0xD9D3	,0x5AE3	,0x5BF3	,0x5DC3},
        {0x40D4	,0xC1E4	,0xC2F4	,0xC4C4	,0xC5D4	,0xC6E4	,0xC7F4	,0xC9C4	,0x4AD4	,0x4BE4	,0x4CF4	,0x4EC4	,0x4FD4	,0x50E4	,0xD1F4	,0xD3C4	,0xD4D4	,0xD5E4	,0xD6F4	,0xD8C4	,0xD9D4	,0x5AE4	,0x5BF4	,0x5DC4},
        {0x40D5	,0xC1E5	,0xC2F5	,0xC4C5	,0xC5D5	,0xC6E5	,0xC7F5	,0xC9C5	,0x4AD5	,0x4BE5	,0x4CF5	,0x4EC5	,0x4FD5	,0x50E5	,0xD1F5	,0xD3C5	,0xD4D5	,0xD5E5	,0xD6F5	,0xD8C5	,0xD9D5	,0x5AE5	,0x5BF5	,0x5DC5},
        {0x40D6	,0xC1E6	,0xC2F6	,0xC4C6	,0xC5D6	,0xC6E6	,0xC7F6	,0xC9C6	,0x4AD6	,0x4BE6	,0x4CF6	,0x4EC6	,0x4FD6	,0x50E6	,0xD1F6	,0xD3C6	,0xD4D6	,0xD5E6	,0xD6F6	,0xD8C6	,0xD9D6	,0x5AE6	,0x5BF6	,0x5DC6},
        {0x40D7	,0xC1E7	,0xC2F7	,0xC4C7	,0xC5D7	,0xC6E7	,0xC7F7	,0xC9C7	,0x4AD7	,0x4BE7	,0x4CF7	,0x4EC7	,0x4FD7	,0x50E7	,0xD1F7	,0xD3C7	,0xD4D7	,0xD5E7	,0xD6F7	,0xD8C7	,0xD9D7	,0x5AE7	,0x5BF7	,0x5DC7},
        {0x40D8	,0xC1E8	,0xC2F8	,0xC4C8	,0xC5D8	,0xC6E8	,0xC7F8	,0xC9C8	,0x4AD8	,0x4BE8	,0x4CF8	,0x4EC8	,0x4FD8	,0x50E8	,0xD1F8	,0xD3C8	,0xD4D8	,0xD5E8	,0xD6F8	,0xD8C8	,0xD9D8	,0x5AE8	,0x5BF8	,0x5DC8},
        {0x40D9	,0xC1E9	,0xC2F9	,0xC4C9	,0xC5D9	,0xC6E9	,0xC7F9	,0xC9C9	,0x4AD9	,0x4BE9	,0x4CF9	,0x4EC9	,0x4FD9	,0x50E9	,0xD1F9	,0xD3C9	,0xD4D9	,0xD5E9	,0xD6F9	,0xD8C9	,0xD9D9	,0x5AE9	,0x5BF9	,0x5DC9},
        {0x405A	,0xC16A	,0xC27A	,0xC44A	,0xC55A	,0xC66A	,0xC77A	,0xC94A	,0x4A5A	,0x4B6A	,0x4C7A	,0x4E4A	,0x4F5A	,0x506A	,0xD17A	,0xD34A	,0xD45A	,0xD56A	,0xD67A	,0xD84A	,0xD95A	,0x5A6A	,0x5B7A	,0x5D4A},
        {0x405B	,0xC16B	,0xC27B	,0xC44B	,0xC55B	,0xC66B	,0xC77B	,0xC94B	,0x4A5B	,0x4B6B	,0x4C7B	,0x4E4B	,0x4F5B	,0x506B	,0xD17B	,0xD34B	,0xD45B	,0xD56B	,0xD67B	,0xD84B	,0xD95B	,0x5A6B	,0x5B7B	,0x5D4B},
        {0x405C	,0xC16C	,0xC27C	,0xC44C	,0xC55C	,0xC66C	,0xC77C	,0xC94C	,0x4A5C	,0x4B6C	,0x4C7C	,0x4E4C	,0x4F5C	,0x506C	,0xD17C	,0xD34C	,0xD45C	,0xD56C	,0xD67C	,0xD84C	,0xD95C	,0x5A6C	,0x5B7C	,0x5D4C},
        {0x405D	,0xC16D	,0xC27D	,0xC44D	,0xC55D	,0xC66D	,0xC77D	,0xC94D	,0x4A5D	,0x4B6D	,0x4C7D	,0x4E4D	,0x4F5D	,0x506D	,0xD17D	,0xD34D	,0xD45D	,0xD56D	,0xD67D	,0xD84D	,0xD95D	,0x5A6D	,0x5B7D	,0x5D4D},
        {0x405E	,0xC16E	,0xC27E	,0xC44E	,0xC55E	,0xC66E	,0xC77E	,0xC94E	,0x4A5E	,0x4B6E	,0x4C7E	,0x4E4E	,0x4F5E	,0x506E	,0xD17E	,0xD34E	,0xD45E	,0xD56E	,0xD67E	,0xD84E	,0xD95E	,0x5A6E	,0x5B7E	,0x5D4E},
        {0x405F	,0xC16F	,0xC27F	,0xC44F	,0xC55F	,0xC66F	,0xC77F	,0xC94F	,0x4A5F	,0x4B6F	,0x4C7F	,0x4E4F	,0x4F5F	,0x506F	,0xD17F	,0xD34F	,0xD45F	,0xD56F	,0xD67F	,0xD84F	,0xD95F	,0x5A6F	,0x5B7F	,0x5D4F},
        {0x4060	,0xC1F0	,0xC340	,0xC450	,0xC560	,0xC6F0	,0xC840	,0xC950	,0x4A60	,0x4BF0	,0x4D40	,0x4E50	,0x4F60	,0x50F0	,0xD240	,0xD350	,0xD460	,0xD5F0	,0xD740	,0xD850	,0xD960	,0x5AF0	,0x5C40	,0x5D50},
        {0x4061	,0xC1F1	,0xC3C1	,0xC4D1	,0xC561	,0xC6F1	,0xC8C1	,0xC9D1	,0x4A61	,0x4BF1	,0x4DC1	,0x4ED1	,0x4F61	,0x50F1	,0xD2C1	,0xD3D1	,0xD461	,0xD5F1	,0xD7C1	,0xD8D1	,0xD961	,0x5AF1	,0x5CC1	,0x5DD1},
        {0x40E2	,0xC1F2	,0xC3C2	,0xC4D2	,0xC5E2	,0xC6F2	,0xC8C2	,0xC9D2	,0x4AE2	,0x4BF2	,0x4DC2	,0x4ED2	,0x4FE2	,0x50F2	,0xD2C2	,0xD3D2	,0xD4E2	,0xD5F2	,0xD7C2	,0xD8D2	,0xD9E2	,0x5AF2	,0x5CC2	,0x5DD2},
        {0x40E3	,0xC1F3	,0xC3C3	,0xC4D3	,0xC5E3	,0xC6F3	,0xC8C3	,0xC9D3	,0x4AE3	,0x4BF3	,0x4DC3	,0x4ED3	,0x4FE3	,0x50F3	,0xD2C3	,0xD3D3	,0xD4E3	,0xD5F3	,0xD7C3	,0xD8D3	,0xD9E3	,0x5AF3	,0x5CC3	,0x5DD3},
        {0x40E4	,0xC1F4	,0xC3C4	,0xC4D4	,0xC5E4	,0xC6F4	,0xC8C4	,0xC9D4	,0x4AE4	,0x4BF4	,0x4DC4	,0x4ED4	,0x4FE4	,0x50F4	,0xD2C4	,0xD3D4	,0xD4E4	,0xD5F4	,0xD7C4	,0xD8D4	,0xD9E4	,0x5AF4	,0x5CC4	,0x5DD4},
        {0x40E5	,0xC1F5	,0xC3C5	,0xC4D5	,0xC5E5	,0xC6F5	,0xC8C5	,0xC9D5	,0x4AE5	,0x4BF5	,0x4DC5	,0x4ED5	,0x4FE5	,0x50F5	,0xD2C5	,0xD3D5	,0xD4E5	,0xD5F5	,0xD7C5	,0xD8D5	,0xD9E5	,0x5AF5	,0x5CC5	,0x5DD5},
        {0x40E6	,0xC1F6	,0xC3C6	,0xC4D6	,0xC5E6	,0xC6F6	,0xC8C6	,0xC9D6	,0x4AE6	,0x4BF6	,0x4DC6	,0x4ED6	,0x4FE6	,0x50F6	,0xD2C6	,0xD3D6	,0xD4E6	,0xD5F6	,0xD7C6	,0xD8D6	,0xD9E6	,0x5AF6	,0x5CC6	,0x5DD6},
        {0x40E7	,0xC1F7	,0xC3C7	,0xC4D7	,0xC5E7	,0xC6F7	,0xC8C7	,0xC9D7	,0x4AE7	,0x4BF7	,0x4DC7	,0x4ED7	,0x4FE7	,0x50F7	,0xD2C7	,0xD3D7	,0xD4E7	,0xD5F7	,0xD7C7	,0xD8D7	,0xD9E7	,0x5AF7	,0x5CC7	,0x5DD7},
        {0x40E8	,0xC1F8	,0xC3C8	,0xC4D8	,0xC5E8	,0xC6F8	,0xC8C8	,0xC9D8	,0x4AE8	,0x4BF8	,0x4DC8	,0x4ED8	,0x4FE8	,0x50F8	,0xD2C8	,0xD3D8	,0xD4E8	,0xD5F8	,0xD7C8	,0xD8D8	,0xD9E8	,0x5AF8	,0x5CC8	,0x5DD8},
        {0x40E9	,0xC1F9	,0xC3C9	,0xC4D9	,0xC5E9	,0xC6F9	,0xC8C9	,0xC9D9	,0x4AE9	,0x4BF9	,0x4DC9	,0x4ED9	,0x4FE9	,0x50F9	,0xD2C9	,0xD3D9	,0xD4E9	,0xD5F9	,0xD7C9	,0xD8D9	,0xD9E9	,0x5AF9	,0x5CC9	,0x5DD9},
        {0x406A	,0xC17A	,0xC34A	,0xC45A	,0xC56A	,0xC67A	,0xC84A	,0xC95A	,0x4A6A	,0x4B7A	,0x4D4A	,0x4E5A	,0x4F6A	,0x507A	,0xD24A	,0xD35A	,0xD46A	,0xD57A	,0xD74A	,0xD85A	,0xD96A	,0x5A7A	,0x5C4A	,0x5D5A},
        {0x406B	,0xC17B	,0xC34B	,0xC45B	,0xC56B	,0xC67B	,0xC84B	,0xC95B	,0x4A6B	,0x4B7B	,0x4D4B	,0x4E5B	,0x4F6B	,0x507B	,0xD24B	,0xD35B	,0xD46B	,0xD57B	,0xD74B	,0xD85B	,0xD96B	,0x5A7B	,0x5C4B	,0x5D5B},
        {0x406C	,0xC17C	,0xC34C	,0xC45C	,0xC56C	,0xC67C	,0xC84C	,0xC95C	,0x4A6C	,0x4B7C	,0x4D4C	,0x4E5C	,0x4F6C	,0x507C	,0xD24C	,0xD35C	,0xD46C	,0xD57C	,0xD74C	,0xD85C	,0xD96C	,0x5A7C	,0x5C4C	,0x5D5C},
        {0x406D	,0xC17D	,0xC34D	,0xC45D	,0xC56D	,0xC67D	,0xC84D	,0xC95D	,0x4A6D	,0x4B7D	,0x4D4D	,0x4E5D	,0x4F6D	,0x507D	,0xD24D	,0xD35D	,0xD46D	,0xD57D	,0xD74D	,0xD85D	,0xD96D	,0x5A7D	,0x5C4D	,0x5D5D},
        {0x406E	,0xC17E	,0xC34E	,0xC45E	,0xC56E	,0xC67E	,0xC84E	,0xC95E	,0x4A6E	,0x4B7E	,0x4D4E	,0x4E5E	,0x4F6E	,0x507E	,0xD24E	,0xD35E	,0xD46E	,0xD57E	,0xD74E	,0xD85E	,0xD96E	,0x5A7E	,0x5C4E	,0x5D5E},
        {0x406F	,0xC17F	,0xC34F	,0xC45F	,0xC56F	,0xC67F	,0xC84F	,0xC95F	,0x4A6F	,0x4B7F	,0x4D4F	,0x4E5F	,0x4F6F	,0x507F	,0xD24F	,0xD35F	,0xD46F	,0xD57F	,0xD74F	,0xD85F	,0xD96F	,0x5A7F	,0x5C4F	,0x5D5F},
        {0x40F0	,0xC240	,0xC350	,0xC460	,0xC5F0	,0xC740	,0xC850	,0xC960	,0x4AF0	,0x4C40	,0x4D50	,0x4E60	,0x4FF0	,0xD140	,0xD250	,0xD360	,0xD4F0	,0xD640	,0xD750	,0xD860	,0xD9F0	,0x5B40	,0x5C50	,0x5D60},
        {0x40F1	,0xC2C1	,0xC3D1	,0xC461	,0xC5F1	,0xC7C1	,0xC8D1	,0xC961	,0x4AF1	,0x4CC1	,0x4DD1	,0x4E61	,0x4FF1	,0xD1C1	,0xD2D1	,0xD361	,0xD4F1	,0xD6C1	,0xD7D1	,0xD861	,0xD9F1	,0x5BC1	,0x5CD1	,0x5D61},
        {0x40F2	,0xC2C2	,0xC3D2	,0xC4E2	,0xC5F2	,0xC7C2	,0xC8D2	,0xC9E2	,0x4AF2	,0x4CC2	,0x4DD2	,0x4EE2	,0x4FF2	,0xD1C2	,0xD2D2	,0xD3E2	,0xD4F2	,0xD6C2	,0xD7D2	,0xD8E2	,0xD9F2	,0x5BC2	,0x5CD2	,0x5DE2},
        {0x40F3	,0xC2C3	,0xC3D3	,0xC4E3	,0xC5F3	,0xC7C3	,0xC8D3	,0xC9E3	,0x4AF3	,0x4CC3	,0x4DD3	,0x4EE3	,0x4FF3	,0xD1C3	,0xD2D3	,0xD3E3	,0xD4F3	,0xD6C3	,0xD7D3	,0xD8E3	,0xD9F3	,0x5BC3	,0x5CD3	,0x5DE3},
        {0x40F4	,0xC2C4	,0xC3D4	,0xC4E4	,0xC5F4	,0xC7C4	,0xC8D4	,0xC9E4	,0x4AF4	,0x4CC4	,0x4DD4	,0x4EE4	,0x4FF4	,0xD1C4	,0xD2D4	,0xD3E4	,0xD4F4	,0xD6C4	,0xD7D4	,0xD8E4	,0xD9F4	,0x5BC4	,0x5CD4	,0x5DE4},
        {0x40F5	,0xC2C5	,0xC3D5	,0xC4E5	,0xC5F5	,0xC7C5	,0xC8D5	,0xC9E5	,0x4AF5	,0x4CC5	,0x4DD5	,0x4EE5	,0x4FF5	,0xD1C5	,0xD2D5	,0xD3E5	,0xD4F5	,0xD6C5	,0xD7D5	,0xD8E5	,0xD9F5	,0x5BC5	,0x5CD5	,0x5DE5},
        {0x40F6	,0xC2C6	,0xC3D6	,0xC4E6	,0xC5F6	,0xC7C6	,0xC8D6	,0xC9E6	,0x4AF6	,0x4CC6	,0x4DD6	,0x4EE6	,0x4FF6	,0xD1C6	,0xD2D6	,0xD3E6	,0xD4F6	,0xD6C6	,0xD7D6	,0xD8E6	,0xD9F6	,0x5BC6	,0x5CD6	,0x5DE6},
        {0x40F7	,0xC2C7	,0xC3D7	,0xC4E7	,0xC5F7	,0xC7C7	,0xC8D7	,0xC9E7	,0x4AF7	,0x4CC7	,0x4DD7	,0x4EE7	,0x4FF7	,0xD1C7	,0xD2D7	,0xD3E7	,0xD4F7	,0xD6C7	,0xD7D7	,0xD8E7	,0xD9F7	,0x5BC7	,0x5CD7	,0x5DE7},
        {0x40F8	,0xC2C8	,0xC3D8	,0xC4E8	,0xC5F8	,0xC7C8	,0xC8D8	,0xC9E8	,0x4AF8	,0x4CC8	,0x4DD8	,0x4EE8	,0x4FF8	,0xD1C8	,0xD2D8	,0xD3E8	,0xD4F8	,0xD6C8	,0xD7D8	,0xD8E8	,0xD9F8	,0x5BC8	,0x5CD8	,0x5DE8},
        {0x40F9	,0xC2C9	,0xC3D9	,0xC4E9	,0xC5F9	,0xC7C9	,0xC8D9	,0xC9E9	,0x4AF9	,0x4CC9	,0x4DD9	,0x4EE9	,0x4FF9	,0xD1C9	,0xD2D9	,0xD3E9	,0xD4F9	,0xD6C9	,0xD7D9	,0xD8E9	,0xD9F9	,0x5BC9	,0x5CD9	,0x5DE9},
        {0x407A	,0xC24A	,0xC35A	,0xC46A	,0xC57A	,0xC74A	,0xC85A	,0xC96A	,0x4A7A	,0x4C4A	,0x4D5A	,0x4E6A	,0x4F7A	,0xD14A	,0xD25A	,0xD36A	,0xD47A	,0xD64A	,0xD75A	,0xD86A	,0xD97A	,0x5B4A	,0x5C5A	,0x5D6A},
        {0x407B	,0xC24B	,0xC35B	,0xC46B	,0xC57B	,0xC74B	,0xC85B	,0xC96B	,0x4A7B	,0x4C4B	,0x4D5B	,0x4E6B	,0x4F7B	,0xD14B	,0xD25B	,0xD36B	,0xD47B	,0xD64B	,0xD75B	,0xD86B	,0xD97B	,0x5B4B	,0x5C5B	,0x5D6B},
        {0x407C	,0xC24C	,0xC35C	,0xC46C	,0xC57C	,0xC74C	,0xC85C	,0xC96C	,0x4A7C	,0x4C4C	,0x4D5C	,0x4E6C	,0x4F7C	,0xD14C	,0xD25C	,0xD36C	,0xD47C	,0xD64C	,0xD75C	,0xD86C	,0xD97C	,0x5B4C	,0x5C5C	,0x5D6C},
        {0x407D	,0xC24D	,0xC35D	,0xC46D	,0xC57D	,0xC74D	,0xC85D	,0xC96D	,0x4A7D	,0x4C4D	,0x4D5D	,0x4E6D	,0x4F7D	,0xD14D	,0xD25D	,0xD36D	,0xD47D	,0xD64D	,0xD75D	,0xD86D	,0xD97D	,0x5B4D	,0x5C5D	,0x5D6D},
        {0x407E	,0xC24E	,0xC35E	,0xC46E	,0xC57E	,0xC74E	,0xC85E	,0xC96E	,0x4A7E	,0x4C4E	,0x4D5E	,0x4E6E	,0x4F7E	,0xD14E	,0xD25E	,0xD36E	,0xD47E	,0xD64E	,0xD75E	,0xD86E	,0xD97E	,0x5B4E	,0x5C5E	,0x5D6E},
        {0x407F	,0xC24F	,0xC35F	,0xC46F	,0xC57F	,0xC74F	,0xC85F	,0xC96F	,0x4A7F	,0x4C4F	,0x4D5F	,0x4E6F	,0x4F7F	,0xD14F	,0xD25F	,0xD36F	,0xD47F	,0xD64F	,0xD75F	,0xD86F	,0xD97F	,0x5B4F	,0x5C5F	,0x5D6F},
        {0xC140	,0xC250	,0xC360	,0xC4F0	,0xC640	,0xC750	,0xC860	,0xC9F0	,0x4B40	,0x4C50	,0x4D60	,0x4EF0	,0x5040	,0xD150	,0xD260	,0xD3F0	,0xD540	,0xD650	,0xD760	,0xD8F0	,0x5A40	,0x5B50	,0x5C60	,0x5DF0},
        {0xC1C1	,0xC2D1	,0xC361	,0xC4F1	,0xC6C1	,0xC7D1	,0xC861	,0xC9F1	,0x4BC1	,0x4CD1	,0x4D61	,0x4EF1	,0x50C1	,0xD1D1	,0xD261	,0xD3F1	,0xD5C1	,0xD6D1	,0xD761	,0xD8F1	,0x5AC1	,0x5BD1	,0x5C61	,0x5DF1},
        {0xC1C2	,0xC2D2	,0xC3E2	,0xC4F2	,0xC6C2	,0xC7D2	,0xC8E2	,0xC9F2	,0x4BC2	,0x4CD2	,0x4DE2	,0x4EF2	,0x50C2	,0xD1D2	,0xD2E2	,0xD3F2	,0xD5C2	,0xD6D2	,0xD7E2	,0xD8F2	,0x5AC2	,0x5BD2	,0x5CE2	,0x5DF2},
        {0xC1C3	,0xC2D3	,0xC3E3	,0xC4F3	,0xC6C3	,0xC7D3	,0xC8E3	,0xC9F3	,0x4BC3	,0x4CD3	,0x4DE3	,0x4EF3	,0x50C3	,0xD1D3	,0xD2E3	,0xD3F3	,0xD5C3	,0xD6D3	,0xD7E3	,0xD8F3	,0x5AC3	,0x5BD3	,0x5CE3	,0x5DF3},
        {0xC1C4	,0xC2D4	,0xC3E4	,0xC4F4	,0xC6C4	,0xC7D4	,0xC8E4	,0xC9F4	,0x4BC4	,0x4CD4	,0x4DE4	,0x4EF4	,0x50C4	,0xD1D4	,0xD2E4	,0xD3F4	,0xD5C4	,0xD6D4	,0xD7E4	,0xD8F4	,0x5AC4	,0x5BD4	,0x5CE4	,0x5DF4},
        {0xC1C5	,0xC2D5	,0xC3E5	,0xC4F5	,0xC6C5	,0xC7D5	,0xC8E5	,0xC9F5	,0x4BC5	,0x4CD5	,0x4DE5	,0x4EF5	,0x50C5	,0xD1D5	,0xD2E5	,0xD3F5	,0xD5C5	,0xD6D5	,0xD7E5	,0xD8F5	,0x5AC5	,0x5BD5	,0x5CE5	,0x5DF5},
        {0xC1C6	,0xC2D6	,0xC3E6	,0xC4F6	,0xC6C6	,0xC7D6	,0xC8E6	,0xC9F6	,0x4BC6	,0x4CD6	,0x4DE6	,0x4EF6	,0x50C6	,0xD1D6	,0xD2E6	,0xD3F6	,0xD5C6	,0xD6D6	,0xD7E6	,0xD8F6	,0x5AC6	,0x5BD6	,0x5CE6	,0x5DF6},
        {0xC1C7	,0xC2D7	,0xC3E7	,0xC4F7	,0xC6C7	,0xC7D7	,0xC8E7	,0xC9F7	,0x4BC7	,0x4CD7	,0x4DE7	,0x4EF7	,0x50C7	,0xD1D7	,0xD2E7	,0xD3F7	,0xD5C7	,0xD6D7	,0xD7E7	,0xD8F7	,0x5AC7	,0x5BD7	,0x5CE7	,0x5DF7},
        {0xC1C8	,0xC2D8	,0xC3E8	,0xC4F8	,0xC6C8	,0xC7D8	,0xC8E8	,0xC9F8	,0x4BC8	,0x4CD8	,0x4DE8	,0x4EF8	,0x50C8	,0xD1D8	,0xD2E8	,0xD3F8	,0xD5C8	,0xD6D8	,0xD7E8	,0xD8F8	,0x5AC8	,0x5BD8	,0x5CE8	,0x5DF8},
        {0xC1C9	,0xC2D9	,0xC3E9	,0xC4F9	,0xC6C9	,0xC7D9	,0xC8E9	,0xC9F9	,0x4BC9	,0x4CD9	,0x4DE9	,0x4EF9	,0x50C9	,0xD1D9	,0xD2E9	,0xD3F9	,0xD5C9	,0xD6D9	,0xD7E9	,0xD8F9	,0x5AC9	,0x5BD9	,0x5CE9	,0x5DF9},
        {0xC14A	,0xC25A	,0xC36A	,0xC47A	,0xC64A	,0xC75A	,0xC86A	,0xC97A	,0x4B4A	,0x4C5A	,0x4D6A	,0x4E7A	,0x504A	,0xD15A	,0xD26A	,0xD37A	,0xD54A	,0xD65A	,0xD76A	,0xD87A	,0x5A4A	,0x5B5A	,0x5C6A	,0x5D7A},
        {0xC14B	,0xC25B	,0xC36B	,0xC47B	,0xC64B	,0xC75B	,0xC86B	,0xC97B	,0x4B4B	,0x4C5B	,0x4D6B	,0x4E7B	,0x504B	,0xD15B	,0xD26B	,0xD37B	,0xD54B	,0xD65B	,0xD76B	,0xD87B	,0x5A4B	,0x5B5B	,0x5C6B	,0x5D7B},
        {0xC14C	,0xC25C	,0xC36C	,0xC47C	,0xC64C	,0xC75C	,0xC86C	,0xC97C	,0x4B4C	,0x4C5C	,0x4D6C	,0x4E7C	,0x504C	,0xD15C	,0xD26C	,0xD37C	,0xD54C	,0xD65C	,0xD76C	,0xD87C	,0x5A4C	,0x5B5C	,0x5C6C	,0x5D7C},
        {0xC14D	,0xC25D	,0xC36D	,0xC47D	,0xC64D	,0xC75D	,0xC86D	,0xC97D	,0x4B4D	,0x4C5D	,0x4D6D	,0x4E7D	,0x504D	,0xD15D	,0xD26D	,0xD37D	,0xD54D	,0xD65D	,0xD76D	,0xD87D	,0x5A4D	,0x5B5D	,0x5C6D	,0x5D7D},
        {0xC14E	,0xC25E	,0xC36E	,0xC47E	,0xC64E	,0xC75E	,0xC86E	,0xC97E	,0x4B4E	,0x4C5E	,0x4D6E	,0x4E7E	,0x504E	,0xD15E	,0xD26E	,0xD37E	,0xD54E	,0xD65E	,0xD76E	,0xD87E	,0x5A4E	,0x5B5E	,0x5C6E	,0x5D7E},
        {0xC14F	,0xC25F	,0xC36F	,0xC47F	,0xC64F	,0xC75F	,0xC86F	,0xC97F	,0x4B4F	,0x4C5F	,0x4D6F	,0x4E7F	,0x504F	,0xD15F	,0xD26F	,0xD37F	,0xD54F	,0xD65F	,0xD76F	,0xD87F	,0x5A4F	,0x5B5F	,0x5C6F	,0x5D7F},
    };

    final static private TN_StateChangeInfo[] gTN_StateChangeInfos = {
        new TN_StateChangeInfo(IBmStates.IBMS_Orders, Transitions.Exit, IBmActions.IBMA_OrdersParse),
        new TN_StateChangeInfo(IBmStates.IBMS_OrdersEx, Transitions.Exit, IBmActions.IBMA_OrdersParse),
        new TN_StateChangeInfo(IBmStates.IBMS_CommandEndEx, Transitions.Exit, IBmActions.IBMA_ParseData),
        new TN_StateChangeInfo(IBmStates.IBMS_CommandEnd, Transitions.Exit, IBmActions.IBMA_ParseData)
    };

    final static private TN_StateEventInfo[] gTN_StateEventInfos = {
        //	                   previous IBMState, CharFrom, CharTo, Action, Cur State
        new TN_StateEventInfo(IBmStates.IBMS_Ground, IBMCommand3270.IBMCm_Write, IBMCommand3270.IBMCm_Write, IBmActions.IBMA_ParseCommand, IBmStates.IBMS_Command),
        new TN_StateEventInfo(IBmStates.IBMS_Ground, IBMCommand3270.IBMCm_ReadBuffer, IBMCommand3270.IBMCm_ReadBuffer, IBmActions.IBMA_ParseCommand, IBmStates.IBMS_Command),
        new TN_StateEventInfo(IBmStates.IBMS_Ground, IBMCommand3270.IBMCm_EraseWrite, IBMCommand3270.IBMCm_EraseWrite, IBmActions.IBMA_ParseCommand,IBmStates.IBMS_Command),
        new TN_StateEventInfo(IBmStates.IBMS_Orders, '\u0000', '\u00FF', IBmActions.IBMA_RecordData, IBmStates.IBMS_CommandEndEx),
        new TN_StateEventInfo(IBmStates.IBMS_OrdersEx, '\u0000', '\u00FF', IBmActions.IBMA_RecordData, IBmStates.IBMS_CommandEndEx),
        new TN_StateEventInfo(IBmStates.IBMS_CommandEndEx, '\u0000', '\u00FF', IBmActions.IBMA_RecordData, IBmStates.IBMS_CommandEndEx),
        new TN_StateEventInfo(IBmStates.IBMS_Anywhere, '\u00EF', '\u00EF', IBmActions.IBMA_None, IBmStates.IBMS_Ground),
        new TN_StateEventInfo(IBmStates.IBMS_Anywhere, '\u00FF', '\u00FF', IBmActions.IBMA_None, IBmStates.IBMS_Ground),
    };

    //The valuse are the same with TE-C++
    public static final int IBMKEY_NONE = -1;
    public static final int IBMKEY_ATTN = ServerKeyEvent.TN_KEYCODE_ATTN;
    public static final int IBMKEY_LEFTDELETE = ServerKeyEvent.TN_KEYCODE_LEFTDELETE;
    public static final int IBMKEY_PREV = ServerKeyEvent.TN_KEYCODE_PREV;
    public static final int IBMKEY_CLR = ServerKeyEvent.TN_KEYCODE_CLR;
    public static final int IBMKEY_CLREOF = ServerKeyEvent.TN_KEYCODE_CLREOF;
    public static final int IBMKEY_DEL = ServerKeyEvent.TN_KEYCODE_DEL;
    public static final int IBMKEY_DUP = ServerKeyEvent.TN_KEYCODE_DUP;
    public static final int IBMKEY_ENTER = ServerKeyEvent.TN_KEYCODE_ENTER;
    public static final int IBMKEY_ERINPUT = ServerKeyEvent.TN_KEYCODE_ERINPUT;
    public static final int IBMKEY_FBEGIN = ServerKeyEvent.TN_KEYCODE_FBEGIN;
    public static final int IBMKEY_FEND = ServerKeyEvent.TN_KEYCODE_FEND;
    public static final int IBMKEY_FMARK = ServerKeyEvent.TN_KEYCODE_FMARK;
    public static final int IBMKEY_LAST = ServerKeyEvent.TN_KEYCODE_LAST;
    public static final int IBMKEY_HOME = ServerKeyEvent.TN_KEYCODE_HOME;
    public static final int IBMKEY_INS = ServerKeyEvent.TN_KEYCODE_INS;
    public static final int IBMKEY_NEWLINE = ServerKeyEvent.TN_KEYCODE_NEWLINE;
    public static final int IBMKEY_RESET = ServerKeyEvent.TN_KEYCODE_RESET;
    public static final int IBMKEY_ROLDN = ServerKeyEvent.TN_KEYCODE_ROLDN;
    public static final int IBMKEY_ROLUP = ServerKeyEvent.TN_KEYCODE_ROLUP;
    public static final int IBMKEY_SYSRQ = ServerKeyEvent.TN_KEYCODE_SYSRQ;
    public static final int IBMKEY_NEXT = ServerKeyEvent.TN_KEYCODE_NEXT;
    public static final int IBMKEY_LEFT = ServerKeyEvent.TN_KEYCODE_LEFT;
    public static final int IBMKEY_RIGHT = ServerKeyEvent.TN_KEYCODE_RIGHT;
    public static final int IBMKEY_UP = ServerKeyEvent.TN_KEYCODE_UP;
    public static final int IBMKEY_DOWN = ServerKeyEvent.TN_KEYCODE_DOWN;
    public static final int IBMKEY_PA1 = 108;
    public static final int IBMKEY_PA2 = 110;
    public static final int IBMKEY_PA3 = 107;
    public static final int IBMKEY_F1 = ServerKeyEvent.FUN_KEYCODE_F1;
    public static final int IBMKEY_F2 = ServerKeyEvent.FUN_KEYCODE_F2;
    public static final int IBMKEY_F3 = ServerKeyEvent.FUN_KEYCODE_F3;
    public static final int IBMKEY_F4 = ServerKeyEvent.FUN_KEYCODE_F4;
    public static final int IBMKEY_F5 = ServerKeyEvent.FUN_KEYCODE_F5;
    public static final int IBMKEY_F6 = ServerKeyEvent.FUN_KEYCODE_F6;
    public static final int IBMKEY_F7 = ServerKeyEvent.FUN_KEYCODE_F7;
    public static final int IBMKEY_F8 = ServerKeyEvent.FUN_KEYCODE_F8;
    public static final int IBMKEY_F9 = ServerKeyEvent.FUN_KEYCODE_F9;
    public static final int IBMKEY_F10 = ServerKeyEvent.FUN_KEYCODE_F10;
    public static final int IBMKEY_F11 = ServerKeyEvent.FUN_KEYCODE_F11;
    public static final int IBMKEY_F12 = ServerKeyEvent.FUN_KEYCODE_F12;
    public static final int IBMKEY_F13 = ServerKeyEvent.FUN_KEYCODE_F13;
    public static final int IBMKEY_F14 = ServerKeyEvent.FUN_KEYCODE_F14;
    public static final int IBMKEY_F15 = ServerKeyEvent.FUN_KEYCODE_F15;
    public static final int IBMKEY_F16 = ServerKeyEvent.FUN_KEYCODE_F16;
    public static final int IBMKEY_F17 = ServerKeyEvent.FUN_KEYCODE_F17;
    public static final int IBMKEY_F18 = ServerKeyEvent.FUN_KEYCODE_F18;
    public static final int IBMKEY_F19 = ServerKeyEvent.FUN_KEYCODE_F19;
    public static final int IBMKEY_F20 = ServerKeyEvent.FUN_KEYCODE_F20;
    public static final int IBMKEY_F21 = ServerKeyEvent.FUN_KEYCODE_F21;
    public static final int IBMKEY_F22 = ServerKeyEvent.FUN_KEYCODE_F22;
    public static final int IBMKEY_F23 = ServerKeyEvent.FUN_KEYCODE_F23;
    public static final int IBMKEY_F24 = ServerKeyEvent.FUN_KEYCODE_F24;

    final char DBCS_LEADING_AND_ENDING = 0x6f;
    //AID Code Value
    //Todo: To confirm 3270 has auto enter or not? i think no and should remove usage from ENT_VAL
    final char ENT_VAL = 0xF1;                        //Enter
    final char ENT_VAL_3270 = 0x7d;                   //Enter
    final char ATTN_3270 = 0x02;
    final char PF1_3270 = 0xF1;
    final char PF2_3270 = 0xF2;
    final char PF3_3270 = 0xF3;
    final char PF4_3270 = 0xF4;
    final char PF5_3270 = 0xF5;
    final char PF6_3270 = 0xF6;
    final char PF7_3270 = 0xF7;
    final char PF8_3270 = 0xF8;
    final char PF9_3270 = 0xF9;
    final char PF10_3270 = 0x7a;
    final char PF11_3270 = 0x7b;
    final char PF12_3270 = 0x7c;
    final char PF13_3270 = 0xc1;
    final char PF14_3270 = 0xc2;
    final char PF15_3270 = 0xc3;
    final char PF16_3270 = 0xc4;
    final char PF17_3270 = 0xc5;
    final char PF18_3270 = 0xc6;
    final char PF19_3270 = 0xc7;
    final char PF20_3270 = 0xc8;
    final char PF21_3270 = 0xc9;
    final char PF22_3270 = 0x4a;
    final char PF23_3270 = 0x4b;
    final char PF24_3270 = 0x4c;
    final char PA1_3270 = 0x6c;
    final char PA2_3270 = 0x6e;
    final char PA3_3270 = 0x6b;

    public static java.util.Map<Integer, Integer> gDefaultTN_3270KeyCodeMap_Taurus = new java.util.HashMap<>();
    public static java.util.Map<Integer, Integer> gDefaultTN_3270KeyCodeMap = new java.util.HashMap<>();
    private static java.util.Map<Integer, String> mTNKeyCodeText = new java.util.HashMap<>();
    static {
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_BACKSLASH, IBMKEY_ATTN);
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_DEL, IBMKEY_LEFTDELETE);
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_TAB, IBMKEY_NEXT);
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_GRAVE, IBMKEY_PREV); //KEYCODE_GRAVE or Blue + J, VK_BACKQUOTE in TE C++
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyMapUtility.encodePhyKeyCode(KeyEvent.KEYCODE_ESCAPE, true, false, false), IBMKEY_CLR);
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_F12, IBMKEY_CLREOF);//F12 or Blue + K
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_MOVE_END, IBMKEY_DEL);//End or Blue + backspace
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_MINUS, IBMKEY_DUP);   //KEYCODE_MINUS or Blue + N
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_ENTER, IBMKEY_ENTER);
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_LEFT_BRACKET, IBMKEY_ERINPUT); //Left BRACKET or Blue + E
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_RIGHT_BRACKET, IBMKEY_FMARK); //Right BRACKET or Blue + F
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_PAGE_DOWN, IBMKEY_HOME);//Page down or Blue + *
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_SEMICOLON, IBMKEY_INS); //Semicolon or Blue + R, need confirm
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyMapUtility.encodePhyKeyCode(KeyEvent.KEYCODE_N, true, false, false), IBMKEY_NEWLINE); //Ctrl + N
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_ESCAPE, IBMKEY_RESET);
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyMapUtility.encodePhyKeyCode(KeyEvent.KEYCODE_F6, false, true, false), IBMKEY_ROLUP);
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyMapUtility.encodePhyKeyCode(KeyEvent.KEYCODE_F7, false, true, false), IBMKEY_ROLDN);
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_F11, IBMKEY_SYSRQ); //F11 or Blue + L
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_DPAD_LEFT, IBMKEY_LEFT);
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_DPAD_RIGHT, IBMKEY_RIGHT);
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_DPAD_UP, IBMKEY_UP);
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_DPAD_DOWN, IBMKEY_DOWN);
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_COMMA, IBMKEY_PA1); //Comma or Blue + A
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_PERIOD, IBMKEY_PA2); //Period or Blue + B
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_APOSTROPHE, IBMKEY_PA3); //KEYCODE_APOSTROPHE or Blue + C
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_F1, IBMKEY_F1);
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_F2, IBMKEY_F2);
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_F3, IBMKEY_F3);
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_F4, IBMKEY_F4);
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_F5, IBMKEY_F5);
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_F6, IBMKEY_F6);
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_F7, IBMKEY_F7);
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_F8, IBMKEY_F8);
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_F9, IBMKEY_F9);
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_F10, IBMKEY_F10);
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyMapUtility.encodePhyKeyCode(KeyEvent.KEYCODE_1, false, true, false), IBMKEY_F11);
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyMapUtility.encodePhyKeyCode(KeyEvent.KEYCODE_2, false, true, false), IBMKEY_F12);
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyMapUtility.encodePhyKeyCode(KeyEvent.KEYCODE_3, false, true, false), IBMKEY_F13);
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyMapUtility.encodePhyKeyCode(KeyEvent.KEYCODE_4, false, true, false), IBMKEY_F14);
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyMapUtility.encodePhyKeyCode(KeyEvent.KEYCODE_5, false, true, false), IBMKEY_F15);
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyMapUtility.encodePhyKeyCode(KeyEvent.KEYCODE_6, false, true, false), IBMKEY_F16);
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyMapUtility.encodePhyKeyCode(KeyEvent.KEYCODE_7, false, true, false), IBMKEY_F17);
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyMapUtility.encodePhyKeyCode(KeyEvent.KEYCODE_8, false, true, false), IBMKEY_F18);
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyMapUtility.encodePhyKeyCode(KeyEvent.KEYCODE_9, false, true, false), IBMKEY_F19);
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyMapUtility.encodePhyKeyCode(KeyEvent.KEYCODE_0, false, true, false), IBMKEY_F20);
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyMapUtility.encodePhyKeyCode(KeyEvent.KEYCODE_F1, false, true, false), IBMKEY_F21);
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyMapUtility.encodePhyKeyCode(KeyEvent.KEYCODE_F2, false, true, false), IBMKEY_F22);
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyMapUtility.encodePhyKeyCode(KeyEvent.KEYCODE_F3, false, true, false), IBMKEY_F23);
        gDefaultTN_3270KeyCodeMap_Taurus.put(KeyMapUtility.encodePhyKeyCode(KeyEvent.KEYCODE_F4, false, true, false), IBMKEY_F24);

        gDefaultTN_3270KeyCodeMap.put(KeyEvent.KEYCODE_DEL, IBMKEY_LEFTDELETE);
        gDefaultTN_3270KeyCodeMap.put(KeyMapUtility.encodePhyKeyCode(KeyEvent.KEYCODE_DPAD_UP, false, true, false), IBMKEY_PREV);
        gDefaultTN_3270KeyCodeMap.put(KeyEvent.KEYCODE_TAB, IBMKEY_NEXT);
        gDefaultTN_3270KeyCodeMap.put(KeyMapUtility.encodePhyKeyCode(KeyEvent.KEYCODE_ESCAPE, true, false, false), IBMKEY_CLR);
        gDefaultTN_3270KeyCodeMap.put(KeyEvent.KEYCODE_FORWARD_DEL, IBMKEY_DEL);//Deletes characters ahead of the insertion point, unlike KEYCODE_DEL.
        gDefaultTN_3270KeyCodeMap.put(KeyEvent.KEYCODE_ENTER, IBMKEY_ENTER);
        gDefaultTN_3270KeyCodeMap.put(KeyEvent.KEYCODE_ESCAPE, IBMKEY_RESET);
        gDefaultTN_3270KeyCodeMap.put(KeyEvent.KEYCODE_DPAD_LEFT, IBMKEY_LEFT);
        gDefaultTN_3270KeyCodeMap.put(KeyEvent.KEYCODE_DPAD_RIGHT, IBMKEY_RIGHT);
        gDefaultTN_3270KeyCodeMap.put(KeyEvent.KEYCODE_DPAD_UP, IBMKEY_UP);
        gDefaultTN_3270KeyCodeMap.put(KeyEvent.KEYCODE_DPAD_DOWN, IBMKEY_DOWN);
        gDefaultTN_3270KeyCodeMap.put(KeyEvent.KEYCODE_F1, IBMKEY_F1);
        gDefaultTN_3270KeyCodeMap.put(KeyEvent.KEYCODE_F2, IBMKEY_F2);
        gDefaultTN_3270KeyCodeMap.put(KeyEvent.KEYCODE_F3, IBMKEY_F3);
        gDefaultTN_3270KeyCodeMap.put(KeyEvent.KEYCODE_F4, IBMKEY_F4);
        gDefaultTN_3270KeyCodeMap.put(KeyEvent.KEYCODE_F5, IBMKEY_F5);
        gDefaultTN_3270KeyCodeMap.put(KeyEvent.KEYCODE_F6, IBMKEY_F6);
        gDefaultTN_3270KeyCodeMap.put(KeyEvent.KEYCODE_F7, IBMKEY_F7);
        gDefaultTN_3270KeyCodeMap.put(KeyEvent.KEYCODE_F8, IBMKEY_F8);
        gDefaultTN_3270KeyCodeMap.put(KeyEvent.KEYCODE_F9, IBMKEY_F9);
        gDefaultTN_3270KeyCodeMap.put(KeyEvent.KEYCODE_F10, IBMKEY_F10);
        gDefaultTN_3270KeyCodeMap.put(KeyEvent.KEYCODE_F11, IBMKEY_F11);
        gDefaultTN_3270KeyCodeMap.put(KeyEvent.KEYCODE_F12, IBMKEY_F12);

        mTNKeyCodeText.put(IBMKEY_ATTN, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_ATTN));
        mTNKeyCodeText.put(IBMKEY_FBEGIN, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_FBEGIN));
        mTNKeyCodeText.put(IBMKEY_DEL, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_DEL));
        mTNKeyCodeText.put(IBMKEY_FEND, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_FEND));
        mTNKeyCodeText.put(IBMKEY_ERINPUT, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_ERINPUT));
        mTNKeyCodeText.put(IBMKEY_FMARK, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_FMARK));
        mTNKeyCodeText.put(IBMKEY_LAST, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_LAST));
        mTNKeyCodeText.put(IBMKEY_NEXT, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_NEXT));
        mTNKeyCodeText.put(IBMKEY_ROLDN, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_ROLDN));
        mTNKeyCodeText.put(IBMKEY_ROLUP, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_ROLUP));
        mTNKeyCodeText.put(IBMKEY_PREV, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_PREV));
        mTNKeyCodeText.put(IBMKEY_SYSRQ, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_SYSRQ));
        mTNKeyCodeText.put(IBMKEY_INS, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_INS));
        mTNKeyCodeText.put(IBMKEY_DUP, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_DUP));
        mTNKeyCodeText.put(IBMKEY_CLR, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_CLR));
        mTNKeyCodeText.put(IBMKEY_RESET, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_RESET));
        mTNKeyCodeText.put(IBMKEY_ENTER, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_ENTER));
        mTNKeyCodeText.put(IBMKEY_LEFT, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_LEFT));
        mTNKeyCodeText.put(IBMKEY_LEFTDELETE, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_LEFTDELETE));
        mTNKeyCodeText.put(IBMKEY_HOME, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_HOME));
        //mTNKeyCodeText.put(//IBMKEY_RECORD_BSP, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_RECORD_BSP));mTNKeyCodeText.put(IBMKEY_FMARK, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_FMARK));
        mTNKeyCodeText.put(IBMKEY_NEWLINE, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_NEWLINE));
        mTNKeyCodeText.put(IBMKEY_CLREOF, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_CLREOF));
        mTNKeyCodeText.put(IBMKEY_RIGHT, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_RIGHT));
        mTNKeyCodeText.put(IBMKEY_UP, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_UP));
        mTNKeyCodeText.put(IBMKEY_DOWN, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_DOWN));
        mTNKeyCodeText.put(IBMKEY_F1, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_F1));
        mTNKeyCodeText.put(IBMKEY_F2, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_F2));
        mTNKeyCodeText.put(IBMKEY_F3, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_F3));
        mTNKeyCodeText.put(IBMKEY_F4, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_F4));
        mTNKeyCodeText.put(IBMKEY_F5, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_F5));
        mTNKeyCodeText.put(IBMKEY_F6, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_F6));
        mTNKeyCodeText.put(IBMKEY_F7, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_F7));
        mTNKeyCodeText.put(IBMKEY_F8, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_F8));
        mTNKeyCodeText.put(IBMKEY_F9, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_F9));
        mTNKeyCodeText.put(IBMKEY_F10, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_F10));
        mTNKeyCodeText.put(IBMKEY_F11, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_F11));
        mTNKeyCodeText.put(IBMKEY_F12, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_F12));
        mTNKeyCodeText.put(IBMKEY_F13, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_F13));
        mTNKeyCodeText.put(IBMKEY_F14, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_F14));
        mTNKeyCodeText.put(IBMKEY_F15, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_F15));
        mTNKeyCodeText.put(IBMKEY_F16, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_F16));
        mTNKeyCodeText.put(IBMKEY_F17, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_F17));
        mTNKeyCodeText.put(IBMKEY_F18, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_F18));
        mTNKeyCodeText.put(IBMKEY_F19, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_F19));
        mTNKeyCodeText.put(IBMKEY_F20, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_F20));
        mTNKeyCodeText.put(IBMKEY_F21, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_F21));
        mTNKeyCodeText.put(IBMKEY_F22, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_F22));
        mTNKeyCodeText.put(IBMKEY_F23, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_F23));
        mTNKeyCodeText.put(IBMKEY_F24, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_F24));
        mTNKeyCodeText.put(IBMKEY_PA1, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_PA1));
        mTNKeyCodeText.put(IBMKEY_PA2, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_PA2));
        mTNKeyCodeText.put(IBMKEY_PA3, stdActivityRef.getCurrActivity().getResources().getString(R.string.IBMKEY_PA3));
    }
    public static String getServerKeyText(int nKeyCode) {
        return mTNKeyCodeText.get(nKeyCode);
    }

    //Members
    private java.util.Map<Integer, Integer> mTN3270KeyCodeMap = null;
    private byte PreviousChar; // Robin+ 2006.7.20 to support DBCS input
    private char mCurChar;
    private char mCurCommand;
    private char CurOrder;
    private char mCurWcc;
    private IBmStates mPreIBMState = IBMS_Ground;
    private IBmActions mLastEventAction;
    private ArrayList<Character> OrderBuffer = new ArrayList<>();
    private ArrayList<Character> DataBuffer = new ArrayList<>();
    private int mParsePanding = 0;
    private boolean bLock = false;
    private boolean bInsert = false;
    private boolean bDBCS = false;
    private AtomicInteger nBufX = new AtomicInteger();  //buffer address
    private AtomicInteger nBufY = new AtomicInteger(); //buffer address
    private AtomicInteger nICX = new AtomicInteger();
    private AtomicInteger nICY = new AtomicInteger(); //Insert cursor address, the address after user unlock keyboard or press home key
    int FieldStdX;
    int FieldStdY;
    private char cAttrib = 0;
    private CTNTag TNTag = new CTNTag();
    private CTNTag ProtectTNTag = new CTNTag();
    private tagField ActiveField = null;

    private int checkPadding() {
        int value = mParsePanding;

        if (mParsePanding <= 0)
            mParsePanding = 0;
        else
            mParsePanding--;
        return value;
    }

    private void preSetOrder(char order) {
        switch(order) {
            case EraseUnprotectedToAddress:
                mParsePanding = 2;
                break;
            case InsertCursor:
                break;
            case ModifyField:
                mParsePanding = 3;
                break;
            case ProgramTab:
            case RepeatToAddress:
                mParsePanding = 3;
                break;
            case SetAttribute:
                mParsePanding = 2;
                break;
            case SetBufferAddress:
                mParsePanding = 2;
                break;
            case StartField:
                mParsePanding = 1;
                break;
            case StartFieldExtended:
                break;
        }
    }

    private boolean getStateChangeAction(IBmStates ibmState, Transitions transition, AtomicReference<IBmActions> changeAction) {
        for (int idxStateChgInfo = 0; idxStateChgInfo < gTN_StateChangeInfos.length; idxStateChgInfo++) {
            TN_StateChangeInfo stateChgInfo = gTN_StateChangeInfos[idxStateChgInfo];
            if (ibmState == stateChgInfo.mIbmState && transition == stateChgInfo.mTransition) {
                changeAction.set(stateChgInfo.mAction);
                return true;
            }
        }
        return false;
    }

    private boolean getStateEventAction(IBmStates preIBMState, char curChar, AtomicReference<IBmStates> curIBMState, AtomicReference<IBmActions> curAction) {
        if (preIBMState == IBMS_Command) {
            if (mCurCommand == IBMCommand3270.IBMCm_Write || mCurCommand == IBMCommand3270.IBMCm_EraseWrite) {
                curIBMState.set(IBmStates.IBMS_CommandEndEx);
                curAction.set(IBmActions.IBMA_ParseWCC);
                return true;
            }
        }

        if (preIBMState == IBMS_Command || preIBMState == IBMS_CommandEndEx ) {
            switch(curChar) {
                case EraseUnprotectedToAddress:
                case InsertCursor:
                case ModifyField:
                case ProgramTab:
                case RepeatToAddress:
                case SetAttribute:
                case SetBufferAddress:
                case StartField:
                case StartFieldExtended:
                    preSetOrder(curChar);
                    curIBMState.set(IBMS_Orders);
                    curAction.set(IBmActions.IBMA_OrdersRecord);
                    return true;
            }
        }

        if (preIBMState == IBMS_Orders || preIBMState == IBMS_OrdersEx) {
            switch(curChar) {
                case EraseUnprotectedToAddress:
                case InsertCursor:
                case ModifyField:
                case ProgramTab:
                case RepeatToAddress:
                case SetAttribute:
                case SetBufferAddress:
                case StartField:
                case StartFieldExtended:
                    preSetOrder(curChar);
                    if (preIBMState == IBMS_Orders) {
                        curIBMState.set(IBMS_OrdersEx);
                    } else {
                        curIBMState.set(IBMS_Orders);
                    }
                    curAction.set(IBmActions.IBMA_OrdersRecord);
                    return true;
            }
        }

        for(int idxStateEvntInfo = 0; idxStateEvntInfo < gTN_StateEventInfos.length; idxStateEvntInfo++) {
            TN_StateEventInfo stateEvntInfo = gTN_StateEventInfos[idxStateEvntInfo];
            if (curChar >= stateEvntInfo.mCharFrom &&
                    curChar <= stateEvntInfo.mCharTo &&
                    (stateEvntInfo.mPreState == preIBMState || stateEvntInfo.mPreState == IBMS_Anywhere)) {

                curIBMState.set(stateEvntInfo.mState);
                curAction.set(stateEvntInfo.mAction);
                return true;
            }
        }

        return false;
    }

    private int getAidBufferAddress(int Y,int X) {
        return BufferAddrMap[X][Y];
    }

    private int getPosVal(int X, int Y) {
        return (Y * _cols) + X;
    }

    private boolean getFieldPosFirst(tagField field) {
        ProtectTNTag.toFirst();
        if (!ProtectTNTag.getCurr(field))
            return false;

        while (true) {
            if (!ProtectTNTag.toNext())
                break;
            tagField aFieldLook = new tagField();
            ProtectTNTag.getCurr(aFieldLook);
            if (getPosVal(aFieldLook.x, aFieldLook.y) < getPosVal(field.x, field.y)) {
                field.copy(aFieldLook);
            }
        }
        return true;
    }

    boolean getFieldPosNext(tagField Field) {
        tagField aField = new tagField();
        ProtectTNTag.toFirst();
        if (!ProtectTNTag.getCurr(aField)) {
            return false;
        }

        boolean find = false;
        if (getPosVal(aField.x, aField.y) > getPosVal(Field.x, Field.y)) {
            aField.copy(Field);
            find = true;
        }

        while(true) {
            if (!ProtectTNTag.toNext())
                break;

            tagField aFieldLook = new tagField();
            ProtectTNTag.getCurr(aFieldLook);

            if  (getPosVal(aFieldLook.x,aFieldLook.y) > getPosVal(Field.x, Field.y)) {
                if (find != true) {
                    aField.copy(aFieldLook);
                    find = true;
                } else {
                    if (getPosVal(aFieldLook.x, aFieldLook.y) < getPosVal(aField.x, aField.y))
                        aField.copy(aFieldLook);
                }
            }
        }
        if (find)
            Field.copy(aField);

        return find;
    }

    private char EBCDIC2ASCII(Character c) {
        char cRet = szEBCDIC[c];
        return cRet;
    }

    private char ASCII2EBCDIC(char c) {
        char cRet = 0xFF; //return 0xFF if no match
        for (int i = 64; i < 255; i++) {
            if (szEBCDIC[i] == c) {
                cRet = (char) i;
                break;
            }
        }
        return cRet;
    }

    /*-----------------------------------------------------------------------------
     -Purpose: insrt a character in screen and right move the reset content in the field
     -Param  : aField: target field; (x,y): coordinate
     -Return :
     -Remark :  Robin+ 2006.8.3
     TODO: InsertMapChar is not supported untill now
     -----------------------------------------------------------------------------*/
    private void insertChar(tagField aField, int x, int y) {
        int nRest = 0, i = 0;
        AtomicInteger nextXAtom = new AtomicInteger();
        AtomicInteger nextYAtom = new AtomicInteger();
        nextXAtom.set(x);
        nextYAtom.set(y);
        char PreChar = 0, CurChar = 0;
        cAttrib = AttribGrid[nBufY.get()][nBufX.get()];
        nRest = aField.nLen - diffPos(aField.x, aField.y, x, y);
        while (i < nRest) {
            CurChar = CharGrid[nextYAtom.get()][nextXAtom.get()];
            CharGrid[nextYAtom.get()][nextXAtom.get()] = PreChar;
            char asciiChar = EBCDIC2ASCII(PreChar);
            if(isScreenAttributeVisible(cAttrib)) {
                DrawChar(asciiChar, nextXAtom.get(), nextYAtom.get(), false, isUnderLine(cAttrib), false);
            }
            AttribGrid[nextYAtom.get()][nextXAtom.get()] = cAttrib;
            PreChar = CurChar;
            nextPos(nextXAtom, nextYAtom);
            i++;
        }
        CharGrid[y][x] = 0;
        AttribGrid[nextYAtom.get()][nextXAtom.get()] = 0;
    }

    /*-----------------------------------------------------------------------------
     -Purpose: Check if the poing (x,y) is any aField
     -Return : true: pt is in the field; false: pt is not in the field
     -Remark :
     -----------------------------------------------------------------------------*/
    private boolean ptInFields(int x, int y) {
        boolean bRet = false;
        boolean bValid = false;
        tagField aField = new tagField();
        TNTag.saveListPos();
        bValid = TNTag.toFirst();
        while (bValid) {
            TNTag.getCurr(aField);
            if (aField.ptInField(x, y)) {
                bRet = true;
                break;
            }
            if (!TNTag.toNext())
                break;
        }
        TNTag.restoreListPos();
        return bRet;
    }

    /*-----------------------------------------------------------------------------
        -Purpose: previous position before current coordinate
        -Param  : x: x coordinate; y: y coordinate
        -Return :
        -Remark :
        -----------------------------------------------------------------------------*/
    void prevPos(AtomicInteger x, AtomicInteger y) { //Previous coordinate
        int nValue = (y.get() * _cols + x.get() - 1);
        y.set(nValue / _cols);
        x.set(nValue % _cols);
    }

    /*-----------------------------------------------------------------------------
        -Purpose: the next position after current coordinate
        -Param  : *x: x coordinate; *y: y coordinate
        -Return :
        -Remark :
        -----------------------------------------------------------------------------*/
    void nextPos(AtomicInteger x, AtomicInteger y) {//Next coordinate
        int nValue = (y.get() * _cols + x.get() + 1);
        y.set(nValue / _cols);
        x.set(nValue % _cols);
    }

    int fillBuffZERO(int nStartX, int nStartY, int nEndX, int nEndY, byte[] szContent, int nLen) {
        AtomicInteger nStartXAtom = new AtomicInteger(nStartX);
        AtomicInteger nStartYAtom = new AtomicInteger(nStartY);
        AtomicInteger nEndXAtom = new AtomicInteger(nEndX);
        AtomicInteger nEndYAtom = new AtomicInteger(nEndY);
        prevPos(nEndXAtom, nEndYAtom);
        if  (getPosVal(nStartXAtom.get(), nStartYAtom.get()) > getPosVal(nEndXAtom.get(), nEndYAtom.get())) {
            return nLen;
        }

        while (nStartXAtom.get() != nEndXAtom.get() || nStartYAtom.get() != nEndYAtom.get()) {
            szContent[nLen++] = 0;
            nextPos(nStartXAtom, nEndYAtom);
        }// end of while
        return nLen;
    }

    /*-----------------------------------------------------------------------------
     -Purpose: get the coordinate of the last character of current field.
     -Param  : aField: the field to proces
     -       : x:     the pointer to return x coordinate
     -       : y:     the pointer to return y coordinate
     -       : bRes:   should we reserve a character position if field is Signed Numberic
     -Return :
     -Remark : In fact the last position of Signed Numeric field is reserved for '-'
     -----------------------------------------------------------------------------*/
    void getLastPos(tagField aField, AtomicInteger x, AtomicInteger y, boolean bRes) {
        if (!aField.valid()) {
            return;
        }

        int nLen = aField.nLen;
        if (aField.getShiftSpec() == 7 && bRes)
            nLen--;
        x.set((int) aField.x);
        y.set((int) aField.y);
        for (int i = 1; i < nLen; i++) {
            nextPos(x, y);
        }
    }

    private String convertFieldData(int nStartX, int nStartY, int nEndX, int nEndY, boolean bReplace) {
        StringBuilder sb = new StringBuilder();
        AtomicInteger nStartXAtom = new AtomicInteger(nStartX);
        AtomicInteger nStartYAtom = new AtomicInteger(nStartY);
        AtomicInteger nEndXAtom = new AtomicInteger(nEndX);
        AtomicInteger nEndYAtom = new AtomicInteger(nEndY);
        AtomicInteger nX1Atom = new AtomicInteger();
        AtomicInteger nY1Atom = new AtomicInteger();
        char[] szPC = new char[2];
        int nCount = 0;
        bDBCS = false;
        nextPos(nEndXAtom, nEndYAtom);

        while (nStartXAtom.get() != nEndXAtom.get() || nStartYAtom.get() != nEndYAtom.get()) {
            nX1Atom.set(nStartXAtom.get());
            nY1Atom.set(nStartYAtom.get());
            nextPos(nX1Atom, nY1Atom);

            // try to get two characters
            if (nX1Atom.get() == nEndXAtom.get() && nY1Atom.get() == nEndXAtom.get()) {
                // c is the last character
                szPC[1] = CharGrid[nStartYAtom.get()][nStartXAtom.get()];
                nCount = 1;
            }
            else {
                szPC[1] = CharGrid[nStartYAtom.get()][nStartXAtom.get()];
                szPC[0] = CharGrid[nY1Atom.get()][nX1Atom.get()];
                nCount = 2;
            }

            if (bReplace) {
                for (int i = 0; i < nCount; i++) {
                    if (szPC[i] == 0)
                        szPC[i] = ' ';
                }
            }
            /*
            //Todo:Handle DBCS
            if ((nCount == 2) && isDBCS(szPC[1], szPC[0]) && pc2Host(SysParam[tnSession].TELanguage, szPC)) {
                if (!bDBCS) {
                    szContent[nLen++] = 0x0e;
                    bDBCS = true;
                }
                szContent[nLen++] = szPC[1];
                szContent[nLen++] = szPC[0];
                nextPos(nStartXAtom, nStartYAtom); //Since I process 2 characters together, we should move forword
            }
            else */
            {
                if (bDBCS) {
                    sb.append(0x0f);
                    bDBCS = false;
                } else if (szPC[1]==0x1e) {
                    sb.append(0x1e);
                } else {
                    if (szPC[1] != 0)
                        sb.append(szPC[1]);
                    else
                        sb.append(0);
                }
            }
            nextPos(nStartXAtom, nStartYAtom);
        } // end of while

        if (bDBCS) {
            sb.append(0x0f);
            bDBCS = false;
        }
        return sb.toString();
    }

    private String buildAidPack(char cAID) {
        StringBuilder sb = new StringBuilder();
        int Addr = getAidBufferAddress(nBufY.get(), nBufX.get());
        sb.append(cAID);
        sb.append((char)((Addr & 0xFF00) / 256));
        sb.append((char)(Addr & 0x00FF));
        return sb.toString();
    }

    /*-----------------------------------------------------------------------------
     -Purpose: send data to host
     -----------------------------------------------------------------------------*/
    private void ibmSend(char cAID) {
        bLock = true;

        String szSend = "";
        if (ActiveField.valid()) {
            // Robin+ 2004.6.14 correct the coordinate
            int nLen = diffPos((int) ActiveField.x, (int) ActiveField.y, nBufX.get(), nBufY.get());
            if (nLen == ActiveField.nLen)
                prevPos(nBufX, nBufY);
        }

        szSend += buildAidPack(cAID);

        //build content
        {
            tagField aField = new tagField();
            TNTag.saveListPos();
            boolean bValid = TNTag.toFirst();
            while (bValid) {
                int x, y;
                AtomicInteger nEndX = new AtomicInteger();
                AtomicInteger nEndY = new AtomicInteger();
                AtomicInteger nLastX = new AtomicInteger();
                AtomicInteger nLastY = new AtomicInteger();

                TNTag.getCurr(aField);
                if (getBit(aField.cAttrib, 7)) {
                    x = aField.x;
                    y = aField.y;
                    getLastPos(aField, nLastX, nLastY, false);
                    getLastPos(aField, nEndX, nEndY, true);

                    int Addr = getAidBufferAddress(y, x);
                    szSend += (char) 0x11;
                    szSend += (char) ((Addr& 0xFF00) / 256);
                    szSend += (char) (Addr& 0x00FF);

                    //Trim
                    while (nEndX.get() >= x || nEndY.get() > y) {
                        if (CharGrid[nEndY.get()][nEndX.get()] != 0 && CharGrid[nEndY.get()][nEndX.get()] != 32)
                            break;
                        else
                            prevPos(nEndX, nEndY);
                    }

                    x = aField.x;
                    y = aField.y;

                    if (x >= nEndX.get() || nEndY.get() >= y) {
                        szSend += convertFieldData(x, y, nEndX.get(), nEndY.get(), true);
                    }
                }
                if (!TNTag.toNext())
                    break;
            }
            TNTag.restoreListPos();
        }

        szSend += (char) 0xff;
        szSend += (char) 0xef;
        DispatchMessage(this, szSend);
        //    bLock = FALSE;                Robin- 2004.6.14 lock keyboard untill host reset it
    }

    //action for Commands begin
    //IBMCommand3270.IBMCm_EraseWrite
    private void eraseDisplay() {
        bLock = false;
        bInsert = false;
        TNTag.initList();
        ProtectTNTag.initList();
        ActiveField = new tagField();
        cAttrib = 0;
        clearGrid();
        nICX.set(-1);
        nICY.set(-1);
        ViewClear();
        ViewPostInvalidate();
    }

    private void sendDataBuffer() {
        byte [] szContent = new byte[5600];
        int nSend = 0;
        int nAddr;
        int nStartBufX = 0;
        int nStartBufY = 0;
        AtomicInteger nEndXAtom = new AtomicInteger();
        AtomicInteger nEndYAtom = new AtomicInteger();

        nAddr = getAidBufferAddress(nBufY.get(), nBufX.get());
        szContent[nSend++] = 0x60;
        szContent[nSend++] = (byte) ((nAddr & 0xFF00) / 256);
        szContent[nSend++] = (byte) (nAddr & 0x00FF);

        tagField Field = new tagField();
        boolean result = getFieldPosFirst(Field);
        if (result) {
            nSend = fillBuffZERO(nStartBufX, nStartBufY, Field.x, Field.y, szContent, nSend);
            getLastPos(Field, nEndXAtom, nEndYAtom, false);

            szContent[nSend++] = 0x1d;
            szContent[nSend++] = Field.cAttrib;
            byte [] converData = convertFieldData((int)Field.x, (int)Field.y, nEndXAtom.get(), nEndYAtom.get(), false).getBytes();
            System.arraycopy(converData, 0, szContent, nSend, converData.length);
            nSend += converData.length;
            nextPos(nEndXAtom, nEndYAtom);
            nStartBufX = nEndXAtom.get();
            nStartBufY = nEndYAtom.get();
        }

        while(getFieldPosNext(Field) && result) {
            if (Field.nLen == 0)
                continue;

            nSend = fillBuffZERO(nStartBufX, nStartBufY,Field.x,Field.y,szContent, nSend);
            getLastPos(Field, nEndXAtom, nEndYAtom, false);
            szContent[nSend++] = 0x1d;
            szContent[nSend++] = Field.cAttrib;
            byte [] converData = convertFieldData(Field.x, Field.y, nEndXAtom.get(), nEndYAtom.get(), false).getBytes();
            System.arraycopy(converData, 0, szContent, nSend, converData.length);
            nSend += converData.length;
            nextPos(nEndXAtom, nEndYAtom);
            nStartBufX = nEndXAtom.get();
            nStartBufY = nEndYAtom.get();
        }

        nSend = fillBuffZERO(nStartBufX, nStartBufY, _cols - 1, _rows - 1,szContent, nSend);
        //send data
        szContent[nSend++] = 0x1d;
        szContent[nSend++] = 0x60;
        szContent[nSend++] = (byte) 0xff;
        szContent[nSend++] = (byte) 0xef;
        DispatchMessageRaw(this, szContent, nSend);
    }

    private void getBufferAddress(int value, AtomicInteger y, AtomicInteger x) {
        for(int col = 0; col < _cols; col++) {
            for(int row = 0 ; row <_rows ; row++) {
                if (BufferAddrMap[col][row] ==  value) {
                    y.set(row);
                    x.set(col);
                    return;
                }
            }
        }
    }

    private void getBufferPos(AtomicInteger xAtom, AtomicInteger yAtom) {
        xAtom.set(nBufX.get());
        yAtom.set(nBufY.get());
    }

    private void setBufferPos(int X,int Y){
        nBufX.set(X);
        nBufY.set(Y);
    }

    /*-----------------------------------------------------------------------------
     -Purpose: Is current ActiveField is a auto enter field?
     -Param  :
     -Return :
     -Remark : please refer to IBM document for more inf.
     -----------------------------------------------------------------------------*/
    private boolean isAutoEnt() {
        if (getBit(ActiveField.szFFW[1], 0))
            return true;
        else
            return false;
    }

    /*-----------------------------------------------------------------------------
     -Purpose: is current attribute need under line?
     -Param  : attribute
     -Return : true: need  false:no need
     -----------------------------------------------------------------------------*/
    private boolean isUnderLine(char curAttr) {
        return getBit((byte) curAttr, 6) && getBit((byte) curAttr, 2) == false;
    }

    /*-----------------------------------------------------------------------------
     -Purpose: calculate the count between x1,y1 and x2,y2
     -Param  : x1,y1: the first point; x2,y2: the last point
     -Return : count
     -Remark : x1,y1 must before x2,y2
     -----------------------------------------------------------------------------*/
    private int diffPos(int x1, int y1, int x2, int y2) {
        return Math.abs((y1 * _cols + x1) - (y2 * _cols + x2));
    }

    /*-----------------------------------------------------------------------------
     -Purpose: move cursor (up, down, right ,left)
     -Remark : change the active field if needed
     -----------------------------------------------------------------------------*/
    private void moveCursor(int serverKeycode) {
        int nOldX = nBufX.get(), nOldY = nBufY.get();
        switch (serverKeycode) {
            case IBMKEY_UP:
                if (nBufY.get() > 0)
                    nBufY.decrementAndGet();
                else
                    nBufY.set(_rows - 1);
                break;
            case IBMKEY_DOWN:
                if (nBufY.get() < _rows - 1)
                    nBufY.incrementAndGet();
                else
                    nBufY.set(0);
                break;
            case IBMKEY_RIGHT:
                nextPos(nBufX, nBufY);
                break;
            case IBMKEY_LEFT:
                prevPos(nBufX, nBufY);
                break;
        }

        if (!ptInFields(nBufX.get(), nBufY.get())) {
            nBufX.set(nOldX);
            nBufY.set(nOldY);
            return;
        }

        if (!(ActiveField.ptInField(nBufX.get(), nBufY.get()))) {
            if (ActiveField.canExit()) {
                setActiveField();
                changeHardStatus(ActiveField);
            } else {//restore cursor
                warning();
                nBufX.set(nOldX);
                nBufY.set(nOldY);
            }
        }
    }

    /*-----------------------------------------------------------------------------
     -Purpose: set current active field when move cursor
     -Param  :
     -Return :
     -Remark : while move cursor we should tracking active field
     -----------------------------------------------------------------------------*/
    private void setActiveField() {
        if (ActiveField.ptInField(nBufX.get(), nBufY.get()))
            return;

        tagField aField = new tagField();
        boolean bValid = TNTag.toFirst();

        //find active field
        while (bValid) {
            TNTag.getCurr(aField);
            if (aField.ptInField(nBufX.get(), nBufY.get())) {
                ActiveField.copy(aField);
                break;
            }
            if (!TNTag.toNext())
                break;
        }
    }

    /*-----------------------------------------------------------------------------
     -Purpose: adjust hardware status according to current field
     -Param  : aField: the field to refer
     -Return :
     -Remark :
     -----------------------------------------------------------------------------*/
    private void changeHardStatus(tagField aField) {
        PreviousChar = 0;
        bDBCS = false; // Robin+ 2006.7.20 to handle DBCS input
        if (!aField.valid())
            return;
        cAttrib = (char) aField.cAttrib; // Robin+ 2006.9.11 change attrib after ActiveField changed
    }

    /*-----------------------------------------------------------------------------
     -Purpose: get a bit of a character
     -Param  : c:    the character
     -         nPos: the bit position
     -Return : true: if the bit is 1; false: if the bit is 0
     -Remark : in IBM, bit 0 is the high bit of a byte
     -----------------------------------------------------------------------------*/
    private boolean getBit(byte c, int nPos) {
        c = (byte) (c << nPos); //In IBM bit 0 is the high bit of a byte
        return (c & 0x80) == 0x80;
    }
    /*-----------------------------------------------------------------------------
     -Purpose: set a bit of a character at specfic position
     -Param  : c:    the character to set bit
     -         nPos: the bit position
     -         bEnable: set 1 or 0
     -Return : the character after set bit
     -Remark : in IBM, bit 0 is the high bit of a byte
     -----------------------------------------------------------------------------*/
    private byte setBit(byte c, int nPos, boolean bEnable) {
        byte cRet = 1;
        nPos = 7 - nPos;
        cRet = (byte) (cRet << nPos);
        if (bEnable) {
            cRet = (byte) (c | cRet);
        } else {
            cRet = (byte) (c & ~cRet);
        }
        return cRet;
    }

    /*-----------------------------------------------------------------------------
     -Purpose: set screen buffer with character c at position (i,j)
     -Param  : i: x position; j: y position; c: character
     -Return : void
     -Remark : if screen format is in effect, set the NewScreen buffer too.
     _Revise : Robin+ 2004.11.5 attribute
     -----------------------------------------------------------------------------*/
    private void setScrBuf(int X, int Y, char c) {
        CharGrid[Y][X] = c;
        AttribGrid[Y][X] = cAttrib; // Robin+ 2004.11.5 Set screen attribute
    }

    /*-----------------------------------------------------------------------------
     -Purpose: erase a character in screen and left move the reset content in the field
     -Param  : aField: target field; (x,y): coordinate
     -Return :
     -Remark :
     -----------------------------------------------------------------------------*/
    private void eraseChar(tagField aField, int x, int y) {
        int nRest = 0, i = 0;
        AtomicInteger nNextX = new AtomicInteger();
        AtomicInteger nNextY = new AtomicInteger();
        nRest = aField.nLen - diffPos(aField.x, aField.y, x, y);
        while (i < nRest) {
            nNextX.set(x);
            nNextY.set(y);
            nextPos(nNextX, nNextY);
            CharGrid[y][x] = CharGrid[nNextY.get()][nNextX.get()];
            procChar(CharGrid[y][x], AttribGrid[y][x], x, y);
            x = nNextX.get();
            y = nNextY.get();
            i++;
        }
        CharGrid[y][x] = 0;
        procChar(CharGrid[y][x], AttribGrid[y][x], x, y);
    }

    /*-----------------------------------------------------------------------------
     -Purpose: move cursor to default position
     -Param  :
     -Return :
     -Remark :
     -Revise : Robin 2004.10.14 move cursor to IC ....
     -----------------------------------------------------------------------------*/
    private void defaultAddress() {
        //As described in IBM, we should moves the cursor to the address given in the last IC order
        //or defaults to the first position of the first non-bypass input field if no IC order has
        //been given. If there is no non-bypass field, it defaults to row1, column 1.
        tagField aField = new tagField();
        boolean bValid = TNTag.toFirst();

        nBufX.set(0);
        nBufY.set(0);
        ActiveField = new tagField();
        if (nICX.get() != -1 && nICY.get() != -1) {
            //Robin+ 2004.10.14 moves the cursor to the address given in the last IC order
            nBufX.set(nICX.get());
            nBufY.set(nICY.get());
            //Find field at the position
            while (bValid) {
                TNTag.getCurr(aField);
                if (aField.ptInField(nBufX.get(), nBufY.get())) {
                    ActiveField.copy(aField);
                    break;
                }
                if (!TNTag.toNext())
                    break;
            }
        } else {
            while (bValid) {
                byte c = 0;
                TNTag.getCurr(aField);
                c = aField.szFFW[0];
                //if (c != 0 && !GetBit(c, 2))
                if (true) {
                    nBufX.set(aField.x);
                    nBufY.set(aField.y);
                    ActiveField.copy(aField);
                    break;
                }
                if (!TNTag.toNext())
                    break;
            }
        }
        changeHardStatus(ActiveField);
    }

    private void repeatFillData(int ToY, int ToX, char Data) {
        AtomicInteger xAtom = new AtomicInteger();
        AtomicInteger yAtom = new AtomicInteger();
        getBufferPos(xAtom, yAtom);
        int nRepeatlen = (ToY * _cols + ToX) - (yAtom.get() * _cols + xAtom.get());
        if (nRepeatlen <= 0)
            return;

        for (int i = 0; i < nRepeatlen; i++) {
            setScrBuf(xAtom.get(), yAtom.get(), Data);
            if (xAtom.get() >= (_cols - 1)) {
                xAtom.set(1);
                yAtom.incrementAndGet();//y++
            }
            else
                xAtom.incrementAndGet(); //x++
        }
        setBufferPos(xAtom.get(), yAtom.get());
    }

    private void orderSetBufferAddress(int nParmH, int nParmL) {
        getBufferAddress(nParmH * 256 + nParmL, nBufY, nBufX);

        if (nBufX.get() >= (_cols - 1)) {
            nBufX.set(0);
            nBufY.incrementAndGet();
        }
    }

    private void setStartOfField() {
        //bit 2 protect
        //bit 3 : 0,Alpha 1,Number
        //bit 4,5 : 11 ,Not Display Other Display
        //bit 7 :MTD
        boolean samefield = false;

        cAttrib = OrderBuffer.get(1);
        boolean Protect;
        Protect= getBit((byte) cAttrib, 2);

        tagField LastFirld = new tagField();

        nextPos(nBufX, nBufY);

        if (!TNTag.isEmpty()) {
            if (TNTag.getCurrAdd(LastFirld)){
                if (LastFirld.nLen == 0) {
                    LastFirld.nLen = (nBufY.get() * _cols + nBufX.get()) - (FieldStdY * _cols + FieldStdX);
                    if (LastFirld.nLen>0)
                        LastFirld.nLen--;
                    for (int i = 0; i < LastFirld.nLen; i++) {
                        LastFirld.cAttrib = setBit(LastFirld.cAttrib, 6, true);
                        AttribGrid[LastFirld.y][LastFirld.x + i] = (char) LastFirld.cAttrib;
                    }
                    TNTag.setCurrAdd(LastFirld);
                }
            }
        }

        if (!ProtectTNTag.isEmpty()) {
            if (ProtectTNTag.getCurrAdd(LastFirld)) {
                if (LastFirld.nLen == 0) {
                    LastFirld.nLen = (nBufY.get() * _cols + nBufX.get()) - (FieldStdY * _cols + FieldStdX);
                    if (LastFirld.nLen == 0) {
                        samefield = true;
                    }
                    else
                        samefield = false;

                    if (LastFirld.nLen > 0)
                        LastFirld.nLen--;

                    ProtectTNTag.setCurrAdd(LastFirld);
                }
            }
        }

        tagField aField = new tagField();

        aField.x = (byte) nBufX.get();
        aField.y = (byte) nBufY.get();

        FieldStdX = nBufX.get();
        FieldStdY = nBufY.get();
        aField.cAttrib = (byte) cAttrib;

        if (samefield)
            ProtectTNTag.setCurrAdd(aField);
        else
            ProtectTNTag.addMember(aField);

        if (Protect) {
            return;
        }

        TNTag.addMember(aField);
    }

    private void parseOrders() {
        CurOrder = OrderBuffer.get(0);
        switch(CurOrder) {
            case EraseUnprotectedToAddress:
                break;
            case ModifyField:
                break;
            case ProgramTab:
            case RepeatToAddress:
                AtomicInteger yAtom = new AtomicInteger();
                AtomicInteger xAtom = new AtomicInteger();
                getBufferAddress(OrderBuffer.get(1) * 256 + OrderBuffer.get(2), yAtom, xAtom);
                repeatFillData(yAtom.get(), xAtom.get(), EBCDIC2ASCII(OrderBuffer.get(3)));
                break;
            case SetAttribute:
                break;
            case SetBufferAddress:
                orderSetBufferAddress(OrderBuffer.get(1), OrderBuffer.get(2));
                break;
            case StartField:
                setStartOfField();
                break;
            case StartFieldExtended:
                break;
            case InsertCursor:
                getBufferPos(nICX, nICY);
                defaultAddress();
                break;
            default:
                break;
        }
    }

    private void parserFieldData() {
        AtomicInteger xAtom = new AtomicInteger();
        AtomicInteger yAtom = new AtomicInteger();
        getBufferPos(xAtom, yAtom);
        bDBCS = false;
        for (int i = 0; i < DataBuffer.size(); i++) {
            char c = DataBuffer.get(i);
            setScrBuf(xAtom.get(), yAtom.get(), c);
            nextPos(xAtom, yAtom);
        }
        setBufferPos(xAtom.get(), yAtom.get());
    }
    //action for Commands end

    private void doAction(IBmActions action) {
        switch(action) {
            case IBMA_ParseCommand:
                mCurCommand = mCurChar;
                switch(mCurCommand) {
                    case IBMCommand3270.IBMCm_Write:
                        bLock = false;
                        break;
                    case IBMCommand3270.IBMCm_EraseWrite:
                        eraseDisplay();
                        break;
                    case IBMCommand3270.IBMCm_ReadBuffer:
                        bLock = false;
                        sendDataBuffer();
                        break;
                }
                break;
            case IBMA_ParseWCC:
                mCurWcc = mCurChar;
                break;
            case IBMA_OrdersRecord:
                OrderBuffer.add(mCurChar);
                break;
            case IBMA_RecordData:
                DataBuffer.add(mCurChar);
                break;
            case IBMA_ParseData:
                parserFieldData();
                DataBuffer.clear();
                break;
            case IBMA_OrdersParse:
                parseOrders();
                OrderBuffer.clear();
                break;
            default:
                break;
        }
    }

    public IBMHost3270() {
        mPreIBMState = IBMS_Ground;
        mParsePanding = 0;
        SetSize(24, 80);
        mTN3270KeyCodeMap = new HashMap<>(gDefaultTN_3270KeyCodeMap_Taurus);
    }

    @Override
    protected boolean isScreenAttributeVisible(char attr) {
        return (attr & 0x0c) != 0x0c;
    }

    @Override
    public Point getCursorGridPos() {
        return new Point(nBufX.get(), nBufY.get());
    }

    @Override
    protected int getServerKeyCode(int keyCode) {
        Integer nIBMKeyCode = mTN3270KeyCodeMap.get(keyCode);
        if(nIBMKeyCode != null) {
            CipherUtility.Log_d("IBMHost3270", "Keycode mapped, Keyevent = %d[%s], IBM Keycode = %d[%s]", keyCode, KeyMapUtility.getPhysicalKeyTextByEncode(keyCode), nIBMKeyCode, getServerKeyText(nIBMKeyCode));
            return nIBMKeyCode;
        }
        CipherUtility.Log_d("IBMHost3270", "No Keycode mapped! Keyevent = %d[%s]", keyCode, KeyMapUtility.getPhysicalKeyTextByEncode(keyCode));
        return IBMKEY_NONE;
    }

    @Override
    public String GetTerminalTypeName() {
        return "IBM-3278-2";
    }

    @Override
    public void handleKeyDown(int keyCode, KeyEvent event) {
        int nIBMKeyCode = IBMKEY_NONE;
        if(event instanceof ServerKeyEvent) {
            nIBMKeyCode = keyCode;
            CipherUtility.Log_d("IBMHost3270", "IBM Keycode = %d[%s]", nIBMKeyCode, getServerKeyText(nIBMKeyCode));
        } else {
            int nEncodePhyKeycode = KeyMapUtility.getEncodePhyKeyCode(event);
            nIBMKeyCode = getServerKeyCode(nEncodePhyKeycode);
        }

        if(bLock) {
            if(nIBMKeyCode != IBMKEY_RESET)
                return;
        }

        if(nIBMKeyCode != IBMKEY_NONE) {
            switch (nIBMKeyCode) {
                case IBMKEY_ENTER:
                    ibmSend(ENT_VAL_3270);
                    break;
                /*Todo: confirm IBMKEY_ROLUP and IBMKEY_ROLDN is needed
                case IBMKEY_ROLUP:
                case IBMKEY_ROLDN:
                    break;*/
                case IBMKEY_F1:
                case IBMKEY_F2:
                case IBMKEY_F3:
                case IBMKEY_F4:
                case IBMKEY_F5:
                case IBMKEY_F6:
                case IBMKEY_F7:
                case IBMKEY_F8:
                case IBMKEY_F9:
                case IBMKEY_F10:
                case IBMKEY_F11:
                case IBMKEY_F12:
                case IBMKEY_F13:
                case IBMKEY_F14:
                case IBMKEY_F15:
                case IBMKEY_F16:
                case IBMKEY_F17:
                case IBMKEY_F18:
                case IBMKEY_F19:
                case IBMKEY_F20:
                case IBMKEY_F21:
                case IBMKEY_F22:
                case IBMKEY_F23:
                case IBMKEY_F24:
                    //Todo:
                    break;
                case IBMKEY_PA1:
                case IBMKEY_PA2:
                case IBMKEY_PA3:
                    //Todo:IBMSendAid(HostSet[tnSession].hSocket, data);
                    break;
                case IBMKEY_NEXT:
                    if (ActiveField.valid()) {
                        if (TNTag.toNext())
                            TNTag.getCurr(ActiveField);
                        else {
                            TNTag.toFirst();
                            TNTag.getCurr(ActiveField);
                        }
                        nBufX.set(ActiveField.x);
                        nBufY.set(ActiveField.y);
                    } else if (TNTag.toFirst()) {
                        TNTag.getCurr(ActiveField);
                        nBufX.set(ActiveField.x);
                        nBufY.set(ActiveField.y);
                    }
                    changeHardStatus(ActiveField);
                    break;
                case IBMKEY_PREV:
                    if (ActiveField.valid()) {
                        if (TNTag.toPrev())
                            TNTag.getCurr(ActiveField);
                        else {
                            TNTag.toLast();
                            TNTag.getCurr(ActiveField);
                        }
                        nBufX.set(ActiveField.x);
                        nBufY.set(ActiveField.y);
                    } else if (TNTag.toLast()) {
                        TNTag.getCurr(ActiveField);
                        nBufX.set(ActiveField.x);
                        nBufY.set(ActiveField.y);
                    }
                    changeHardStatus(ActiveField);
                    break;
                case IBMKEY_FBEGIN:
                    if (ActiveField.valid()) {
                        nBufX.set(ActiveField.x);
                        nBufY.set(ActiveField.y);
                    }
                    break;
                case IBMKEY_FEND:
                    if (ActiveField.valid()) {
                        getLastPos(ActiveField, nBufX, nBufY, true);
                    }
                    break;
                case IBMKEY_LEFTDELETE:
                    if (ActiveField.valid()) {
                        if (nBufX.get() == ActiveField.x && nBufY.get() == ActiveField.y) {
                            tagField tempField = new tagField();
                            if (TNTag.getPrev(tempField)) {
                                ActiveField.copy(tempField);
                            } else {
                                if (TNTag.toLast())
                                    TNTag.getCurr(ActiveField);
                            }
                            int xResult = ActiveField.x + ActiveField.nLen - 1;
                            int yResult = ActiveField.y;

                            if (xResult >= _cols) {
                                yResult += xResult / _cols;
                                xResult = xResult % _cols;
                            }
                            nBufX.set(xResult);
                            nBufY.set(yResult);
                            changeHardStatus(ActiveField);
                        }
                        else
                            prevPos(nBufX, nBufY);
                        eraseChar(ActiveField, nBufX.get(), nBufY.get());
                        ActiveField.cAttrib = setBit(ActiveField.cAttrib, 7, true);
                        TNTag.setCurr(ActiveField);
                    } else {
                        warning();
                    }
                    break;
                case IBMKEY_DEL:
                    /*Todo:IBMKEY_DEL
                    if (ActiveField.valid()) {
                        EraseChar(ActiveField, nBufX, nBufY);
                        EraseMapChar(ActiveField, ReX, ReY);
                        ActiveField.cAttrib = setBit(ActiveField.cAttrib, 7, true);
                        TNTag.setCurr(ActiveField);
                    }
                    */
                    break;
                case IBMKEY_LEFT:
                case IBMKEY_RIGHT:
                case IBMKEY_UP:
                case IBMKEY_DOWN:
                    moveCursor(nIBMKeyCode);
                    break;
                case IBMKEY_INS:
                    bInsert = !bInsert;
                    break;
                case IBMKEY_ATTN:
                    //Todo:IBMSendAid(HostSet[tnSession].hSocket, 0x02);
                    break;
                case IBMKEY_CLR:
                    //Todo:IBMSendAid(HostSet[tnSession].hSocket, 0x6d);
                case IBMKEY_ERINPUT:
                    /*Todo:IBMKEY_ERINPUT
                    NullFields(TRUE);
                    if (TNTag.toFirst()) {
                        TNTag.getCurr(ActiveField);
                        nBufX.set(ActiveField.x);
                        nBufY.set(ActiveField.y);
                        changeHardStatus(ActiveField);
                    }
                    */
                    break;
                case IBMKEY_CLREOF:
                    /*Todo:IBMKEY_CLREOF
                    if (ActiveField.valid()) {
                        EraseEof(ActiveField, nBufX, nBufY);
                        ActiveField.szFFW[0] = setBit(ActiveField.szFFW[0], 4, true);
                        TNTag.setCurr(ActiveField);
                    }
                    */
                    break;
                case IBMKEY_HOME:
                    if (TNTag.toFirst()) {
                        TNTag.getCurr(ActiveField);
                        nBufX.set(ActiveField.x);
                        nBufY.set(ActiveField.y);
                        changeHardStatus(ActiveField);
                    }
                    break;
                case IBMKEY_RESET:
                    if (bLock) {
                        bLock = false;
                    }
                    break;
                case IBMKEY_DUP:
                    /*Todo:IBMKEY_DUP
                    if (ActiveField.valid()) {
                        tagField NextField = new tagField();
                        if (TNTag.readNext(NextField)) {
                            Duplicatefield(NextField, ActiveField);
                            if (TNTag.toNext()) {
                                TNTag.getCurr(ActiveField);
                                nBufX.set(ActiveField.x);
                                nBufY.set(ActiveField.y);
                                changeHardStatus(ActiveField);
                            }
                        }
                    }*/
                    break;
                case IBMKEY_FMARK:
                    //Todo:IBMFieldMaekActiveField(1);
                    break;
                case IBMKEY_SYSRQ:
                    //Todo:IBMSendAid(HostSet[tnSession].hSocket, 0xf0);
                    break;
                case IBMKEY_LAST:
                    if (ActiveField.valid()) {
                        TNTag.toLast();
                        TNTag.getCurr(ActiveField);
                        nBufX.set(ActiveField.x);
                        nBufY.set(ActiveField.y);
                        changeHardStatus(ActiveField);
                    }
                    break;
                case IBMKEY_NEWLINE:
                    int xTemp = nBufX.get();
                    int yTemp = nBufY.get();
                    yTemp++;
                    xTemp =0;
                    if (!ptInFields(xTemp, yTemp)) {
                        if (ActiveField.valid()) {
                            if (TNTag.toNext())
                                TNTag.getCurr(ActiveField);
                            else {
                                TNTag.toFirst();
                                TNTag.getCurr(ActiveField);
                            }
                            nBufX.set(ActiveField.x);
                            nBufY.set(ActiveField.y);
                        } else if (TNTag.toFirst()) {
                            TNTag.getCurr(ActiveField);
                            nBufX.set(ActiveField.x);
                            nBufY.set(ActiveField.y);
                        }
                    } else {
                        nBufX.set(xTemp);
                        nBufY.set(yTemp);
                    }
                    changeHardStatus(ActiveField);
                    break;
                default:
                    break;
            }
        } else {
            char pressedKey = (char) event.getUnicodeChar();
            if (pressedKey == 0)
                return;
            putAsciiKey(pressedKey);
        }

        ViewPostInvalidate();
    }

    private void putAsciiKey(char c) {
        char ebcdic = ASCII2EBCDIC(c);
        if (!ptInFields(nBufX.get(), nBufY.get())) {
            defaultAddress();
        }

        //Todo: handle dbcs
        if ((PreviousChar > 0x7f) && isDBCS(PreviousChar, (byte) c)) {
            bDBCS = true;
            PreviousChar = 0;
        } else {
            bDBCS = false;
            PreviousChar = (byte) c;
        }

        if (bInsert) {
            insertChar(ActiveField, nBufX.get(), nBufY.get());
        }

        cAttrib = AttribGrid[nBufY.get()][nBufX.get()]; // Robin+ 2004.11.9
        setScrBuf(nBufX.get(), nBufY.get(), ebcdic);
        procChar(ebcdic, cAttrib, nBufX.get(), nBufY.get());
        nextPos(nBufX, nBufY);
        ActiveField.cAttrib = setBit(ActiveField.cAttrib, 7, true);
        TNTag.setCurr(ActiveField); //save to list

        int nLen = diffPos(ActiveField.x, ActiveField.y, nBufX.get(), nBufY.get());
        int nBufLen = ActiveField.nLen;

        if (nLen == nBufLen) {
            if (isAutoEnt()) {
                ibmSend(ENT_VAL); // Robin 2004.6.14 AUTO_VAL -> ENT_VAL
            } else {
                ActiveField = new tagField();
                if (!TNTag.isLast()) {
                    TNTag.toNext();
                    TNTag.getCurr(ActiveField);
                } else {
                    if (TNTag.toFirst())
                        TNTag.getCurr(ActiveField);
                }
                changeHardStatus(ActiveField);
                nBufX.set(ActiveField.x);
                nBufY.set(ActiveField.y);
            }
        }
    }

    protected void procChar(char edbic, char attr, int x, int y) {
        if(isScreenAttributeVisible(attr)) {
            char ch = EBCDIC2ASCII(edbic);
            DrawChar(ch, x, y, false, isUnderLine(attr), false);
        } else {
            DrawSpace(x, y, 1);
        }
    }

    @Override
    public void handleBarcodeFire(String Code) {

    }

    @Override
    public void OnScreenBufferPos(int x, int y) {

    }

    @Override
    public void drawAll() {
        StringBuilder dbcsTemp = new StringBuilder();
        for (int idxRow = 0; idxRow < _rows; idxRow++) {
            for (int idxCol = 0; idxCol < this._cols; ++idxCol) {
                char c = CharGrid[idxRow][idxCol];
                switch (c) {
                    case DBCS_LEADING_AND_ENDING:
                        if(bDBCS) {
                            bDBCS = false;
                        } else {
                            bDBCS = true;
                        }
                        break;
                    default://data
                        if(bDBCS == false) { //Single Byte
                            if (IsCharAttributes(c) == false) {
                                char curAttr = AttribGrid[idxRow][idxCol];
                                if(isScreenAttributeVisible(curAttr)) {
                                    char curData = EBCDIC2ASCII(c);
                                    //means protected
                                    DrawChar(curData, idxCol, idxRow, false, isUnderLine(curAttr), false);
                                }
                            }
                        } else { //Double byte
                            char curAttr = AttribGrid[idxRow][idxCol];
                            boolean bUnderLine = getBit((byte) curAttr, 6);
                            dbcsTemp.append(c);
                            if(dbcsTemp.length() >= 2) {
                                String word = converToDBCSByTable(dbcsTemp.toString());
                                DrawChar(Character.valueOf(word.charAt(0)), idxCol, idxRow, false, bUnderLine, true);
                                dbcsTemp.setLength(0);
                            }
                        }
                        break;
                }
            }
        }
    }

    @Override
    public void ParseEnd() {
        mPreIBMState = IBMS_Ground;
        drawAll();
    }

    @Override
    public void processChar(char ch) {
        CipherUtility.Log_d("IBMHost3270", "IBMHost3270.processChar, char = [%02x]", (byte) ch);
        AtomicReference<IBmStates> curIBMState = new AtomicReference<>(IBmStates.IBMS_None);
        AtomicReference<IBmActions> curEventAction = new AtomicReference<>(IBmActions.IBMA_None);

        mCurChar = ch;
        if (checkPadding() > 0) {
            curIBMState.set(mPreIBMState);
            curEventAction.set(mLastEventAction);
        } else {
            getStateEventAction(mPreIBMState, mCurChar, curIBMState, curEventAction);
        }

        boolean bStateChanged = (curIBMState.get() != IBmStates.IBMS_None && curIBMState.get() != mPreIBMState);
        if (bStateChanged) {
            // check for state exit actions
            AtomicReference<IBmActions> stateExitAction = new AtomicReference<>(IBmActions.IBMA_None);
            getStateChangeAction(mPreIBMState, Transitions.Exit, stateExitAction);
            // Process the exit action
            if (stateExitAction.get() != IBmActions.IBMA_None) {
                doAction(stateExitAction.get());
            }
            CipherUtility.Log_d("IBMHost3270", "[Exit] mPreIBMState = %s, curIBMState = %s, state change exit Action = %s", mPreIBMState.toString(), curIBMState, stateExitAction);
        }

        if (curEventAction.get() != IBmActions.IBMA_None) {
            doAction(curEventAction.get());
            CipherUtility.Log_d("IBMHost3270", "[Event Action] do action = %s", curEventAction);
        }

        mLastEventAction = curEventAction.get();

        if (bStateChanged) {
            AtomicReference<IBmActions> stateEntryAction = new AtomicReference<>(IBmActions.IBMA_None);
            // check for state entry actions
            getStateChangeAction(curIBMState.get(), Transitions.Entry, stateEntryAction);

            // Process the entry action
            if (stateEntryAction.get() != IBmActions.IBMA_None) {
                doAction(stateEntryAction.get());
            }
            CipherUtility.Log_d("IBMHost3270", "[Entry] curIBMState = %s, state change entry Action = %s", curIBMState.toString(), stateEntryAction);
        }
        mPreIBMState = curIBMState.get();
    }

    @Override
    public void setKeyMapList(KeyMapList keyMapList) {
        if(keyMapList != null) { //Load from settings
            mTN3270KeyCodeMap = new HashMap<>();
            for (int idxKeyMapList = 0; idxKeyMapList < keyMapList.listSize(); idxKeyMapList++) {
                KeyMapItem keyMapItem = keyMapList.getItem(idxKeyMapList);
                mTN3270KeyCodeMap.put(keyMapItem.getPhysicalKeycode(), keyMapItem.getServerKeycode());
            }
        }
    }

    @Override
    protected boolean autoLogin() {
        return false;
    }
}
