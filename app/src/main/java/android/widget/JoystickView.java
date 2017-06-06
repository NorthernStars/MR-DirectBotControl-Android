package android.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class JoystickView extends View {

    private final String TAG = "JoystickView";

    private Paint mCirclePaint;
    private Paint mHandlePaint;
    private int mInnerPadding;
    private int mRadius;
    private int mHandleRadius;
    private JoystickMovedListener mMoveListener;

    private float mMoveResolution;

    private boolean mYAxisInverted;

    private float mMovementRange;

    public final static int sCOORDINATE_CARTESIAN = 0;
    public final static int sCOORDINATE_DIFFERENTIAL = 1;
    private int mUserCoordinateSystem;

    private float mTouchX, mTouchY;

    private float mReportX, mReportY;

    private int mPx, mPy;

    private int mUserX, mUserY;

    public JoystickView(Context aContext) {
        super(aContext);
        initJoystickView();
    }

    public JoystickView(Context aContext, AttributeSet aAttributeSet) {
        super(aContext, aAttributeSet);
        initJoystickView();
    }

    public JoystickView(Context aContext, AttributeSet aAttributeSet, int aDefStyle) {
        super(aContext, aAttributeSet, aDefStyle);
        initJoystickView();
    }

    private void initJoystickView() {
        setFocusable(true);

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(Color.GRAY);
        mCirclePaint.setStrokeWidth(1);
        mCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mHandlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHandlePaint.setColor(Color.DKGRAY);
        mHandlePaint.setStrokeWidth(1);
        mHandlePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mInnerPadding = 10;

        setMovementRange(10);
        setMoveResolution(1.0f);
        setYAxisInverted(true);
        setUserCoordinateSystem(sCOORDINATE_CARTESIAN);
    }

    public void setUserCoordinateSystem(int aUserCoordinateSystem) {
        if (aUserCoordinateSystem < sCOORDINATE_CARTESIAN)
            Log.e(TAG, "invalid value for userCoordinateSystem");
        else
            this.mUserCoordinateSystem = aUserCoordinateSystem;
    }

    public void setYAxisInverted(boolean aYAxisInverted) {
        this.mYAxisInverted = aYAxisInverted;
    }

    public void setMovementRange(float aMovementRange) {
        this.mMovementRange = aMovementRange;
    }

    public void setMoveResolution(float aMoveResolution) {
        this.mMoveResolution = aMoveResolution;
    }

    public void setOnJostickMovedListener(JoystickMovedListener aListener) {
        this.mMoveListener = aListener;
    }

    @Override
    protected void onMeasure(int aWidthMeasureSpec, int aHeightMeasureSpec) {
        int vMeasuredWidth = measure(aWidthMeasureSpec);
        int vMeasuredHeight = measure(aHeightMeasureSpec);
        int vD = Math.min(vMeasuredWidth, vMeasuredHeight);

        mHandleRadius = (int)(vD * 0.25);

        mPx = vD / 2;
        mPy = vD / 2;
        mRadius = Math.min(mPx, mPy) - mHandleRadius;

        setMeasuredDimension(vD, vD);
    }

    private int measure(int aMeasureSpec) {
        int vResult;
        int vSpecMode = MeasureSpec.getMode(aMeasureSpec);
        int vSpecSize = MeasureSpec.getSize(aMeasureSpec);
        if (vSpecMode == MeasureSpec.UNSPECIFIED) {
            vResult = 200;
        } else {
            vResult = vSpecSize;
        }
        return vResult;
    }

    @Override
    protected void onDraw(Canvas aCanvas) {
        int vPx = getMeasuredWidth() / 2;
        int vPy = getMeasuredHeight() / 2;
        int vRadius = Math.min(vPx, vPy);

        aCanvas.drawCircle(vPx, vPy, vRadius - mInnerPadding, mCirclePaint);

        aCanvas.drawCircle(mTouchX + vPx, mTouchY + vPy, mHandleRadius, mHandlePaint);

        aCanvas.save();
    }

    private void constrainBox() {
        mTouchX = Math.max(Math.min(mTouchX, mRadius), -mRadius);
        mTouchY = Math.max(Math.min(mTouchY, mRadius), -mRadius);
    }

    @Override
    public boolean onTouchEvent(MotionEvent aEvent) {
        int vActionType = aEvent.getAction();
        if (vActionType == MotionEvent.ACTION_MOVE) {
            mTouchX = (aEvent.getX() - mPx);
            mTouchY = (aEvent.getY() - mPy);

            reportOnMoved();
            invalidate();
        }
        else if (vActionType == MotionEvent.ACTION_UP) {
            returnHandleToCenter();
        }
        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private void reportOnMoved() {
        constrainBox();

        calcUserCoordinates();

        if (mMoveListener != null) {
            boolean vRx = Math.abs(mTouchX - mReportX) >= mMoveResolution;
            boolean vRy = Math.abs(mTouchY - mReportY) >= mMoveResolution;
            if (vRx || vRy) {
                this.mReportX = mTouchX;
                this.mReportY = mTouchY;

                mMoveListener.OnMoved(mUserX, mUserY);
            }
        }
    }

    private void calcUserCoordinates() {
        int vCartX = (int)(mTouchX / mRadius * mMovementRange);
        int vCartY = (int) (mTouchY / mRadius * mMovementRange);

        if ( !mYAxisInverted)
            vCartY *= -1;

        if ( mUserCoordinateSystem == sCOORDINATE_CARTESIAN) {
            mUserX = vCartX;
            mUserY = vCartY;
        }
        else if ( mUserCoordinateSystem == sCOORDINATE_DIFFERENTIAL) {
            mUserX = vCartY + vCartX / 4;
            mUserY = vCartY - vCartX / 4;

            if ( mUserX < -mMovementRange)
                mUserX = (int)-mMovementRange;
            if ( mUserX > mMovementRange)
                mUserX = (int) mMovementRange;

            if ( mUserY < -mMovementRange)
                mUserY = (int)-mMovementRange;
            if ( mUserY > mMovementRange)
                mUserY = (int) mMovementRange;
        }

    }

    private void returnHandleToCenter() {

        Handler vHandler = new Handler();
        final int vNumberOfFrames = 5;
        final double vIntervalsX = (0 - mTouchX) / vNumberOfFrames;
        final double vIntervalsY = (0 - mTouchY) / vNumberOfFrames;

        for (int i = 0; i < vNumberOfFrames; i++) {
            final int j = i;
            vHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mTouchX += vIntervalsX;
                    mTouchY += vIntervalsY;

                    reportOnMoved();
                    invalidate();

                    if (mMoveListener != null && j == vNumberOfFrames - 1) {
                        mMoveListener.OnReturnedToCenter();
                    }
                }
            }, i * 40);
        }

        if (mMoveListener != null) {
            mMoveListener.OnReleased();
        }
    }
}

