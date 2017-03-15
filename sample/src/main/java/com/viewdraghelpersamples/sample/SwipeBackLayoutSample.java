package com.viewdraghelpersamples.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;

import com.viewdraghelpersamples.BaseAlphaActivity;
import com.viewdraghelpersamples.R;
import com.viewdraghelperwidget.SwipeBackLayout;


/**
 * Created by Fishy on 2017/3/13.
 */

public class SwipeBackLayoutSample extends BaseAlphaActivity {
    SwipeBackLayout swipeBackLayout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_swipebacklayout);
        swipeBackLayout= (SwipeBackLayout) findViewById(R.id.swipeBackLayout);
        //测试用，已注释
//        swipeBackLayout.setContentView(R.layout.layout_swipeback_content);
        swipeBackLayout.setOnSwipeBackLisener(new SwipeBackLayout.onSwipeBackLisener() {
            @Override
            public void onBackFinished() {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        swipeBackLayout.goBack();
    }

    @Override
    protected int setStatusBarColor() {
        return 0;
    }

    @Override
    protected Mode setAlphaMode() {
        return Mode.IMAGE_TOOLBAR;
    }

    @Override
    protected DrawerLayout setDrawerLayout() {
        return null;
    }

    @Override
    protected boolean isUseLightStatusIcon() {
        return false;
    }
}
