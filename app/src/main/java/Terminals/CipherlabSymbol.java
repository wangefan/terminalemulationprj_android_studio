package Terminals;

import java.util.Hashtable;

public class CipherlabSymbol {

	/*public final static String[] ASCIIText = { "  ", "00", "10", "20", "30",
			"40", "50", "60", "70", "00", "", "DLE", "Space", "0", "@", "P",
			"'", "p", "01", "SOH", "DC1", "!", "1", "A", "Q", "a", "q", "02",
			"STX", "DC2", "\"", "2", "B", "R", "b", "r", "03", "ETX", "DC3",
			"#", "3", "C", "S", "c", "s", "04", "EOT", "DC4", "$", "4", "D",
			"T", "d", "t", "05", "ENQ", "NAK", "%", "5", "E", "U", "e", "u",
			"06", "ACK", "SYN", "&", "6", "F", "V", "f", "v", "07", "BEL",
			"ETB", "`", "7", "G", "W", "g", "w", "08", "BS", "CAN", "(", "8",
			"H", "X", "h", "x", "09", "HT", "EM", ")", "9", "I", "Y", "i", "y",
			"0A", "LF", "SUB", "*", ":", "J", "Z", "j", "z", "0B", "VT", "ESC",
			"+", ";", "K", "[", "k", "{", "0C", "FF", "FS", ",", "<", "L",
			"\\", "l", "|", "0D", "CR", "GS", "-", "=", "M", "]", "m", "}",
			"0E", "SO", "RS", ".", ">", "N", "^", "n", "~", "0F", "SI", "US",
			"/", "?", "O", "_", "o", "DEL", };*/
	
	public final static String[] ASCIIText = { "  ", "00", "10", "20", "30","40", "50", "60", "70",
		                                       "00", "", "DLE", "Space", "0", "@", "P",	"'", "p",
		                                       "01", "SOH", "DC1", "!", "1", "A", "Q", "a", "q",
		                                       "02", "STX", "DC2", "\"", "2", "B", "R", "b", "r",
		                                       "03", "ETX", "DC3", "#", "3", "C", "S", "c", "s",
		                                       "04", "EOT", "DC4", "$", "4", "D", "T", "d", "t",
		                                       "05", "ENQ", "NAK", "%", "5", "E", "U", "e", "u",
		                                       "06", "ACK", "SYN", "&", "6", "F", "V", "f", "v",
		                                       "07", "BEL",	"ETB", "`", "7", "G", "W", "g", "w",
		                                       "08", "BS", "CAN", "(", "8" , "H", "X", "h", "x",
		                                       "09", "HT", "EM", ")", "9", "I", "Y", "i", "y",
		                                       "0A", "LF", "SUB", "*", ":", "J", "Z", "j", "z",
		                                       "0B", "VT", "ESC", "+", ";", "K", "[", "k", "{",
		                                       "0C", "FF", "FS", ",", "<", "L" , "\\", "l", "|",
		                                       "0D", "CR", "GS", "-", "=", "M", "]", "m", "}",
		                                       "0E", "SO", "RS", ".", ">", "N", "^", "n", "~",
		                                       "0F", "SI", "US","/", "?", "O", "_", "o", "DEL",
		                                       
		                                       };

	public final static byte[] HEXValue = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,	0x00, 0x00, 0x00,
		                                    0x00, 0x00, 0x10, 0x20, 0x30, 0x40, 0x50, 0x60, 0x70,
		                                    0x01, 0x01, 0x11, 0x21, 0x31, 0x41, 0x51, 0x61, 0x71,
		                                    0x02, 0x02, 0x12, 0x22, 0x32, 0x42, 0x52, 0x62, 0x72,
		                                    0x03, 0x03, 0x13, 0x23, 0x33, 0x43, 0x53, 0x63, 0x73,
		                                    0x04, 0x04, 0x14, 0x24, 0x34, 0x44, 0x54, 0x64, 0x74,
		                                    0x05, 0x05, 0x15, 0x25, 0x35, 0x45, 0x55, 0x65, 0x75,
		                                    0x06, 0x06, 0x16, 0x26, 0x36, 0x46, 0x56, 0x66, 0x76,
		                                    0x07, 0x07, 0x17, 0x27, 0x37, 0x47, 0x57, 0x67,0x77,
		                                    0x08, 0x08, 0x18, 0x28, 0x38, 0x48, 0x58, 0x68, 0x78,
		                                    0x09, 0x09, 0x19, 0x29,	0x39, 0x49, 0x59, 0x69, 0x79,
		                                    0x0A, 0x0A, 0x1A, 0x2A, 0x3A, 0x4A, 0x5A, 0x6A, 0x7A,
		                                    0x0B, 0x0B, 0x1B, 0x2B, 0x3B, 0x4B, 0x5B, 0x6B,	0x7B,
		                                    0x0C, 0x0C, 0x1C, 0x2C, 0x3C, 0x4C, 0x5C, 0x6C, 0x7C,
		                                    0x0D, 0x0D, 0x1D, 0x2D, 0x3D, 0x4D, 0x5D, 0x6D, 0x7D,
		                                    0x0E, 0x0E, 0x1E, 0x2E, 0x3E, 0x4E, 0x5E, 0x6E, 0x7E,
		                                    0x0F, 0x0F, 0x1F, 0x2F, 0x3F, 0x4F, 0x5F, 0x6F, 0x7F, };

	private static Hashtable<Integer, String> table = new Hashtable<Integer, String>();

	public static void InitialTable() {
		table.clear();
		table.put(0x01, "[SOH]");
		table.put(0x02, "[STX]");
		table.put(0x03, "[ETX]");
		table.put(0x04, "[EOT]");
		table.put(0x05, "[ENQ]");
		table.put(0x06, "[ACK]");
		table.put(0x07, "[BEL]");
		table.put(0x08, "[BS]");
		table.put(0x09, "[TAB]");
		table.put(0x0A, "[LF]");
		table.put(0x0B, "[VT]");
		table.put(0x0C, "[FF]");
		table.put(0x0D, "[CR]");
		table.put(0x0E, "[SO]");
		table.put(0x0F, "[SI]");
		table.put(0x10, "[DLE]");
		table.put(0x11, "[DC1]");
		table.put(0x12, "[DC2]");
		table.put(0x13, "[DC3]");
		table.put(0x14, "[DC4]");
		table.put(0x15, "[NAK]");
		table.put(0x16, "[SYN]");
		table.put(0x17, "[ETB]");
		table.put(0x18, "[CAN]");
		table.put(0x19, "[EM]");
		table.put(0x1A, "[SUB]");
		table.put(0x1B, "[ESC]");
		table.put(0x1C, "[FS]");
		table.put(0x1D, "[GS]");
		table.put(0x1E, "[RS]");
		table.put(0x1F, "[US]");
		table.put(0x20, "[Space]");
		table.put(0x7F, "[DEL]");
	}

	public static String TransformMulit(byte[] Input) {

		if (Input == null)
			return null;

		String temp = "";
		for (byte b : Input) {
			int value = (int) (b & 0xFF);
			if (table.containsKey(value)) {
				temp += table.get(value);
			} else {
				char ch = (char) value;
				temp += ch;
			}
		}
		return temp;
	}
	public static String GetStringMulit(byte[] Input) {

		if (Input == null)
			return null;

	    String temp = "";
		
		for(int i=0;i<Input.length;i++)
		{
			temp += "[" + ASCIIText[Input[i]] + "]";
			
		}
		 
	
		return temp;
	}
	
}
