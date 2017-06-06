package android.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class VerticalSeekBar extends AppCompatSeekBar {

    public VerticalSeekBar(Context aContext) {
        super(aContext);
    }

    public VerticalSeekBar(Context aContext, AttributeSet aAttributeSet, int aDefStyle) {
        super(aContext, aAttributeSet, aDefStyle);
    }

    public VerticalSeekBar(Context aContext, AttributeSet aAttributeSet) {
        super(aContext, aAttributeSet);
    }

    protected void onSizeChanged(int aWidth, int aHeigth, int aOldWidth, int aOldHeigth) {
        super.onSizeChanged(aHeigth, aWidth, aOldHeigth, aOldWidth);
    }

    @Override
    protected synchronized void onMeasure(int aWidthMeasureSpec, int aHeightMeasureSpec) {
        super.onMeasure(aHeightMeasureSpec, aWidthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    protected void onDraw(Canvas aCanvas) {
        aCanvas.rotate(-90);
        aCanvas.translate(-getHeight(), 0);

        super.onDraw(aCanvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent aEvent) {
        if (!isEnabled()) {
            return false;
        }

        switch (aEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                setProgress(getMax() - (int) (getMax() * aEvent.getY() / getHeight()));
                onSizeChanged(getWidth(), getHeight(), 0, 0);
                break;

            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }
}
