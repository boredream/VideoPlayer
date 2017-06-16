package com.boredream.videoplayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.boredream.videoplayer.video.bean.VideoDetailInfo;
import com.boredream.videoplayer.video.utils.MockUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoDetailInfo info = MockUtils.mockData(VideoDetailInfo.class);
                VideoDetailActivity.start(MainActivity.this, info);
            }
        });

    }
}
