package com.boredream.videoplayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.boredream.videoplayer.video.VideoControllerView;
import com.boredream.videoplayer.video.bean.VideoDetailInfo;
import com.boredream.videoplayer.video.utils.MockUtils;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final VideoControllerView vv = (VideoControllerView) findViewById(R.id.vv);
        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoDetailInfo info = MockUtils.mockData(VideoDetailInfo.class);
                info.defaultDefinition = "common";
                info.setCurDefinition(info.defaultDefinition);
                info.vedioUrlDto = new HashMap<>();
                info.vedioUrlDto.put(info.defaultDefinition, "http://baobab.wdjcdn.com/1455782903700jy.mp4");
                vv.startPlayVideo(info);
            }
        });

    }
}
