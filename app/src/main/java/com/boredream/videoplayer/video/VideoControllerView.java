package com.boredream.videoplayer.video;

import android.content.Context;
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
                Log.i("DDD", "surfaceCreated: ");
                if(mMediaPlayer != null) {
                    mMediaPlayer.setDisplay(holder);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.i("DDD", "surfaceChanged: ");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.i("DDD", "surfaceDestroyed: ");
                release();
            }
        });
    }

    private void initPlayer() {
        mMediaPlayer = new VideoPlayer();
        mMediaPlayer.setCallback(new SimplePlayerCallback() {
            // TODO: 2017/6/13

            @Override
            public void onPrepared(MediaPlayer mp) {
                super.onPrepared(mp);
                playerStart();
            }
        });
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
        if(mMediaPlayer != null && mMediaPlayer.getVideoPath() != null && reConnect < 2) {
            // 重连两次
            reConnect ++;
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
        if (mMediaPlayer == null) {
            return;
        }

        if (mMediaPlayer.isPlaying()) {
            // 如果已经开始且在播放，则暂停同时记录状态
            Log.i("DDD", "isBackgroundPause");
            isBackgroundPause = true;
            playerPause();
        }
    }

    public void onResume() {
        if (mMediaPlayer == null) {
            return;
        }

        if (isBackgroundPause) {
            // 如果切换到后台暂停，后又切回来，则继续播放
            Log.i("DDD", "isBackgroundPause resume");
            isBackgroundPause = false;
            playerStart();
        }
    }

    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }

//        if (mWeakReferenceHandler != null) {
//            mWeakReferenceHandler.cancel();
//            mWeakReferenceHandler = null;
//        }
    }

    public void clearVideo() {
        if(isPlayLocalVideo) return;

        video = null;
        reset();
    }

    /**
     * 开始播放
     */
    public void startPlayVideo(final VideoDetailInfo video) {
        if(video == null) {
            return;
        }

        this.video = video;

        // TODO: 2017/6/13
//        isPlayLocalVideo = startWithLocal;

        // TODO: 2017/6/13 check local video
        reset();

        String videoPath = video.getVideoUrl();
        mMediaPlayer.setVideoPath(videoPath);

//        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
//            @Override
//            public void surfaceCreated(SurfaceHolder holder) {
//                String videoPath = video.getVideoUrl();
//                String videoPath = AVProvider.getDownloadedMediaPath(DownloadInfo.VIDEO, video.vedioId);
//                // 如果新的视频和正在播放的是同一个，则不重复start
//                if(videoPath != null
//                        && mMediaPlayer != null
//                        && mMediaPlayer.isStart()
//                        && mMediaPlayer.isPlaying()
//                        && videoPath.equals(mMediaPlayer.getDataSource()+"")) {
//                    return;
//                }

//                reset();
//                    if (videoPath != null) {
//                        isPlayLocalVideo = true;
//                    } else {
//                        videoPath = video.getVideoUrl();
//                        isPlayLocalVideo = false;
//
//                        // 先判断网络情况是否为手机流量
//                        if (NetworkUtils.isMobileConnected(getContext()) &&
//                                !NetworkUtils.isWifiConnected(getContext())) {
//
//                            // 再判断是否已经同意了手机流量播放
//                            if (!mAllowUnWifiPlay) {
//                                mErrorView.showError(VideoErrorView.STATUS_UN_WIFI_ERROR);
//                                return;
//                            }
//                        }
//                    }
//
//                Log.i("DDD", "startPlayVideo: " + isPlayLocalVideo + " = " + videoPath);
//                mMediaPlayer.setVideoPath(videoPath);
//                mMediaPlayer.start();
//            }
//
//            @Override
//            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//
//            }
//
//            @Override
//            public void surfaceDestroyed(SurfaceHolder holder) {
//
//            }
//        });
    }

    private void reset() {
        // 先停止上一个
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
        isPlayLocalVideo = false;
        reConnect = 0;
        reConnectPosition = 0;
    }

    private void playerPause() {
        if (mMediaPlayer == null) return;
        mMediaPlayer.pause();
        Log.i("DDD", "playerPause");

        // TODO: 2017/6/7 暂停的时候控件不隐藏
//        mWeakReferenceHandler.cancel();
    }

    private void playerStart() {
        if (mMediaPlayer == null) return;

        reset();

        mMediaPlayer.start();
        Log.i("DDD", "playerStart");
    }

    private void reload() {
        if (mMediaPlayer == null) return;
        mMediaPlayer.stop();
        mMediaPlayer.start();

        showLoading();
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        mediaController.toggleDisplay();
        return super.onSingleTapUp(e);
    }

    private OnVideoControlListener onVideoControlListener;

    public void setOnVideoControlListener(OnVideoControlListener onVideoControlListener) {
        this.onVideoControlListener = onVideoControlListener;
    }
}
