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
import com.boredream.videoplayer.video.bean.VideoDetailInfo;
import com.boredream.videoplayer.video.player.SimplePlayerCallback;
import com.boredream.videoplayer.video.player.VideoPlayer;

public class VideoControllerView extends VideoBehaviorView {

    private SurfaceView mSurfaceView;
    private View mLoading;
    private MediaController mediaController;
    private VideoPlayer mMediaPlayer;

    private boolean mIsScreenLock;
    private boolean isPlayLocalVideo;
    private VideoDetailInfo video;
    private int initWidth;
    private int initHeight;

    public boolean isLocked() {
        return mIsScreenLock;
    }

    public VideoControllerView(Context context) {
        super(context);
        init();
    }

    public VideoControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VideoControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.video_controller_container, this);

        mSurfaceView = (SurfaceView) findViewById(R.id.video_surface);
        mLoading = findViewById(R.id.video_loading);
        mediaController = (MediaController) findViewById(R.id.video_controller);

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
        Log.i("DDD", "showLoading");
        mLoading.setVisibility(VISIBLE);
    }

    private void hideLoading() {
        Log.i("DDD", "hideLoading");
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

//        if (mWeakReferenceHandler != null) {
//            mWeakReferenceHandler.cancel();
//            mWeakReferenceHandler = null;
//        }
    }

    public void clearVideo() {
        if (isPlayLocalVideo) return;

        video = null;
        reset();
    }

    /**
     * 开始播放
     */
    public void startPlayVideo(final VideoDetailInfo video) {
        if (video == null) {
            return;
        }

        this.video = video;

        // TODO: 2017/6/13
//        isPlayLocalVideo = startWithLocal;

        // TODO: 2017/6/13 check local video
        reset();

        String videoPath = video.getVideoUrl();
        mMediaPlayer.setVideoPath(videoPath);
    }

    private void reset() {
        // 先停止上一个
        mMediaPlayer.stop();
        isPlayLocalVideo = false;
        reConnect = 0;
        reConnectPosition = 0;
    }

    private void playerPause() {
        mMediaPlayer.pause();
        Log.i("DDD", "playerPause");

        // TODO: 2017/6/7 暂停的时候控件不隐藏
//        mWeakReferenceHandler.cancel();
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
    protected void updateSeekUI(int delProgress) {
        // TODO: 2017/6/15
    }

    @Override
    protected void updateVolumeUI(int maxVolume, int curVolume) {

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
