package TelnetIBM;
import Terminals.*;
/**
 * Created by Franco.Liu on 2015/1/21.
 */
public abstract class IBMHostBase extends TerminalBase {


	 public boolean mBInsertMode;

	 public class Ibm_Caret
	    {
	        public Point Pos;
	        //public System.Drawing.Color Color = System.Drawing.Color.FromArgb(255, 181, 106);
	        //public System.Drawing.Bitmap Bitmap = null;
	        //public System.Drawing.Graphics Buffer = null;
	        public boolean IsOff = false;
	        public boolean EOL = false;

	        public Ibm_Caret()
	        {
	            this.Pos = new Point(0, 0);
	        }
	        public Ibm_Caret(Point Pos)
	        {
	            this.Pos = new Point(Pos.X, Pos.Y);
	            
	        }
	    }

	protected boolean isDBCS(final byte bHigh, final byte bLow) {
		boolean bRet = false;
		int nLanguage = TESettingsInfo.getTELanguage(TESettingsInfo.getSessionIndex());
		switch (nLanguage) {
			case 1: //1: TC
				if ((bHigh >= 0x81 && bHigh <= 0xFE) && ((bLow >= 0x40 && bLow <= 0x7e) || (bLow >= 0xa1 && bLow <= 0xfe)))
					bRet = true;
				break;
			case 2: //2: SC
				if ((bHigh >= 0xa1 && bHigh <= 0xf7) && (bLow >= 0xa1 && bLow <= 0xfe))
					bRet = true;
				break;
			case 3: //3: Kor
				if ((bHigh >= 0xa1 && bHigh <= 0xfe) && (bLow >= 0xa1 && bLow <= 0xf3))
					bRet = true;
				break;
			case 4: //4: Jap
				if (((bHigh >= 0x81 && bHigh <= 0x9f) || (bHigh >= 0xe0 && bHigh <= 0xfc)) && (bLow >= 0x40 && bLow <= 0xfe))
					bRet = true;
				break;
			case 0: //0: single byte char
			case 5: //5: Gre
			case 6: //6: Fre
			default:
				break;
		}

		return bRet;
	}
}
