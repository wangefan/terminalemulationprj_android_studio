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
    
}
