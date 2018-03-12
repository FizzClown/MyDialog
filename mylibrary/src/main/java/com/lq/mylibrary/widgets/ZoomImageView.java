package com.lq.mylibrary.widgets;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;

/**
 * Created by lq on 2018/1/10.
 */

public class ZoomImageView extends android.support.v7.widget.AppCompatImageView implements ViewTreeObserver.OnGlobalLayoutListener,
        ScaleGestureDetector.OnScaleGestureListener,
        View.OnTouchListener {
    private boolean once;
    //初始化放大的值
    private float mInitScale;
    //双击放大的值
    private float mMidScale;
    //放大的最大值
    private float mMaxScale;
    //Matrix矩阵
    private Matrix mScaleMatrix;
    //捕获用户多指触控时缩放的比例
    private ScaleGestureDetector mScaleGestureDetector;

    //--------------------------------下面是自由移动
    //记录上一次多点触控的数量
    private int mLastPointCount;
    //中心点的位置
    private float mLastX;
    private float mLastY;
    //判断滑动的最小值
    private int mTouchSlop;
    //判断是否可以移动（大图的时候）
    private boolean isCanMove;
    private boolean isCheckLeftAndRight;
    private boolean isCheckTopAndBottom;

    //--------------------------------下面是双击放大缩小
    //检测各种手势和事件使用提供的
    private GestureDetector mGestureDetector;
    private boolean isAutoScale;


    public ZoomImageView(Context context) {
        this(context, null);
    }

    public ZoomImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScaleMatrix = new Matrix();
        super.setScaleType(ScaleType.MATRIX);
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        setOnTouchListener(this);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (isAutoScale) {
                    return true;
                }

                float x = e.getX();
                float y = e.getY();
                if (getScale() < mMidScale) {
                    //mScaleMatrix.postScale(mMidScale / getScale(), mMidScale / getScale(), x, y);
                    postDelayed(new AutoScaleRunnable(mMidScale, x, y), 10);
                    isAutoScale = true;
                } else {
                    //mScaleMatrix.postScale(mInitScale / getScale(), mInitScale / getScale(), x, y);
                    postDelayed(new AutoScaleRunnable(mInitScale, x, y), 10);
                    isAutoScale = true;
                }
                setImageMatrix(mScaleMatrix);
                return true;
            }
        });
    }

    /**
     * 自动放大与缩小
     */
    private class AutoScaleRunnable implements Runnable {
        //缩放的目标值
        private float mTargetScale;
        //缩放的中心点
        private float x;
        private float y;
        private final float BIGGER = 1.07F;
        private final float SMALLER = 0.93F;
        private float temScale;

        public AutoScaleRunnable(float mTargetScale, float x, float y) {
            this.mTargetScale = mTargetScale;
            this.x = x;
            this.y = y;
            if (getScale() < mTargetScale) {
                temScale = BIGGER;
            }

            if (getScale() > mTargetScale) {
                temScale = SMALLER;
            }
        }

        @Override
        public void run() {
            mScaleMatrix.postScale(temScale, temScale, x, y);
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);

            float currentScale = getScale();
            if ((temScale > 1.0f && currentScale < mTargetScale) || (temScale < 1.0f && currentScale > mTargetScale)) {
                postDelayed(this, 16);
            } else {
                float scale = mTargetScale / currentScale;
                mScaleMatrix.postScale(scale, scale, x, y);
                checkBorderAndCenterWhenScale();
                setImageMatrix(mScaleMatrix);
                isAutoScale = false;
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    /**
     * 获取ImageView加载完成的图片
     */
    @Override
    public void onGlobalLayout() {
        if (!once) {
            //得到控件的宽高
            int width = getWidth();
            int height = getHeight();
            //得到我们图片以及宽和高
            Drawable drawable = getDrawable();
            if (drawable == null) {
                return;
            }
            float scale = 1.0f;
            int dw = drawable.getIntrinsicWidth();
            int dh = drawable.getIntrinsicHeight();
            //如果图片很宽
            if (dw > width && dh < height) {
                scale = width * 1.0f / dw;
            }

            if (dh > height && dw < width) {
                scale = height * 1.0f / dh;
            }

            if (dw > width && dh > height) {
                scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);
            }

            if (dw < width && dh < height) {
                scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);
            }
            /**
             * 得到初始化时缩放的比例
             */
            mInitScale = scale;
            mMidScale = scale * 2f;
            mMaxScale = scale * 4f;
            //将图片移动到当前控件的中心
            float dx = getWidth() / 2f - dw / 2f;
            float dy = getHeight() / 2f - dh / 2f;
            /**
             * Matrix的数据结构
             * |xScale   0     xTran|
             * |0      yScale yTrans|
             * |0       0       0   |
             * 内部为三成三的矩阵（一维数组）
             */
            mScaleMatrix.postTranslate(dx, dy);
            mScaleMatrix.postScale(mInitScale, mInitScale, width / 2f, height / 2f);
            setImageMatrix(mScaleMatrix);
            once = true;
        }
    }

    /**
     * 获取当前图片的缩放值
     *
     * @return
     */
    public float getScale() {
        float[] values = new float[9];
        mScaleMatrix.getValues(values);
        return values[Matrix.MSCALE_X];
    }

    /**
     * 当缩放的时候
     *
     * @param detector
     * @return
     */
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        //当前的缩放值
        float scale = getScale();
        //当前捕获的缩放值
        float scaleFactor = detector.getScaleFactor();
        if (getDrawable() == null) {
            return true;
        }
        //缩放的控制
        if ((scale < mMaxScale && scaleFactor > 1.0f) || (scale > mInitScale && scaleFactor < 1.0f)) {
            if (scale * scaleFactor < mInitScale) {
                scaleFactor = mInitScale / scale;
            }

            if (scale * scaleFactor > mMaxScale) {
                scaleFactor = mMaxScale / scale;
            }

            mScaleMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);
        }
        return true;
    }

    /**
     * 获得图片放大缩小以后的宽高以及l,r,t,b
     *
     * @return
     */
    private RectF getMatrixRectF() {
        Matrix matrix = mScaleMatrix;
        RectF rectF = new RectF();
        Drawable d = getDrawable();
        if (d != null) {
            rectF.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            matrix.mapRect(rectF);
        }
        return rectF;
    }

    /**
     * 在缩放时进行边界的控制以及位置的控制
     */
    private void checkBorderAndCenterWhenScale() {
        RectF rect = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;
        float width = getWidth();
        float height = getHeight();
        //缩放时进行边界检测防止出现白边
        if (rect.width() >= width) {
            //屏幕左边有空隙
            if (rect.left > 0) {
                deltaX = -rect.left;
            }
            if (rect.right < width) {
                deltaX = width - rect.right;
            }
        }

        if (rect.height() >= height) {
            if (rect.top > 0) {
                deltaY = -rect.top;
            }
            if (rect.bottom < height) {
                deltaY = height - rect.bottom;
            }
        }

        //如果宽度或者高度小于控件的宽高则让其居中
        if (rect.width() < width) {
            deltaX = width / 2f - rect.right + rect.width() / 2f;
        }

        if (rect.height() < height) {
            deltaY = height / 2f - rect.bottom + rect.height() / 2f;
        }

        mScaleMatrix.postTranslate(deltaX, deltaY);
    }

    /**
     * 当缩放开始时
     *
     * @param detector
     * @return
     */
    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //如果双击直接返回true
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }

        mScaleGestureDetector.onTouchEvent(event);
        float x = 0;
        float y = 0;
        //得到多点触控的数量
        int pointCount = event.getPointerCount();
        for (int i = 0; i < pointCount; i++) {
            x += event.getX(i);
            y += event.getY(i);
        }
        //得到中心点的位置
        x /= pointCount;
        y /= pointCount;
        //如果触摸过程中手指数量发生改变
        if (mLastPointCount != pointCount) {
            isCanMove = false;
            mLastX = x;
            mLastY = y;
        }
        mLastPointCount = pointCount;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = x - mLastX;
                float dy = y - mLastY;
                if (!isCanMove) {
                    isCanMove = isMoveAction(dx, dy);
                }
                if (isCanMove) {
                    RectF rectF = getMatrixRectF();

                    if (getDrawable() != null) {

                        isCheckLeftAndRight = isCheckTopAndBottom = true;

                        //如果宽度小于控件宽度 不允许横向移动
                        if (rectF.width() < getWidth()) {
                            isCheckLeftAndRight = false;
                            dx = 0;
                        }

                        if (rectF.height() < getHeight()) {
                            isCheckTopAndBottom = false;
                            dy = 0;
                        }

                        mScaleMatrix.postTranslate(dx, dy);
                        checkBorderWhenTranslate();
                        setImageMatrix(mScaleMatrix);
                    }
                }
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
                //手指抬起触控点重置为0个
                mLastPointCount = 0;
                break;
        }
        return true;
    }

    /**
     * 移动时进行边界检查
     */
    private void checkBorderWhenTranslate() {
        RectF rectF = getMatrixRectF();
        float dx = 0;
        float dy = 0;
        int width = getWidth();
        int height = getHeight();

        if (rectF.top > 0 && isCheckTopAndBottom) {
            dy = -rectF.top;
        }

        if (rectF.bottom < height && isCheckTopAndBottom) {
            dy = height - rectF.bottom;
        }

        if (rectF.left > 0 && isCheckLeftAndRight) {
            dx = -rectF.left;
        }

        if (rectF.right < width && isCheckLeftAndRight) {
            dx = width - rectF.right;
        }
        //平移
        mScaleMatrix.postTranslate(dx, dy);
    }

    /**
     * 判断是否是Move
     *
     * @param dx
     * @param dy
     * @return
     */
    private boolean isMoveAction(float dx, float dy) {
        //开方算出斜对角线的长度
        return Math.sqrt(dx * dx + dy * dy) > mTouchSlop;
    }
}
