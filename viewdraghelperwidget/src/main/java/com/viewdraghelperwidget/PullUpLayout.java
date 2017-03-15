package com.viewdraghelperwidget;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Fishy on 2017/3/3.
 */

public class PullUpLayout extends ViewGroup {
    final String TAG_LOG = "DragOutLayout";
    /**
     * 蒙版颜色
     */
    final int MASK_COLOR = 0x55000000;
    /**
     * 上下文context
     */
    Context context;
    /**
     * 蒙版
     */
    View maskView;
    /**
     * 拖动辅助器
     */
    ViewDragHelper viewDragHelper;
    /**
     * 内容部分的View
     */
    View contentView;
    /**
     * 可拉出的view
     */
    View pullUpView;
    /**
     * 默认的敏感度
     */
    final float defaultSensitivity = 1.0f;
    /**
     * 可拉出的view的title的高度
     */
    int pullUpTitleHeight;
    /**
     * 可拉出的view的content的高度
     */
    int pullUpContentHeight;
    /**
     * 可拉出的view的高度
     */
    int pullUpHeight;
    /**
     * 整个布局的高度
     */
    int layoutHeight;
    /**
     * pullupView是否打开
     */
    boolean isPullupViewOpen = false;
    /**
     * 打开pullupView所需要的百分比,越小越容易开/关
     */
    float openPullupPercent = 0.3f;
    /**
     * 关闭pullupView所需要的百分比
     */
    float closePullupPersent = 0.3f;
    /**
     * pullupView打开状态监听器
     */
    onPullupViewStateChangeListener onPullupViewStateChangeListener;

    OnClickListener maskOnclickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            closePullUpView();
        }
    };

    public PullUpLayout(Context context) {
        this(context, null);
    }

    public PullUpLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullUpLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        contentView = getChildAt(0);
        pullUpView = getChildAt(1);
        maskView = createMaskView();
        maskView.setOnClickListener(maskOnclickListener);
        pullUpView.setClickable(true);
    }

    /**
     * 初始化
     */
    void init() {
        viewDragHelper = ViewDragHelper.create(this, defaultSensitivity, new CustomDragCallback());
        //开启底部可以响应手势滑动
        viewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_BOTTOM);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //先确定模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //再确定测绘出的尺寸
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        //测绘子view
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        //赋值给一些全局变量
        pullUpHeight = pullUpView.getMeasuredHeight();

        int width;
        int height;
        //这种布局暂时不考虑wrap_content的情况
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = widthSize;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = heightSize;
        }
        setMeasuredDimension(width, height);
    }

    /**
     * 重写对scroll的处理，将事件交给viewDragHelper来处理
     */
    @Override
    public void computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    /**
     * 是否激活点击蒙版区域，关闭pullupView
     * @param isEnable
     */
    public void enableOutsideTouchClose(boolean isEnable) {
        if (isEnable) {
            maskView.setOnClickListener(maskOnclickListener);
        } else {
            maskView.setClickable(false);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //contentView填充满父view
        contentView.layout(l, t, r, b);
        int height = getMeasuredHeight();
        //高度
        layoutHeight = getMeasuredHeight();
        pullUpView.layout(l, height - pullUpHeight, r, height);
        //放置蒙版
        maskView.layout(l, t, r, b);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }

    /**
     * 更新PullupView的状态
     *
     * @param isOpen 是否打开
     */
    void updatePullupViewState(boolean isOpen) {
        if (isOpen) {
            viewDragHelper.settleCapturedViewAt(0, layoutHeight - pullUpHeight);

        } else {
            viewDragHelper.settleCapturedViewAt(0, layoutHeight);
        }
        if (isPullupViewOpen != isOpen) {
            if (onPullupViewStateChangeListener != null) {
                onPullupViewStateChangeListener.onPullupViewStateChanged(isOpen);
            }
            isPullupViewOpen = isOpen;
            changeMaskViewState(isOpen);
        }
        invalidate();
    }

    /**
     * 设置拉出view状态改变监听器
     * @param onPullupViewStateChangeListener
     */
    public void setOnPullupViewStateChangeListener(PullUpLayout.onPullupViewStateChangeListener onPullupViewStateChangeListener) {
        this.onPullupViewStateChangeListener = onPullupViewStateChangeListener;
    }

    /**
     *
     * @param openPullupPercent
     */
    public void setOpenPullupPercent(float openPullupPercent) {
        this.openPullupPercent = openPullupPercent;
    }

    /**
     *
     * @param closePullupPersent
     */
    public void setClosePullupPersent(float closePullupPersent) {
        this.closePullupPersent = closePullupPersent;
    }

    /**
     * 打开pullupView
     */
    public void openPullUpView() {
        viewDragHelper.smoothSlideViewTo(pullUpView, 0, layoutHeight - pullUpHeight);
        if (onPullupViewStateChangeListener != null) {
            onPullupViewStateChangeListener.onPullupViewStateChanged(true);
        }
        changeMaskViewState(true);
        invalidate();
        isPullupViewOpen = true;
    }

    /**
     * 关闭pullupView
     */
    public void closePullUpView() {
        viewDragHelper.smoothSlideViewTo(pullUpView, 0, layoutHeight);
        if (onPullupViewStateChangeListener != null) {
            onPullupViewStateChangeListener.onPullupViewStateChanged(false);
        }
        changeMaskViewState(false);
        invalidate();
        isPullupViewOpen = false;
    }

    /**
     * pullupView是否打开
     *
     * @return
     */
    public boolean isPullupViewOpen() {
        return isPullupViewOpen;
    }

    /**
     * 改变maskView的状态
     *
     * @param isShow
     */
    void changeMaskViewState(boolean isShow) {
        if (maskView != null) {
            if (isShow) {
                maskView.setVisibility(View.VISIBLE);
            } else {
                maskView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 创建蒙版
     */
    View createMaskView() {
        View view = new View(context);
        view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        view.setBackgroundColor(MASK_COLOR);
        addView(view, 1);
        return view;
    }

    class CustomDragCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            if (child == pullUpView) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            //该组件横向不滑动
            return super.clampViewPositionHorizontal(child, left, dx);
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            //仅在纵向滑动
            //初始值代表不发生偏移
            int offset = pullUpView.getTop();
            if (child == pullUpView) {
                Log.i(TAG_LOG, "pullupView-->" + offset);
                offset = Math.max(Math.min(top, layoutHeight), layoutHeight - pullUpHeight);
            }
//            Log.i(TAG_LOG,"offset-->"+offset);
            return offset;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (releasedChild == pullUpView) {
                int dividerTop;
                //先判断当前是否开启
                //给出dividerLeft赋值，定下打开或关闭的分界线
                if (isPullupViewOpen) {
                    dividerTop = (int) (layoutHeight - pullUpHeight * (1 - closePullupPersent));
                } else {
                    dividerTop = (int) (layoutHeight - pullUpHeight * openPullupPercent);
                }
                if (releasedChild.getTop() <= dividerTop) {
                    updatePullupViewState(true);
                } else {
                    updatePullupViewState(false);
                }
            }
        }

        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
//            Log.i(TAG_LOG,"EdgeDrag");
            viewDragHelper.captureChildView(pullUpView, pointerId);
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return super.getViewHorizontalDragRange(child);
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return 1;
        }
    }

    public interface onPullupViewStateChangeListener {
        void onPullupViewStateChanged(boolean isOpen);
    }
}
