package com.viewdraghelperwidget;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Fishy on 2017/3/2.
 */

public class DragOutLayout extends ViewGroup {
    final String TAG_LOG = "DragOutLayout";
    /**
     * 拖动辅助器
     */
    ViewDragHelper viewDragHelper;
    /**
     * 上下文context
     */
    Context context;
    /**
     * 默认的敏感度
     */
    final float defaultSensitivity = 1.0f;
    /**
     * 内容view
     */
    View contentView;
    /**
     * 额外view
     */
    View extraView;
    /**
     * 整个布局的高度
     */
    int layoutHeight;
    /**
     * 内容view的宽度
     */
    int contentWidth;
    /**
     * 额外view的宽度
     */
    int extraWidth;
    /**
     * 额外view是否处于打开状态
     */
    boolean isExtraViewOpen = false;
    /**
     * 打开extraView所需要的长度百分比
     */
    float openExtraPercent = 0.3f;
    /**
     * 关闭extraView所需要的长度百分比
     */
    float closeExtraPersent = 0.3f;
    /**
     * extra视图开关状态监听器
     */
    OnExtraOpenStateChangeListener onExtraOpenStateChangeListener;

    public DragOutLayout(Context context) {
        this(context, null);
    }

    public DragOutLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragOutLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    /**
     * 初始化
     */
    void init() {
        //viewDragHelper实例化
        viewDragHelper = ViewDragHelper.create(this, defaultSensitivity, new CustomDragCallback());
    }

    /**
     * view完成填充时被调用
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        //如果超过2个child则报错
        if (childCount > 2) {
            throw new RuntimeException("the child is more than 2!");
        }
        if (childCount == 2) {
            contentView = getChildAt(0);
            extraView = getChildAt(1);
        }
    }

    /**
     * extra视图是否打开
     *
     * @return
     */
    public boolean isExtraViewOpen() {
        return isExtraViewOpen;
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

    public void setOnExtraOpenStateChangeListener(OnExtraOpenStateChangeListener onExtraOpenStateChangeListener) {
        this.onExtraOpenStateChangeListener = onExtraOpenStateChangeListener;
    }

    /**
     * 设置内容view
     *
     * @param contentView
     */
    public void setContentView(View contentView) {
        if (contentView != null) {
            removeView(contentView);
        }
        this.contentView = contentView;
        addView(contentView, 0);
    }

    /**
     * 设置内容view资源id
     *
     * @param layoutId
     */
    public void setContentView(@LayoutRes int layoutId) {
        View view = LayoutInflater.from(context).inflate(layoutId, null);
        this.setContentView(view);
    }

    /**
     * 设置额外view
     *
     * @param extraView
     */
    public void setExtraView(View extraView) {
        if (extraView != null) {
            removeView(extraView);
        }
        this.extraView = extraView;
        addView(extraView, 1);
    }

    /**
     * 设置额外view资源id
     *
     * @param layoutId
     */
    public void setExtraView(@LayoutRes int layoutId) {
        View view = LayoutInflater.from(context).inflate(layoutId, null);
        this.setExtraView(view);
    }

    /**
     * 设置打开extra的阈值度，取值0~1，越小越容易打开
     *
     * @param openExtraPercent
     */
    public void setOpenExtraPercent(float openExtraPercent) {
        this.openExtraPercent = openExtraPercent;
    }

    /**
     * 设置打开extra的阈值度，取值0~1，越小越容易关闭
     *
     * @param closeExtraPersent
     */
    public void setCloseExtraPersent(float closeExtraPersent) {
        this.closeExtraPersent = closeExtraPersent;
    }

    /**
     * 打开额外的view
     */
    public void openExtraView() {
        viewDragHelper.smoothSlideViewTo(contentView, -extraWidth, 0);
        if (onExtraOpenStateChangeListener != null) {
            onExtraOpenStateChangeListener.onOpenStateChanged(true);
        }
        invalidate();
        isExtraViewOpen = true;
    }

    /**
     * 关闭额外的view
     */
    public void closeExtraView() {
        viewDragHelper.smoothSlideViewTo(contentView, 0, 0);
        if (onExtraOpenStateChangeListener != null) {
            onExtraOpenStateChangeListener.onOpenStateChanged(false);
        }
        invalidate();
        isExtraViewOpen = false;
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //得到测绘模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //得到测绘的尺寸
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        //最后需要设置的宽、高
        int width;
        int height;
        try {
            measureChildren(widthMeasureSpec, heightMeasureSpec);
        } catch (NullPointerException e) {
            throw new RuntimeException("not set contentView or extraView!");
        }
        //根据mode重新设置尺寸
        //默认将wrap_content模式当成match_parent处理
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = widthSize;
        }

        //高度需处理wrap_content的情况
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            int paddingTop = getPaddingTop();
            int paddingBottom = getPaddingBottom();
            height = contentView.getMeasuredHeight() + paddingTop + paddingBottom;
        } else {
            height = heightSize;
        }

