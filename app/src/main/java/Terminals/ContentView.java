package Terminals;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.cipherlab.barcode.BuildConfig;
import com.example.terminalemulation.R;

import SessionProcess.TerminalProcess;
import Terminals.TerminalBaseEnum.Point;


public class ContentView extends View {
    private HorizontalScrollView mHScrollView = null;
    private ScrollView mVScrollView = null;
    private CursorView mCorsor;
    private Rect mFontRect = null;
    private int mTextBaselineDis = 0;//distance from baseline to bottom
    Canvas mCanvas;
    Typeface mFontface = null;
    float mFontsize = 0;
    Paint mBgpaint;
    TextPaint mFgpaint;
    int mBmpWidth;
    int mBmpHeight;
    private Bitmap mImage;
    private int mBackgroundColor;
    private int mForegroundColor;
    private TerminalProcess mTerminalProc; //subject

    public ContentView(Context context, CursorView Cursor) {
        super(context);
        mHScrollView = (HorizontalScrollView) stdActivityRef.GetCurrActivity().findViewById(R.id.mainHScroll);
        mVScrollView = (ScrollView) stdActivityRef.GetCurrActivity().findViewById(R.id.mainVScroll);
        mCorsor = Cursor;
        setPaintColor();
        this.setFocusableInTouchMode(true);
        this.setFocusable(true);

        if (isInEditMode())
            return;


    }

    private void setPaintColor() {
        int nFontsColor = CipherConnectSettingInfo.getHostFontsColorByIndex(CipherConnectSettingInfo.GetSessionIndex());
        int nBgColor = CipherConnectSettingInfo.getHostBgColorByIndex(CipherConnectSettingInfo.GetSessionIndex());
        mBackgroundColor = nBgColor;
        mForegroundColor = nFontsColor;
        mBgpaint = GetBGPaint(mBackgroundColor);
        mFgpaint = GetPaint(mForegroundColor);
        getCharMonoBoundsAndBaseline(mFgpaint);
        mCorsor.setColor(mForegroundColor);
        mCorsor.SetSize(mFontRect.height() - mTextBaselineDis, mFontRect.width());
    }

