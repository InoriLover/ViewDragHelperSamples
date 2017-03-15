package com.viewdraghelpersamples;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.lang.reflect.Field;

/**
 * Created by Fishy on 2017/2/3.
 * --version 0.5.0
 */

public abstract class BaseAlphaActivity extends AppCompatActivity {
    private ViewGroup root;
    private Mode alphaMode;
    private DrawerLayout drawerLayout;
    private View fakeStatusBarView;
    //默认不带蒙版效果
    int defaultAlpha = 0;
    boolean isStatusbarSetted = false;

    protected enum Mode {
        /**
         * 普通的颜色bar
         */
        NORMAL_COLOR,
        /**
         * 头部含图片
         */
        IMAGE_TOOLBAR,
        /**
         * 带抽屉布局
         */
        DRAWER_LAYOUT
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = createRootLayout(this);
        alphaMode = setAlphaMode();
        super.setContentView(root);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isStatusbarSetted = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        createAlphaStatusBar(this);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        setContentView(View.inflate(this, layoutResID, null));
    }

    @Override
    public void setContentView(View view) {
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        //当为普通颜色设置并在4.4版本的情况下，需要设置paddingTop来适应
        if (setAlphaMode() == Mode.NORMAL_COLOR && Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + getStatusBarHeight(this),
                    view.getPaddingRight(), view.getPaddingBottom());
        }
        root.addView(view, params);
    }

    /**
     * 生成根布局
     * @param context
     */
    private ViewGroup createRootLayout(Context context) {
        FrameLayout root = new FrameLayout(context);
        root.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        return root;
    }

    /**
     * 创建透明状态栏
     * @param context
     */
    private void createAlphaStatusBar(Context context) {
        if (!isStatusbarSetted) {
            switch (alphaMode) {
                case NORMAL_COLOR:
                    createColorStatusBar(context);
                    break;
                case IMAGE_TOOLBAR:
                    createTransparentStatusBar();
                    break;
                case DRAWER_LAYOUT:
                    createDrawerLayoutStatusBar(context);
                    break;
            }
            addTransparentView(context);
            isStatusbarSetted = true;
        }
    }

    /**
     * 创建指定颜色的状态栏
     * 对应mode为NORMAL_COLOR
     * @param context
     */
    private void createColorStatusBar(Context context) {
        //对于5.0以上和4.4做不同的处理
        //4.3及以下不做处理
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(calculateStatusColor(setStatusBarColor(), setTransparentViewAlpha()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            View view = new View(this);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    getStatusBarHeight(context)));
            view.setBackgroundColor(setStatusBarColor());
            root.addView(view, 0);
        }
    }

    /**
     * 创建适应图片头部的透明状态栏
     * 对应mode为IMAGE_TOOLBAR
     *
     */
    private void createTransparentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M&&!isUseLightStatusIcon()){
                getWindow()
                        .getDecorView()
                        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        |View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }else{
                getWindow()
                        .getDecorView()
                        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 是否使用灰色图标，默认为true，使用白色图标，仅6.0之后有效
     *
     * @return
     */
     protected boolean isUseLightStatusIcon(){
        return true;
    }
    /**
     * 创建和drawerlayout配合使用的状态栏
     * 对应mode为DRAWER_LAYOUT
     * @param context
     */
    private void createDrawerLayoutStatusBar(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        // 生成一个状态栏大小的矩形
        // 添加 statusBarView 到布局中
        drawerLayout = setDrawerLayout();
        ViewGroup contentLayout = (ViewGroup) drawerLayout.getChildAt(0);
        if (fakeStatusBarView != null) {
            if (fakeStatusBarView.getVisibility() == View.GONE) {
                fakeStatusBarView.setVisibility(View.VISIBLE);
            }
            fakeStatusBarView.setBackgroundColor(setStatusBarColor());
        } else {
            contentLayout.addView(createFakeStatusBarView(context, setStatusBarColor()), 0);
        }
        // 内容布局不是 LinearLayout 时,设置marginTop
        //用来腾出空间放置fakeStatusbarView
        if (!(contentLayout instanceof LinearLayout) && contentLayout.getChildAt(1) != null) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) contentLayout.getChildAt(1).getLayoutParams();
            params.setMargins(params.leftMargin, params.topMargin + getStatusBarHeight(context),
                    params.rightMargin, params.bottomMargin);
        }
        setDrawerLayoutProperty(drawerLayout, contentLayout);
    }

    /**
     * 设置 DrawerLayout 属性
     *
     * @param drawerLayout              DrawerLayout
     * @param drawerLayoutContentLayout DrawerLayout 的内容布局
     */
    private static void setDrawerLayoutProperty(DrawerLayout drawerLayout, ViewGroup drawerLayoutContentLayout) {
        ViewGroup drawer = (ViewGroup) drawerLayout.getChildAt(1);
        drawerLayout.setFitsSystemWindows(false);
        drawerLayoutContentLayout.setFitsSystemWindows(false);
        drawer.setFitsSystemWindows(false);
    }

    /**
     * 创造伪造的状态栏
     * @param context
     * @param color 状态栏的颜色
     */
    private View createFakeStatusBarView(Context context,@ColorInt int color) {
        View fakeView = new View(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                getStatusBarHeight(context));
        fakeView.setLayoutParams(params);
        fakeView.setBackgroundColor(color);
        fakeStatusBarView = fakeView;
        return fakeView;
    }

    /**
     * 计算状态栏颜色
     * 该算法用来计算在当前的颜色基调上加上黑色蒙版的效果
     * 与普通的给颜色设置透明度不一样
     *
     * @param color color值
     * @param alpha alpha值 取值在0~255之间
     * @return 最终的状态栏颜色
     */
    private int calculateStatusColor(@ColorInt int color, int alpha) {
        if (alpha == 0) {
            return color;
        }
        float a = 1 - alpha / 255f;
        int red = color >> 16 & 0xff;
        int green = color >> 8 & 0xff;
        int blue = color & 0xff;
        red = (int) (red * a + 0.5);
        green = (int) (green * a + 0.5);
        blue = (int) (blue * a + 0.5);
        return 0xff << 24 | red << 16 | green << 8 | blue;
    }

    /**
     * 设置状态栏的颜色
     * 在mode为IMAGE_TOOLBAR时无效
     *
     * @return
     */
    protected abstract int setStatusBarColor();

    /**
     * 设置透明状态栏的模式
     * 分三种：普通颜色模式，图片顶部透明模式，和DrawerLayout结合使用模式
     * 当使用模式IMAGE_TOOLBAR时，可以重写{@link BaseAlphaActivity#isUseLightStatusIcon()}
     * 默认返回true，使用白色样式图标
     *
     * @return
     */
    protected abstract Mode setAlphaMode();

    /**
     * 设置DrawerLayout
     * 当mode不是DrawerLayout时，传null即可
     *
     * @return
     */
    protected abstract DrawerLayout setDrawerLayout();

    /**
     * 添加黑色为底的透明蒙版View
     */
    private void addTransparentView(Context context) {
        if (alphaMode != Mode.NORMAL_COLOR || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            View transparentView = new View(context);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    getStatusBarHeight(context));
            transparentView.setLayoutParams(params);
            transparentView.setBackgroundColor(Color.argb(setTransparentViewAlpha(), 0, 0, 0));
            root.addView(transparentView);
        }
    }

    /**
     * 设置状态栏的黑色蒙版透明度
     * 取值在0~255之间
     */
    protected int setTransparentViewAlpha() {
        return defaultAlpha;
    }

    /**
     * 得到状态栏的高度
     *
     * @return 状态栏高度，单位：像素
     */
    private int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
//            Log.v("test", "the status bar height is : " + statusBarHeight);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }
}