        //给子view宽高赋值
        layoutHeight = height;
        contentWidth = width;
        extraWidth = extraView.getMeasuredWidth();
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
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

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        contentView.layout(l, t, l + contentWidth, t + layoutHeight);
        extraView.layout(l + contentWidth, t, l + contentWidth + extraWidth, t + layoutHeight);
    }

    /**
     * 更新extraView的打开状态
     *
     * @param isOpen      是否打开
     * @param isExtraView 操作对象是否为extraView
     */
    void updateExtraOpenState(boolean isOpen, boolean isExtraView) {
        if (isExtraView) {
            if (isOpen) {
                viewDragHelper.settleCapturedViewAt(contentWidth - extraWidth, 0);
            } else {
                viewDragHelper.settleCapturedViewAt(contentWidth, 0);
            }
        } else {
            if (isOpen) {
                viewDragHelper.settleCapturedViewAt(-extraWidth, 0);
            } else {
                viewDragHelper.settleCapturedViewAt(0, 0);
            }
        }
        //更新状态并刷新
        if(isExtraViewOpen!=isOpen){
            isExtraViewOpen = isOpen;
            //监听器发出信号
            if (onExtraOpenStateChangeListener != null) {
                onExtraOpenStateChangeListener.onOpenStateChanged(isOpen);
            }
        }
        invalidate();
    }

    class CustomDragCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            if (child == contentView || child == extraView) {
                return true;
            } else {
                return false;
            }
        }

        /**
         * 处理横向滑动的逻辑处理<p>需要注意的是如果已经通过逻辑停止滑动时，
         * 该值会保持在假设可滑动情况下，会到达的位置</p>
         *
         * @param child 处理的子view对象
         * @param left  滑动时，view的left位置尝试移动到的位置的x坐标
         * @param dx    每次调用时尝试去移动的x轴上的距离
         * @return
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
//            Log.i(TAG_LOG, "left-->" + left);
//            Log.i(TAG_LOG, "dx-->" + dx);
//            Log.i(TAG_LOG, "width-->" + contentWidth);
//            Log.i(TAG_LOG, "extra_left-->" + extraView.getLeft());
            //偏移量,默认值为0，表示不发生任何偏移
            int offset = 0;
            if (child == contentView) {
                //当滑动对象是内容view时
                //这句的逻辑和底下注释掉的逻辑是一样的
                offset = Math.max(Math.min(left, 0), -extraWidth);
//                if (left < -extraWidth) {
//                    offset = -extraWidth;
//                } else if (left > 0) {
//                    offset = 0;
//                } else {
//                    offset = left;
//                }
            } else if (child == extraView) {
                //当滑动对象是额外view时
                offset = Math.max(contentWidth - extraWidth, Math.min(left, contentWidth));
            } else {
                return 0;
            }
            return offset;
        }

        /**
         * 只能横向拖动，竖直方向不做处理
         *
         * @param child
         * @param top
         * @param dy
         * @return
         */
        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return super.clampViewPositionVertical(child, top, dy);
        }

        /**
         * 当view的位置发生改变时调用
         *
         * @param changedView
         * @param left        新的left的x坐标
         * @param top
         * @param dx
         * @param dy
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            //View位置改变时，刷新界面
            //为什么不在extra视图layout改变后刷新呢？这边有一个bug，回弹有时候不会触发，
            // 原理没有搞清楚，但是在重新layout之前调用就不会出现
            invalidate();
//            Log.i(TAG_LOG, "positionChanged-->");
//            Log.i(TAG_LOG, "left-->" + left);
            if (changedView == contentView) {
                //因为left区间在-extraWidth~0之间
                int offset = left;
                extraView.layout(contentWidth + offset, 0, contentWidth + extraWidth + offset, layoutHeight);
            } else if (changedView == extraView) {
                int offset = left - contentWidth;
//                Log.i(TAG_LOG,"newLeft-->"+left);
                contentView.layout(offset, 0, contentWidth + offset, layoutHeight);
            }
//            invalidate();
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
//            Log.i(TAG_LOG, "xvel-->" + xvel);
            if (releasedChild == contentView) {
                int dividerLeft;
                //先判断当前是否开启
                //给出dividerLeft赋值，定下打开或关闭的分界线
                if (isExtraViewOpen) {
                    dividerLeft = (int) (-extraWidth * (1 - closeExtraPersent));
                } else {
                    dividerLeft = (int) (-extraWidth * openExtraPercent);
                }
                if (releasedChild.getLeft() <= dividerLeft) {
                    updateExtraOpenState(true, false);
                } else {
                    updateExtraOpenState(false, false);
                }
            } else if (releasedChild == extraView) {
                int dividerLeft;
                //先判断当前是否开启
                //给出dividerLeft赋值，定下打开或关闭的分界线
                if (isExtraViewOpen) {
                    dividerLeft = (int) (contentWidth - extraWidth * (1 - closeExtraPersent));
                } else {
                    dividerLeft = (int) (contentWidth - extraWidth * openExtraPercent);
                }
                if (releasedChild.getLeft() <= dividerLeft) {
                    updateExtraOpenState(true, true);
                } else {
                    updateExtraOpenState(false, true);
                }
            }
        }

        /**
         * 重写该方法用法判断是否可以对可响应点击事件的控件进行滑动操作，返回值为0时不处理
         *
         * @param child
         * @return
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return 1;
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return super.getViewVerticalDragRange(child);
        }
    }

    /**
     * 监听器，负责监听extra视图的开关状态改变
     */
    public interface OnExtraOpenStateChangeListener {
        /**
         * 当extra视图的开关状态发生变化时回调
         *
         * @param isOpen
         */
        void onOpenStateChanged(boolean isOpen);
    }
}