    private void getCharMonoBoundsAndBaseline(TextPaint paint) {
        final String REFERENCE_CHARACTERS = "MW";
        final int GAP_Y = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics());
        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        double maxWidth = 0;
        for (int idxChar = 0; idxChar < REFERENCE_CHARACTERS.length(); idxChar++) {
            maxWidth = Math.max(paint.measureText(REFERENCE_CHARACTERS, idxChar, idxChar + 1), maxWidth);
        }
        int nHeight = fontMetrics.bottom - fontMetrics.top + GAP_Y;
        mFontRect = new Rect(0, 0, (int)Math.ceil(maxWidth), nHeight);
        mTextBaselineDis = fontMetrics.bottom;
    }

    public void setTerminalProc(TerminalProcess terminalProc) {
        mTerminalProc = terminalProc;
    }

    public final void updateViewGrid() {
        int nNewBmpWidth = mTerminalProc.getCols() * mFontRect.width();
        int nNewBmpHeight = mTerminalProc.getRows() * mFontRect.height();
        if(nNewBmpWidth == mBmpWidth && nNewBmpHeight == mBmpHeight) {
            ClearView();
            return;
        }
        mBmpWidth = nNewBmpWidth;
        mBmpHeight = nNewBmpHeight;
        if(mBmpWidth > 0 && mBmpHeight > 0) {
            setLayoutParams(new RelativeLayout.LayoutParams(mBmpWidth, mBmpHeight));
            Bitmap bitmap = Bitmap.createBitmap(mBmpWidth, mBmpHeight, Bitmap.Config.RGB_565);
            mImage = bitmap;
            mCanvas = new Canvas(mImage);
            ClearView();
        }
    }

    public void refresh() {
        setPaintColor();
        updateViewGrid();
        if(mTerminalProc != null)
            mTerminalProc.drawAll();
    }

    public void ClearView() {
        if (mCanvas != null)
            mCanvas.drawColor(mBackgroundColor);
    }

    private Paint GetBGPaint(int Color) {
        Paint p = new Paint();
        p.setColor(Color);
        p.setStyle(Paint.Style.FILL);
        return p;
    }

    private TextPaint GetPaint(int Color) {
        TextPaint p = new TextPaint(Paint.ANTI_ALIAS_FLAG| Paint.LINEAR_TEXT_FLAG);
        p.setColor(Color);
        p.setStyle(Paint.Style.FILL);
        int nFontType = CipherConnectSettingInfo.getHostFontsTypeByIndex(CipherConnectSettingInfo.GetSessionIndex());
        switch(nFontType) {
            case CipherConnectSettingInfo.LU_CONSOLE:
                mFontface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Lucida-Console.ttf");
                break;
            case CipherConnectSettingInfo.EXCA_MONO:
                mFontface = Typeface.createFromAsset(getContext().getAssets(), "fonts/ExcaliburMonospace.ttf");
                break;
            case CipherConnectSettingInfo.NET_ANSI:
                mFontface = Typeface.createFromAsset(getContext().getAssets(), "fonts/ntansi.ttf");
                break;
            case CipherConnectSettingInfo.NET_OEM:
                mFontface = Typeface.createFromAsset(getContext().getAssets(), "fonts/ntoem.ttf");
                break;
            case CipherConnectSettingInfo.COURIER_NEW:
            default:
                mFontface = Typeface.createFromAsset(getContext().getAssets(), "fonts/courier-new.ttf");
                break;
        }

        p.setTypeface(mFontface);
        final int nScaleFromTECPPSetting = 2;
        int nFontWidth = CipherConnectSettingInfo.getHostFontWidthByIndex(CipherConnectSettingInfo.GetSessionIndex()) * nScaleFromTECPPSetting;
        mFontsize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, nFontWidth, getResources().getDisplayMetrics());
        p.setTextSize(mFontsize);
        p.setTextAlign(Align.LEFT);

        return p;
    }

    public void DrawCharLive(Character c, Integer x, Integer y) {
        if (mCanvas == null)
            return;

        Rect rect = new Rect(mFontRect.width() * x, mFontRect.height() * y, mFontRect.width() * x + mFontRect.width(), mFontRect.height() * y + mFontRect.height());
        mCanvas.drawRect(rect, mBgpaint);
        if (c == 0)
            c = ' ';
        mCanvas.drawText(String.valueOf(c), rect.left, rect.bottom - mTextBaselineDis, mFgpaint);
    }

    public void DrawFieldChar(char c, int x, int y, boolean IsBold, boolean IsUnderLine) {
        if (mCanvas == null)
            return;
        Rect rect = new Rect(mFontRect.width() * x, mFontRect.height() * y, mFontRect.width() * x + mFontRect.width(), mFontRect.height() * y + mFontRect.height());
        mCanvas.drawRect(rect, mBgpaint);
        if (c == 0) {
            c = ' ';
        }
        if (IsUnderLine) {
            final int GAP_Y = 5;
            int nUnderlineY = rect.bottom - mTextBaselineDis + GAP_Y;
            mCanvas.drawLine(rect.left, nUnderlineY, rect.right, nUnderlineY, mFgpaint);//(drawpen, rect.Left, rect.Bottom - 1, rect.Right, rect.Bottom - 1);
        }
        mCanvas.drawText(String.valueOf(c), rect.left, rect.bottom - mTextBaselineDis, mFgpaint);
    }

    public void DrawSpace(int x, int y, int space) {
        //Rect rect = new Rect((int)mFontRect.width() * x, (int)mFontRect.height() * y, (int)(mFontRect.width() * space), (int)(mFontRect.height() * 1));
        Rect rect = new Rect((int) mFontRect.width() * x, (int) mFontRect.height() * y, (int) (mFontRect.width() * x) + (mFontRect.width() * space), (int) (mFontRect.height() * y) + mFontRect.height());
        if (mCanvas == null)
            return;
        mCanvas.drawRect(rect, mBgpaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        this.setMeasuredDimension(mBmpWidth, mBmpHeight);
        // this.setLayoutParams(new RelativeLayout.LayoutParams(800*2,800*2));

    }

    private int getXPosByRate(float fRate, int nCursorBmpPosX) {
        int nMoveToX ;
        int nTrackMaxX = mBmpWidth - (int) ((float)mHScrollView.getWidth() * fRate);
        nMoveToX = Math.max((nCursorBmpPosX - (int) ((float)mHScrollView.getWidth() * fRate)), 0);
        nMoveToX = Math.min(nMoveToX, nTrackMaxX);
        return nMoveToX;
    }

    private int getYPosByRate(float fRate, int nCursorBmpPosY) {
        int nMoveToY = 0;
        int nTrackMaxY = mBmpHeight - (int) ((float)mHScrollView.getHeight() * fRate);
        nMoveToY = Math.max((nCursorBmpPosY - (int) ((float)mHScrollView.getHeight() * fRate)), 0);
        nMoveToY = Math.min(nMoveToY, nTrackMaxY);
        return nMoveToY;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mImage == null)
            return;

        canvas.drawBitmap(mImage, 0, 0, mBgpaint);

        if (mTerminalProc == null)
            return;
        Point cursorPos = mTerminalProc.getCursorGridPos();
        int nBmpPosX = cursorPos.X * mFontRect.width();
        int nBmpPosY = cursorPos.Y * mFontRect.height();
        mCorsor.setX(nBmpPosX);
        mCorsor.setY(nBmpPosY);

        Boolean IsTracking = CipherConnectSettingInfo.getHostIsCursorTrackByIndex(CipherConnectSettingInfo.GetSessionIndex());

        if (IsTracking) {
            int nMoveToX = 0, nMoveToY = 0;
            switch (CipherConnectSettingInfo.getHostAutoTrackTypeByIndex(CipherConnectSettingInfo.GetSessionIndex())) {
                case AutoTrackType_Visible:
                    nMoveToX = getXPosByRate(0.75f, nBmpPosX);
                    nMoveToY = getYPosByRate(0.75f, nBmpPosY);
                    break;
                case AutoTrackType_Center:
                    nMoveToX = getXPosByRate(0.5f, nBmpPosX);
                    nMoveToY = getYPosByRate(0.5f, nBmpPosY);
                    break;
                case AutoTrackType_Lock:
                    int nFixRow = CipherConnectSettingInfo.getHostLockerRowIndex(CipherConnectSettingInfo.GetSessionIndex());
                    int nFixCol = CipherConnectSettingInfo.getHostLockerColIndex(CipherConnectSettingInfo.GetSessionIndex());
                    nMoveToX = Math.min(nFixCol * mFontRect.width(), mBmpWidth);
                    nMoveToY = Math.min(nFixRow * mFontRect.height(), mBmpHeight);
                    break;
                default:
                    break;
            }

            mHScrollView.scrollTo(nMoveToX, 0);
            mVScrollView.scrollTo(0, nMoveToY);
            if (BuildConfig.DEBUG) {
                Log.d("TE:", String.format("Scroll to [x:%d , y:%d]", nMoveToX, nMoveToY));
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mTerminalProc != null)
            mTerminalProc.handleKeyDown(keyCode, event);
        if (keyCode == KeyEvent.KEYCODE_SPACE || keyCode == KeyEvent.KEYCODE_TAB) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.
        //showSoftKeyboard(this);
        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                OnScreenMouseDown((int) x, (int) y);
                break;

        }


        return true;
    }

    public void OnScreenMouseDown(int x, int y) {
        Point BufferPos;


        BufferPos = CalculateCaretPos(x, y);

        if (mTerminalProc != null)
            mTerminalProc.handleScreenTouch(BufferPos.X, BufferPos.Y);

        // DrawSpaceFront(BufferPos.X,BufferPos.Y,1);


    }

    public Point CalculateCaretPos(int PosX, int PosY) {
        int Cx, Cy;

        Cx = PosX / (int) this.mFontRect.width();
        Cy = PosY / (int) this.mFontRect.height();

        return new Point(Cx, Cy);
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {

            //IMMResult result = new IMMResult();
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

            boolean isOpen = imm.isActive();
            //imm.hideSoftInputFromWindow(getWindowToken(),0);
            //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            //imm.hideSoftInputFromWindow(getWindowToken(),0);
            //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            imm.toggleSoftInputFromWindow(getWindowToken(), InputMethodManager.SHOW_FORCED, 0);
            //imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            //if (imm.isActive()) {
            //imm.hideSoftInputFromInputMethod(view, InputMethodManager.HIDE_NOT_ALWAYS);
            // } else {
            //imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            //  }
            //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);
            //imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        }
    }

    public interface OnContentViewListener {
        void onKeyDown(int keyCode, KeyEvent event);
        void onScreenTouch(int x, int y);
    }
   
    /*public void surfaceCreated(SurfaceHolder holder) {
        
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
         
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
         
    }*/


}