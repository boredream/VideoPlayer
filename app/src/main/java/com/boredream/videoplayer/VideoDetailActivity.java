package com.boredream.videoplayer;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.boredream.videoplayer.video.DefaultOnVideoControlListener;
import com.boredream.videoplayer.video.VideoControllerView;
import com.boredream.videoplayer.video.bean.VideoDetailInfo;
import com.boredream.videoplayer.video.utils.DisplayUtils;

public class VideoDetailActivity extends AppCompatActivity {


    public static void start(Context context, VideoDetailInfo info) {
        Intent intent = new Intent(context, VideoDetailActivity.class);
        intent.putExtra("info", info);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);

        VideoDetailInfo info = (VideoDetailInfo) getIntent().getSerializableExtra("info");

        VideoControllerView vv = (VideoControllerView) findViewById(R.id.vv);
        vv.setOnVideoControlListener(new DefaultOnVideoControlListener() {
            @Override
            public void onFullScreen() {
                DisplayUtils.toggleScreenOritation(VideoDetailActivity.this);
            }
        });
        vv.startPlayVideo(info);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    public void onBackPressed() {
        if (!DisplayUtils.isPortrait(this)) {
            DisplayUtils.toggleScreenOritation(this);
        } else {
            super.onBackPressed();
        }
    }
}
