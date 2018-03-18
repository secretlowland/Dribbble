package com.andy.dribbble.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.andy.dribbble.common_utils.ScreenUtil;

/**
 * Created by lixn on 2018/3/17.
 */

public class ImageViewer extends LinearLayout {

    private ImageView mImgView;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mImgRes;
    private ImageView mImgSrc;
    private ActionMode mActionMode;
    private Matrix mMatrix = new Matrix();
    private Matrix mCurrentMatrix = new Matrix();

    private int mTouchSlop;
    private int mMinVelocity;
    private int mLastY;
    private int mLastX;
    private int mDownX;
    private int mDownY;
    private int mDragDistanceX;
    private int mDragDistanceY;
    private double mStartDistance;
    private boolean mShowing;

    private GestureDetector mDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            dismiss();
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return true;
        }
    });
    private enum ActionMode {
        IDLE, DRAG, ZOOM
    }

    private OnDragListener mDragListener;

    public ImageViewer(ImageView src, Context context) {
        super(context);
        mImgSrc = src;
        initView(context);
    }

    public ImageViewer(Context context) {
        super(context);
        initView(context);
    }

    public ImageViewer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ImageViewer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        setOrientation(VERTICAL);
        setBackgroundColor(Color.parseColor("#dd000000"));
        setAlpha(0);
        mScroller = new Scroller(context);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMinVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
    }

    public boolean handleBackEvent() {
        if (mShowing) {
            dismiss();
            return true;
        }
        return false;
    }

    protected void zoom() {
        mImgView.setScaleX(2.0f);
        mImgView.setScaleY(2.0f);
    }

    protected void dismiss() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(ImageViewer.this, "alpha", getAlpha(), 0).setDuration(1000);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ViewParent vp = getParent();
                if (vp instanceof ViewGroup) {
                    ((ViewGroup) vp).removeView(ImageViewer.this);
                }
                mShowing = false;
            }
        });
        animator.start();
    }

    public void show(Activity context, final ImageView src) {
        mShowing = true;
        mImgSrc = src;
        ViewGroup decor = (ViewGroup) context.getWindow().getDecorView();
        if (getParent() == null) {
            decor.addView(this);
        }
        if (mImgView == null) {
            mImgView = new ImageView(getContext());
            mImgView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            ViewGroup.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            addView(mImgView, lp);
        }
        if (mImgSrc != null) {
            mImgView.setImageDrawable(mImgSrc.getDrawable());
        }
        postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator.ofFloat(ImageViewer.this, "alpha", 0, 1.0f).setDuration(1000).start();
            }
        }, 200);
    }

    private void initImgBounds() {
        Drawable d = mImgView.getDrawable();
        final int dwidth = d.getIntrinsicWidth();
        final int dheight = d.getIntrinsicHeight();
        final int vwidth = getWidth();
        final int vheight = getHeight();
        float dx = 0;
        float dy = 0;
        float scale = 1.0f;
        if (dwidth * vheight > vwidth * dheight) {
            scale = (float) vheight / (float) dheight;
            dx = (vwidth - dwidth * scale) * 0.5f;
        } else {
            scale = (float) vwidth / (float) dwidth;
            dy = (vheight - dheight * scale) * 0.5f;
        }
        Matrix m = new Matrix();
        m.set(mImgView.getImageMatrix());
        m.setScale(scale, scale);
        m.postTranslate(dx, dy);
        mImgView.setImageMatrix(m);
    }

    private void animateView(Rect bound) {
        final Rect rect = bound;
        final  ViewGroup.LayoutParams lp = mImgView.getLayoutParams();
        final float screenWidth = ScreenUtil.getScreenWidth(getContext());
        final float screenHeight = ScreenUtil.getScreenHeight(getContext());
        final int oldWidth = lp.width;
        final int oldHeight = lp.height;
        final int oldLeft = mImgView.getLeft();
        final int oldTop = mImgView.getTop();
        final int oldRight = mImgView.getRight();
        final int oldBottom = mImgView.getBottom();
        final float oldScaleX = mImgView.getScaleX();
        final float oldScaleY = mImgView.getScaleY();
        Drawable drawable = mImgView.getDrawable();
//        mImgView.setScaleType(ImageView.ScaleType.MATRIX);
//        mImgView.setImageResource(R.mipmap.mm);
//        mImgView.getImageMatrix().setScale(1.0f, -1.0f);
        final int width = drawable.getIntrinsicWidth();
        final int height = drawable.getIntrinsicHeight();
        float v = screenWidth/width;
        float h = screenHeight/height;
        final float scale = Math.min(v, h);
        ValueAnimator animator = ObjectAnimator.ofFloat(0, 1.0f).setDuration(3000);
        ObjectAnimator.ofFloat(this, "alpha", 0, 1.0f).setDuration(1000).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float per = (float) animation.getAnimatedValue();
            }
        });
        animator.start();
    }

    private int calculateTranslationX(Rect r) {
        float w = r.right - r.left;
        float screenWidth = ScreenUtil.getScreenWidth(getContext());
        return (int) (screenWidth/2 - w/2 - r.left+0.5f);
    }

    private int calculateTranslationY(Rect r) {
        float h = r.bottom - r.top;
        float screenHeight = ScreenUtil.getScreenHeight(getContext());
        return (int) (screenHeight/2 - h/2 - r.left+0.5f);
    }

    public ImageView getImgView() {
        return mImgView;
    }

    public void setImgRes(int res) {
        mImgView.setImageResource(res);
        invalidate();
    }



    public void setImgFrame(Rect r) {
        requestLayout();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) (event.getX() + 0.5f);
        int y = (int) (event.getY() + 0.5f);
        checkVelocityTracker();
        mVelocityTracker.addMovement(event);
        boolean shouldHandle = isTouchInView(mImgView, event.getRawX(), event.getRawY());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mActionMode = ActionMode.DRAG;
                mCurrentMatrix.set(mImgView.getImageMatrix());
                mLastX = mDownX= x;
                mLastY = mDownY = y;
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mActionMode = ActionMode.ZOOM;
                mStartDistance = calculateDistance(event);
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = x - mLastX;
                int deltaY = y - mLastY;
                scrollBy(-deltaX, -deltaY);
                mDragDistanceX = x - mDownX;
                mDragDistanceY = y - mDownY;
                if (mDragListener != null) {
                    mDragListener.onDrag(mDragDistanceX, mDragDistanceY);
                }

                if (mActionMode == ActionMode.ZOOM) {
                    double distance = calculateDistance(event);
                    float scale = (float) (distance/mStartDistance);
                    mMatrix.set(mCurrentMatrix);
                    mMatrix.postScale(scale, scale);
                }
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
                mActionMode = ActionMode.IDLE;
                mVelocityTracker.computeCurrentVelocity(1000);
                int vx = (int) (mVelocityTracker.getXVelocity() + 0.5f);
                int vy = (int) (mVelocityTracker.getYVelocity() + 0.5f);
                if (vx > mMinVelocity || vy > mMinVelocity) {
                    mScroller.fling(getScrollX(), getScrollY(), -vx, -vy, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
                    invalidate();
                }
                int distance = (int) Math.sqrt(mDragDistanceX*mDragDistanceX + mDragDistanceY*mDragDistanceY);
                if (distance > mTouchSlop) {
                    mScroller.startScroll(getScrollX(), getScrollY(), mDragDistanceX, mDragDistanceY);
                    invalidate();
                }
                break;
        }
        mImgView.setImageMatrix(mMatrix);
        mDetector.onTouchEvent(event);
        return true;
    }

    private double calculateDistance(MotionEvent event) {
        float x = event.getX(1) - event.getX(0);
        float y = event.getY(1) - event.getY(0);
        return Math.sqrt(x*x+y*y);
    }
    private boolean isTouchInView(View target, float rawX, float rawY) {
        if (target == null) {
            return false;
        }
        Rect bounds = new Rect();
        boolean b = target.getGlobalVisibleRect(bounds);
        return b && rawX>bounds.left && rawX < bounds.right && rawY>bounds.top && rawY<bounds.bottom;

    }

    private void checkVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    public void setOnDragListener(OnDragListener listener) {
        mDragListener = listener;
    }

    public interface OnDragListener {
        void onDrag(int distanceX, int distanceY);
    }
}
