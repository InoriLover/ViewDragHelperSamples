package com.viewdraghelpersamples.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.viewdraghelpersamples.R;
import com.viewdraghelperwidget.DragOutLayout;


/**
 * Created by Fishy on 2017/3/2.
 */

public class DragOutLayoutSample extends AppCompatActivity {
    DragOutLayout dragOutLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_dragoutlayout);
        dragOutLayout = (DragOutLayout) findViewById(R.id.dragOutLayout);
        dragOutLayout.setContentView(R.layout.layout_content_view);
        dragOutLayout.setExtraView(R.layout.layout_extra_view);
        dragOutLayout.setOpenExtraPercent(0.3f);
        dragOutLayout.setCloseExtraPersent(0.3f);
        dragOutLayout.setOnExtraOpenStateChangeListener(new DragOutLayout.OnExtraOpenStateChangeListener() {
            @Override
            public void onOpenStateChanged(boolean isOpen) {
                if (isOpen) {
                    Toast.makeText(DragOutLayoutSample.this, "打开", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DragOutLayoutSample.this, "关闭", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button btnContent = (Button) findViewById(R.id.content);
        Button btnExtra = (Button) findViewById(R.id.extra);
        btnContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dragOutLayout.openExtraView();
            }
        });
        btnExtra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dragOutLayout.closeExtraView();
            }
        });
    }

}
