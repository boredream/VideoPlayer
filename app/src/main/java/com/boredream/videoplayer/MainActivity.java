package com.boredream.videoplayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.boredream.videoplayer.video.bean.VideoDetailInfo;
import com.boredream.videoplayer.video.utils.MockUtils;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoDetailInfo info = MockUtils.mockData(VideoDetailInfo.class);
                info.defaultDefinition = "common";
                info.setCurDefinition(info.defaultDefinition);
                info.vedioUrlDto = new HashMap<>();
                info.vedioUrlDto.put(info.defaultDefinition, "http://pili-av.qbaolive.com/video/42367dc661f8911a8b220f77ca778cd8a_1080.mp4");

                VideoDetailActivity.start(MainActivity.this, info);
            }
        });

    }
}
