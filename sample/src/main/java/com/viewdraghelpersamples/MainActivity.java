package com.viewdraghelpersamples;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.viewdraghelpersamples.sample.DragOutLayoutSample;
import com.viewdraghelpersamples.sample.PullUpLayoutSample;
import com.viewdraghelpersamples.sample.SwipeBackLayoutSample;


public class MainActivity extends AppCompatActivity {
    Button btnDragoutLayout;
    Button btnPullupLayout;
    Button btnSwipeBackLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnDragoutLayout= (Button) findViewById(R.id.sampleDragOutLayout);
        btnPullupLayout= (Button) findViewById(R.id.samplePullupLayout);
        btnSwipeBackLayout= (Button) findViewById(R.id.sampleSwipeBackLayout);
        btnDragoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,
                        DragOutLayoutSample.class));
            }
        });
        btnPullupLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,
                        PullUpLayoutSample.class));
            }
        });
        btnSwipeBackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,
                        SwipeBackLayoutSample.class));
            }
        });
    }
}
