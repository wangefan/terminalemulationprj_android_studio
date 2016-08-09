package Terminals;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.example.terminalemulation.R;
import com.te.UI.CipherUtility;
import com.te.UI.UIUtility;

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
        mHScrollView = (HorizontalScrollView) stdActivityRef.getCurrActivity().findViewById(R.id.mainHScroll);
        mVScrollView = (ScrollView) stdActivityRef.getCurrActivity().findViewById(R.id.mainVScroll);
        mCorsor = Cursor;
        this.setFocusableInTouchMode(true);
        this.setFocusable(true);
        if (isInEditMode())
            return;
    }

    private void setPaintColor() {
        int nFontsColor = TESettingsInfo.getHostFontsColorByIndex(TESettingsInfo.getSessionIndex());
        int nBgColor = TESettingsInfo.getHostBgColorByIndex(TESettingsInfo.getSessionIndex());
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
        int nScreenWidth = CipherUtility.getScreenWidth();
        int nScreenHeight = CipherUtility.getScreenHeight();
        int nNewBmpWidth = Math.max(Math.max(mTerminalProc.getCols() * mFontRect.width(), nScreenWidth), nScreenHeight);
        int nNewBmpHeight = Math.max(Math.max(mTerminalProc.getRows() * mFontRect.height(), nScreenWidth), nScreenHeight);
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
        int nFontType = TESettingsInfo.getHostFontsTypeByIndex(TESettingsInfo.getSessionIndex());
        switch(nFontType) {
            case TESettingsInfo.LU_CONSOLE:
                mFontface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Lucida-Console.ttf");
                break;
            case TESettingsInfo.EXCA_MONO:
                mFontface = Typeface.createFromAsset(getContext().getAssets(), "fonts/ExcaliburMonospace.ttf");
                break;
            case TESettingsInfo.NET_ANSI:
                mFontface = Typeface.createFromAsset(getContext().getAssets(), "fonts/ntansi.ttf");
                break;
            case TESettingsInfo.NET_OEM:
                mFontface = Typeface.createFromAsset(getContext().getAssets(), "fonts/ntoem.ttf");
                break;
            case TESettingsInfo.COURIER_NEW:
            default:
                mFontface = Typeface.createFromAsset(getContext().getAssets(), "fonts/courier-new.ttf");
                break;
        }

        p.setTypeface(mFontface);
        final int nScaleFromTECPPSetting = 2;
        int nFontWidth = TESettingsInfo.getHostFontWidthByIndex(TESettingsInfo.getSessionIndex()) * nScaleFromTECPPSetting;
        mFontsize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, nFontWidth, getResources().getDisplayMetrics());
        p.setTextSize(mFontsize);
        p.setTextAlign(Align.LEFT);

        return p;
    }

    public void DrawChar(char c, int x, int y, boolean IsBold, boolean IsUnderLine, boolean bMultiByte) {
        if (mCanvas == null)
            return;
        int nFontWidthUnit = 1;
        if(bMultiByte)
            nFontWidthUnit = 2;
        Rect rect = new Rect(mFontRect.width() * x, mFontRect.height() * y, mFontRect.width() * x + mFontRect.width() * nFontWidthUnit, mFontRect.height() * y + mFontRect.height());
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

        Boolean IsTracking = TESettingsInfo.getHostIsCursorTrackByIndex(TESettingsInfo.getSessionIndex());

        if (IsTracking) {
            int nMoveToX = 0, nMoveToY = 0;
            switch (TESettingsInfo.getHostAutoTrackTypeByIndex(TESettingsInfo.getSessionIndex())) {
                case AutoTrackType_Visible:
                    nMoveToX = getXPosByRate(0.75f, nBmpPosX);
                    nMoveToY = getYPosByRate(0.75f, nBmpPosY);
                    break;
                case AutoTrackType_Center:
                    nMoveToX = getXPosByRate(0.5f, nBmpPosX);
                    nMoveToY = getYPosByRate(0.5f, nBmpPosY);
                    break;
                case AutoTrackType_Lock:
                    int nFixRow = TESettingsInfo.getHostLockerRowIndex(TESettingsInfo.getSessionIndex());
                    int nFixCol = TESettingsInfo.getHostLockerColIndex(TESettingsInfo.getSessionIndex());
                    nMoveToX = Math.min(nFixCol * mFontRect.width(), mBmpWidth);
                    nMoveToY = Math.min(nFixRow * mFontRect.height(), mBmpHeight);
                    break;
                default:
                    break;
            }

            mHScrollView.scrollTo(nMoveToX, 0);
            mVScrollView.scrollTo(0, nMoveToY);
            CipherUtility.Log_d("ContentView", String.format("Scroll to [x:%d , y:%d]", nMoveToX, nMoveToY));
        }
    }

    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        if (mTerminalProc != null) {
            if(TESettingsInfo.getHostIsDetectOFRByIndex(TESettingsInfo.getSessionIndex()) == true) {
                UIUtility.detectNetworkOutRange(new UIUtility.OnDetectOFRListener() {
                    @Override
                    public void onResult(boolean bHasNetwork) {
                        if(bHasNetwork == false) {
                            stdActivityRef.getCurrActivity().SessionDisConnect();
                            return;
                        } else {
                            mTerminalProc.handleKeyDown(keyCode, event);
                        }
                    }
                });
            } else {
                mTerminalProc.handleKeyDown(keyCode, event);
            }
        }
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
   
    /*public void surfaceCreated(SurfaceHolder holder) {
        
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
         
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
         
    }*/


}