package com.boredream.videoplayer.video;

import android.content.Context;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.boredream.videoplayer.R;
import com.boredream.videoplayer.VideoBehaviorView;
import com.boredream.videoplayer.video.bean.IVideoInfo;
import com.boredream.videoplayer.video.player.SimplePlayerCallback;
import com.boredream.videoplayer.video.player.VideoPlayer;

public class VideoView extends VideoBehaviorView {

    private SurfaceView mSurfaceView;
    private View mLoading;
    private VideoController mediaController;
    private VideoSystemOverlay videoSystemOverlay;
    private VideoProgressOverlay videoProgressOverlay;
    private VideoPlayer mMediaPlayer;

    private IVideoInfo video;
    private int initWidth;
    private int initHeight;

    public boolean isLock() {
        return mediaController.isLock();
    }

    public VideoView(Context context) {
        super(context);
        init();
    }

    public VideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.video_controller_container, this);

        mSurfaceView = (SurfaceView) findViewById(R.id.video_surface);
        mLoading = findViewById(R.id.video_loading);
        mediaController = (VideoController) findViewById(R.id.video_controller);
        videoSystemOverlay = (VideoSystemOverlay) findViewById(R.id.video_system_overlay);
        videoProgressOverlay = (VideoProgressOverlay) findViewById(R.id.video_progress_overlay);

        initPlayer();

        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                initWidth = getWidth();
                initHeight = getHeight();

                if (mMediaPlayer != null) {
                    mMediaPlayer.setDisplay(holder);
                    mMediaPlayer.openVideo();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                release();
            }
        });
    }

    private void initPlayer() {
        mMediaPlayer = new VideoPlayer();
        mMediaPlayer.setCallback(new SimplePlayerCallback() {
            // TODO: 2017/6/13

            @Override
            public void onError(MediaPlayer mp, int what, int extra) {
                super.onError(mp, what, extra);

                // TODO: 2017/6/16  
            }

            @Override
            public void onLoadingChanged(boolean isShow) {
                if (isShow) showLoading();
                else hideLoading();
            }

            @Override
            public void onPrepared(MediaPlayer mp) {
                super.onPrepared(mp);

                mMediaPlayer.start();
                mediaController.show();
            }
        });
        mediaController.setMediaPlayer(mMediaPlayer);
    }

    private void showLoading() {
        mLoading.setVisibility(VISIBLE);
    }

    private void hideLoading() {
        mLoading.setVisibility(GONE);
    }

    private int reConnect = 0;
    private long reConnectPosition = 0;

    private void reConnect() {
        if (mMediaPlayer.getVideoPath() != null && reConnect < 2) {
            // 重连两次
            reConnect++;
            reConnectPosition = mMediaPlayer.getCurrentPosition();
            mMediaPlayer.stop();
            mMediaPlayer.start();
        } else {
            reConnect = 0;
            reConnectPosition = 0;
//            checkShowError(false);
        }
    }

    private boolean isBackgroundPause;

    public void onPause() {
        if (mMediaPlayer.isPlaying()) {
            // 如果已经开始且在播放，则暂停同时记录状态
            Log.i("DDD", "isBackgroundPause");
            isBackgroundPause = true;
            playerPause();
        }
    }

    public void onResume() {
        if (isBackgroundPause) {
            // 如果切换到后台暂停，后又切回来，则继续播放
            Log.i("DDD", "isBackgroundPause resume");
            isBackgroundPause = false;
            playerStart();
        }
    }

    public void release() {
        mMediaPlayer.stop();
        mediaController.release();
    }

    /**
     * 开始播放
     */
    public void startPlayVideo(final IVideoInfo video) {
        if (video == null) {
            return;
        }

        this.video = video;

        reset();

        String videoPath = video.getVideoPath();
        mediaController.setTitle(video.getVideoTitle());
        mMediaPlayer.setVideoPath(videoPath);
    }

    private void reset() {
        // 先停止上一个
        mMediaPlayer.stop();
        reConnect = 0;
        reConnectPosition = 0;
    }

    private void playerPause() {
        mMediaPlayer.pause();
        Log.i("DDD", "playerPause");
    }

    private void playerStart() {
        reset();

        mMediaPlayer.start();
        Log.i("DDD", "playerStart");
    }

    private void reload() {
        mMediaPlayer.stop();
        mMediaPlayer.start();

        showLoading();
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        mediaController.toggleDisplay();
        return super.onSingleTapUp(e);
    }

    @Override
    protected void endGesture(int behaviorType) {
        switch (behaviorType) {
            case FINGER_BEHAVIOR_BRIGHTNESS:
            case FINGER_BEHAVIOR_VOLUME:
                Log.i("DDD", "endGesture: left right");
                videoSystemOverlay.hide();
                break;
            case FINGER_BEHAVIOR_PROGRESS:
                Log.i("DDD", "endGesture: bottom");
                mMediaPlayer.seekTo(videoProgressOverlay.getProgress());
                videoProgressOverlay.hide();
                break;
        }
    }

    @Override
    protected void updateSeekUI(int delProgress) {
        videoProgressOverlay.show(delProgress, mMediaPlayer.getCurrentPosition(), mMediaPlayer.getDuration());
    }

    @Override
    protected void updateVolumeUI(int max, int progress) {
        videoSystemOverlay.show(VideoSystemOverlay.SystemType.VOLUME, max, progress);
    }

    @Override
    protected void updateLightUI(int max, int progress) {
        videoSystemOverlay.show(VideoSystemOverlay.SystemType.BRIGHTNESS, max, progress);
    }

    private OnVideoControlListener onVideoControlListener;

    public void setOnVideoControlListener(OnVideoControlListener onVideoControlListener) {
        this.onVideoControlListener = onVideoControlListener;
        mediaController.setOnVideoControlListener(onVideoControlListener);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            getLayoutParams().width = initWidth;
            getLayoutParams().height = initHeight;
        } else {
            getLayoutParams().width = LayoutParams.MATCH_PARENT;
            getLayoutParams().height = LayoutParams.MATCH_PARENT;
        }

    }
}
