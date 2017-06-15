package com.boredream.videoplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);

        VideoView vv = (VideoView) findViewById(R.id.vv);

        vv.setVideoPath("http://baobab.wdjcdn.com/1455782903700jy.mp4");
        vv.setMediaController(new MediaController(this));
        vv.requestFocus();
    }
}
