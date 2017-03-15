package com.viewdraghelpersamples.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.viewdraghelpersamples.R;
import com.viewdraghelperwidget.PullUpLayout;


/**
 * Created by Fishy on 2017/3/3.
 */

public class PullUpLayoutSample extends AppCompatActivity{
    PullUpLayout pullUpLayout;
    ImageView imgSnow;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_pulluplayout);

        pullUpLayout= (PullUpLayout) findViewById(R.id.pullUpLayout);
        pullUpLayout.enableOutsideTouchClose(true);
        pullUpLayout.setOnPullupViewStateChangeListener(new PullUpLayout.onPullupViewStateChangeListener() {
            @Override
            public void onPullupViewStateChanged(boolean isOpen) {
                if(isOpen){
                    Toast.makeText(PullUpLayoutSample.this,"已打开",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(PullUpLayoutSample.this,"已关闭",Toast.LENGTH_SHORT).show();
                }

            }
        });
        imgSnow= (ImageView) findViewById(R.id.imgSnow);
        imgSnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pullUpLayout.isPullupViewOpen()){
                    pullUpLayout.closePullUpView();
                }else{
                    pullUpLayout.openPullUpView();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(pullUpLayout.isPullupViewOpen()){
            pullUpLayout.closePullUpView();
        }else{
            super.onBackPressed();
        }
    }
}
