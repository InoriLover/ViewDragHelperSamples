package com.viewdraghelperwidget;

import android.content.Context;
import android.graphics.Path;
import android.support.annotation.IntegerRes;
import android.support.annotation.LayoutRes;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Fishy on 2017/3/13.
 */

public class SwipeBackLayout extends ViewGroup {
    final String TAG_LOG = "SwipeBackLayout";
    /**
     * 拖动辅助器
     */
    ViewDragHelper viewDragHelper;
    /**
     * 上下文context
     */
    Context context;
    /**
     * 内容view
     */
    View contentView;
    /**
     * 内容view长度
     */
    int contentWidth;
    /**
     * 默认的敏感度
     */
    final float defaultSensitivity = 1.0f;
    /**
     * 返回的长度所需要的百分比
     */
    float backPercent = 0.3f;
    /**
     * 是否处于返回状态
     */
    boolean isBack;
    /**
     * back事件完成监听器
     */
    onSwipeBackLisener onSwipeBackLisener;

    public SwipeBackLayout(Context context) {
        this(context, null);
    }

    public SwipeBackLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public SwipeBackLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        contentView = getChildAt(0);
    }

    /**
     * view完成填充时被调用
     */

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        if(getChildCount()>1){
            throw new RuntimeException("sorry swipebackLayout should have less than 2 child!");
        }
        contentWidth = getMeasuredWidth();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //默认的进场有一个渐变动画
        contentView.layout(l , t, r , b);
//        contentView.layout(l + contentWidth, t, r + contentWidth, b);
        //之后移动到指定位置
//        viewDragHelper.smoothSlideViewTo(contentView, contentWidth, 0);
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
     * 设置监听器
     *
     * @param onSwipeBackLisener
     */
    public void setOnSwipeBackLisener(SwipeBackLayout.onSwipeBackLisener onSwipeBackLisener) {
        this.onSwipeBackLisener = onSwipeBackLisener;
    }

    /**
     * 设置ContentView
     *
     * @param resourceId
     */
    public void setContentView(@LayoutRes int resourceId) {
        View view = LayoutInflater.from(context).inflate(resourceId, null);
        this.setContentView(view);
    }

    /**
     * 设置ContentView
     *
     * @param contentView
     */
    public void setContentView(View contentView) {
        this.contentView = contentView;
        if (contentView != null) {
            removeView(contentView);
            addView(contentView, 0);
        }
    }

    /**
     * 将默认的touch事件交给viewDragHelper处理
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    /**
     * 将默认的touch事件交给viewDragHelper处理
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }

    /**
     * 更新swipe的状态
     *
     * @param isBack 是否返回
     */
    void updateSwipeState(boolean isBack) {
        if (isBack) {
            viewDragHelper.settleCapturedViewAt(contentWidth, 0);
            this.isBack=true;
        } else {
            viewDragHelper.settleCapturedViewAt(0, 0);
            this.isBack=false;
        }
    }

    /**
     * 返回方法，因为考虑到该布局多和Activity及Fragment联动
     * 所以这里添加关闭方法,但是相应的调用还需在组件外部进行
     */
    public void goBack(){
        invalidate();
        viewDragHelper.smoothSlideViewTo(contentView,contentWidth,0);
    }
    /**
     * 初始化
     */
    void init() {
        viewDragHelper = ViewDragHelper.create(this, defaultSensitivity, new CustomDragCallback());
        isBack=false;
    }

    class CustomDragCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            //每次滑动时默认均处理,因为只有一个child
            if (child == contentView) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            int offset;
            offset = Math.max(Math.min(left, contentWidth), 0);
            Log.i(TAG_LOG, "offset-->" + offset);
            return offset;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            //纵向不处理
            return super.clampViewPositionVertical(child, top, dy);
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return 1;
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return super.getViewVerticalDragRange(child);
        }

        /**
         * 当触点松开时回调，用来完成回弹的效果
         *
         * @param releasedChild
         * @param xvel          松手时x轴方向上的速率，可以粗略的判断手势是否为甩
         * @param yvel
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (releasedChild == contentView) {
                int dividerLeft;
                dividerLeft = (int) (contentWidth * backPercent);
                Log.i(TAG_LOG, "dividerLeft-->" + dividerLeft);
                Log.i(TAG_LOG, "left-->" + releasedChild.getLeft());
                if (releasedChild.getLeft() <= dividerLeft) {
                    updateSwipeState(false);
                } else {
                    updateSwipeState(true);
                }
            }
            invalidate();
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (changedView == contentView) {
                if (contentView.getLeft() == contentWidth) {
                    if (onSwipeBackLisener != null) {
                        onSwipeBackLisener.onBackFinished();
                    }
                }
            }
        }
    }

    public interface onSwipeBackLisener {
        void onBackFinished();
    }
}
