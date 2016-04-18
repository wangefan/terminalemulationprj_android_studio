package Terminals;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.Timer;
import java.util.TimerTask;


public class CursorView extends View {
	
	int CursorHight=0;
	int CursorWhith=0;
	Paint mPaint = new Paint();
	Timer timer = new Timer(true);
	Boolean ShowCursor=true;
	public CursorView(Context context) {
        super(context);
             
        //setLayoutParams(new LayoutParams(15, 15));
        
      //  this.setLeft(10);
        //this.setTop(10); 
         //this.setRight(50); 
       // this.setBottom(50); 
        startTimer();
    }
    
	public void setColor(int nColor) {
		mPaint.setColor(nColor);
	}
	
	public void SetSize(int h,int w)
	{
		CursorHight=h;
		CursorWhith=w;
		this.setLayoutParams(new RelativeLayout.LayoutParams(CursorWhith, CursorHight));
		
	}
	 protected void onDraw(Canvas canvas) {
	        super.onDraw(canvas);

	    	int nType = CipherConnectSettingInfo.getHostCousorTypeByIndex(CipherConnectSettingInfo.GetSessionIndex());
	    	
	    	 
	    	if (nType == 2)//Full block
	    	{
	    		 canvas.drawRect(0, 0, CursorWhith, CursorHight, mPaint);
	    	}else if(nType == 1)// Underline
	    	{
	    		 canvas.drawRect(0, CursorHight-4, CursorWhith, CursorHight, mPaint);
	    	}
	    	else
	    	{
	    		canvas.drawRect(0, 0, CursorWhith/2, CursorHight, mPaint);
	    	}
 	       
	        if (ShowCursor)
	 	    {
	        	this.setAlpha(255);
	 		    //setVisibility(View.VISIBLE);
	 	    }
		 	else
		 	{
		 		this.setAlpha(0);
		 		//setVisibility(View.INVISIBLE);
		 	}
	 } 
	 protected void OnTimerOn() {
		if (getVisibility()==View.INVISIBLE)
 	    {
 		    setVisibility(View.VISIBLE);
 	    }
	 	else
	 	{
	 		setVisibility(View.INVISIBLE);
	 	}
	 } 
	 protected void startTimer() {
		    //isTimerRunning = true; 
		    timer.scheduleAtFixedRate(new TimerTask() {
		        public void run() {
		           	    
		        	if (ShowCursor)
		        		ShowCursor=false;
		        	else
		        		ShowCursor=true;
		        	
		        	postInvalidate();

		        }
		    }, 0, 500);
		};
	 /*protected void dispatchDraw(Canvas canvas) {
	         
         
	        canvas.drawRGB(0, 0, 0);
	 }*/
	 @Override
	 protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		 super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	     //MUST CALL THIS
		 
	     setMeasuredDimension(CursorWhith, CursorHight);
	     
	     
	 }
}
