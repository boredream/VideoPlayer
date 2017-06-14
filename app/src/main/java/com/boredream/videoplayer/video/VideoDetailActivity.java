package com.boredream.videoplayer.video;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.boredream.videoplayer.R;
import com.boredream.videoplayer.video.bean.VideoDetailInfo;

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
        vv.startPlayVideo(info);

    }

}
