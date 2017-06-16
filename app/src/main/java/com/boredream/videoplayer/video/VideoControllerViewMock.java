package com.boredream.videoplayer.video;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.boredream.videoplayer.R;
import com.boredream.videoplayer.video.bean.VideoDetailInfo;
import com.boredream.videoplayer.video.player.SimplePlayerCallback;
import com.boredream.videoplayer.video.player.VideoPlayer;
import com.boredream.videoplayer.video.utils.DisplayUtils;
import com.boredream.videoplayer.video.utils.NetworkUtils;
import com.boredream.videoplayer.video.utils.StringUtils;

public class VideoControllerViewMock extends FrameLayout implements GestureDetector.OnGestureListener, View.OnTouchListener {

    public static final int DELAY_DISMISS_TIME = 4 * 1000; // 延迟消失事件

    private static final int FINGER_BEHAVIOR_PROGRESS = 0x01;  //进度调节
    private static final int FINGER_BEHAVIOR_VOLUME = 0x02;  //音量调节
    private static final int FINGER_BEHAVIOR_BRIGHTNESS = 0x03;  //亮度调节

    private SurfaceView mSurfaceView;
    private VideoPlayer mMediaPlayer;
    private View mControllerBack;
    private View mControllerTitle;
    private TextView mVideoTitle;
    private View mControllerBottom;
    private SeekBar mPlayerSeekBar;
    private ImageView mVideoPlayState;
    private TextView mVideoProgress;
    private TextView mVideoDuration;
    private TextView mVideoRatio;
    private TextView mVideoCatalog;
    private ImageView mVideoFullScreen;
    private VideoSystemOverlay mSystemUI;
    private VideoProgressOverlay mProgressDialog;
    private View mViewComplete;
    private Button mViewCompleteBack;
    private ImageView mScreenLock;
    private VideoErrorView mErrorView;
    private View mLoading;
    private TextView mVideoChangeFluency;

    private int mFingerBehavior;
    private float mCurrentVolume; // 鉴于音量范围值比较小 使用float类型施舍五入处理.
    private int mMaxVolume;
    private int mCurrentBrightness, mMaxBrightness;

    private boolean isVideoPanelShowing; // 面板正在显示中
    private boolean mIsChangeFluency; // 正在切换清晰度

    private AudioManager mAudioManager;
    private GestureDetector mGestureDetector;
    private boolean mIsScreenLock;
    private boolean mAllowUnWifiPlay;
    private boolean isPlayLocalVideo;
    private VideoDetailInfo video;

    public boolean isLocked() {
        return mIsScreenLock;
    }

    public VideoControllerViewMock(Context context) {
        super(context);
        init();
    }

    public VideoControllerViewMock(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VideoControllerViewMock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.video_controller_container, this);

        mSurfaceView = (SurfaceView) findViewById(R.id.video_surface);

        initPlayer();
        initControllerPanel();

        mGestureDetector = new GestureDetector(getContext().getApplicationContext(), this);
        setOnTouchListener(this);

        //初始化音量和亮度.
        mAudioManager = (AudioManager) (getContext().getSystemService(Context.AUDIO_SERVICE));
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mMaxBrightness = 255;

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

        showVideoPanel();
    }

    private void initControllerPanel() {
        // back
        mControllerBack = findViewById(R.id.video_back);
        mControllerBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onVideoControlListener != null) {
                    onVideoControlListener.onBack();
                }
            }
        });
        // top
        mControllerTitle = findViewById(R.id.video_controller_title);
        mVideoTitle = (TextView) mControllerTitle.findViewById(R.id.video_title);
        // bottom
        mControllerBottom = findViewById(R.id.video_controller_bottom);
        mPlayerSeekBar = (SeekBar) mControllerBottom.findViewById(R.id.player_seek_bar);
        mVideoPlayState = (ImageView) mControllerBottom.findViewById(R.id.player_pause);
        mVideoProgress = (TextView) mControllerBottom.findViewById(R.id.player_progress);
        mVideoDuration = (TextView) mControllerBottom.findViewById(R.id.player_duration);
        mVideoFullScreen = (ImageView) mControllerBottom.findViewById(R.id.video_full_screen);
        mVideoPlayState.setOnClickListener(mOnPlayerPauseClick);
        mVideoPlayState.setImageResource(R.drawable.ic_video_pause);
        mPlayerSeekBar.setOnSeekBarChangeListener(mSeekBarListener);
        mVideoFullScreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onVideoControlListener != null) {
                    onVideoControlListener.onFullScreen();
                }
            }
        });

        // lock
        mScreenLock = (ImageView) findViewById(R.id.player_lock_screen);
        mScreenLock.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsScreenLock) unlock();
                else lock();

                showVideoPanel();
            }
        });

        // error
        mErrorView = (VideoErrorView) findViewById(R.id.video_controller_error);
        mErrorView.setOnVideoControlListener(new DefaultOnVideoControlListener() {
            @Override
            public void onRetry(int status) {
                retry(status);
            }
        });

        // loading
        mLoading = findViewById(R.id.video_loading);
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onNetWorkChanged(NetChangeEvent event) {
//        if (event == null) {
//            return;
//        }
//
//        checkShowError(true);
//    }

    /**
     * 判断显示错误类型
     */
    public void checkShowError(boolean isNetChanged) {
        boolean isConnect = NetworkUtils.isNetworkConnected(getContext());
        boolean isMobileNet = NetworkUtils.isMobileConnected(getContext());
        boolean isWifiNet = NetworkUtils.isWifiConnected(getContext());

        if (isConnect) {
            // 如果已经联网
            if (mErrorView.getCurStatus() == VideoErrorView.STATUS_NO_NETWORK_ERROR && !(isMobileNet && !isWifiNet)) {
                // 如果之前是无网络，应该提示“网络已经重新连上，请重试”，这里暂不处理
            } else if (video == null) {
                // 优先判断是否有video数据
                showError(VideoErrorView.STATUS_VIDEO_DETAIL_ERROR);
            } else if (isMobileNet && !isWifiNet && !mAllowUnWifiPlay && !isPlayLocalVideo) {
                // 如果是手机流量，且未同意过播放，且非本地视频，则提示错误
                mErrorView.showError(VideoErrorView.STATUS_UN_WIFI_ERROR);

                if (isNetChanged) {
                    mMediaPlayer.pause();
                } else {
                    mMediaPlayer.stop();
                }
            } else if (isWifiNet && isNetChanged && mErrorView.getCurStatus() == VideoErrorView.STATUS_UN_WIFI_ERROR) {
                // 如果是wifi流量，且之前是非wifi错误，则恢复播放
                playFromUnWifiError();
            } else if (!isNetChanged) {
                showError(VideoErrorView.STATUS_VIDEO_SRC_ERROR);
            }
        } else if (!isPlayLocalVideo) {
            // 没网，且不是本地视频，则提示网络错误
            showError(VideoErrorView.STATUS_NO_NETWORK_ERROR);
        }
    }

    private void showError(int status) {
        mErrorView.showError(status);
        dismissVideoPanel();
        hideLoading();
        hideComplete();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }

        // 如果提示了错误，则看需要解锁
        if (mIsScreenLock) {
            unlock();
        }
    }

    private void allowUnWifiPlay() {
        Log.i("DDD", "allowUnWifiPlay");
        mAllowUnWifiPlay = true;

        playFromUnWifiError();
    }

    private void playFromUnWifiError() {
        Log.i("DDD", "playFromUnWifiError");

        if (mMediaPlayer != null && video != null) {
            String dataSource = mMediaPlayer.getVideoPath();
            if (TextUtils.isEmpty(dataSource)) {
                startPlayVideo(video);
            } else {
                playerStart();
            }
        }
    }

    private void retry(int status) {
        Log.i("DDD", "retry " + status);

        switch (status) {
            case VideoErrorView.STATUS_VIDEO_DETAIL_ERROR:
                // 传递给activity
                if (onVideoControlListener != null) {
                    onVideoControlListener.onRetry(status);
                }
                break;
            case VideoErrorView.STATUS_VIDEO_SRC_ERROR:
                reload();
                break;
            case VideoErrorView.STATUS_UN_WIFI_ERROR:
                allowUnWifiPlay();
                break;
            case VideoErrorView.STATUS_NO_NETWORK_ERROR:
                // 无网络时
                if (NetworkUtils.isNetworkConnected(getContext())) {
                    if (video == null) {
                        // 如果video为空，重新请求详情
                        retry(VideoErrorView.STATUS_VIDEO_DETAIL_ERROR);
                    } else {
                        // 如果有video，重新加载视频资源
                        reload();
                    }
                } else {
                    Toast.makeText(getContext(), "没有网络", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void showComplete() {
        if (mIsScreenLock) {
            unlock();
        }
        mViewComplete.setVisibility(View.VISIBLE);
        dismissVideoPanel();
    }

    public void hideComplete() {
        mViewComplete.setVisibility(View.GONE);
    }

    private OnClickListener mOnPlayerPauseClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mMediaPlayer.isPlaying()) {
                playerPause();
            } else {
                playerStart();
            }
        }
    };

    private boolean isDragSeeking; // 进度条正在手动拖动
    private SeekBar.OnSeekBarChangeListener mSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                isDragSeeking = true;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO: 2017/6/7 progress暂停变化
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            isDragSeeking = false;
            seekComplete(seekBar.getProgress());
            delayDismissVideoPanel();
        }
    };

    private void initPlayer() {
        mMediaPlayer = new VideoPlayer();
        mMediaPlayer.setCallback(new SimplePlayerCallback() {
            // TODO: 2017/6/13

            @Override
            public void onPrepared(MediaPlayer mp) {
                super.onPrepared(mp);
                preparedComplete();
            }
        });
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggleVideoLayoutParams();
    }

    void toggleVideoLayoutParams() {
        final boolean isPortrait = DisplayUtils.isPortrait(getContext());

        if (isPortrait) {
            mControllerBack.setVisibility(VISIBLE);

            ((RelativeLayout.LayoutParams) mControllerTitle.getLayoutParams()).topMargin = 0;
            mVideoRatio.setVisibility(GONE);
            mVideoCatalog.setVisibility(GONE);
            mVideoFullScreen.setVisibility(VISIBLE);

            mScreenLock.setVisibility(GONE);

            ((LinearLayout.LayoutParams) mViewCompleteBack.getLayoutParams()).topMargin =
                    DisplayUtils.dp2px(getContext(), 26);
        } else {
            ((RelativeLayout.LayoutParams) mControllerTitle.getLayoutParams()).topMargin =
                    DisplayUtils.dp2px(getContext(), 10);
            mVideoRatio.setVisibility(isPlayLocalVideo ? GONE : VISIBLE);
            mVideoCatalog.setVisibility(VISIBLE);
            mVideoFullScreen.setVisibility(GONE);

            if (isVideoPanelShowing) {
                mScreenLock.setVisibility(VISIBLE);
            }

            ((LinearLayout.LayoutParams) mViewCompleteBack.getLayoutParams()).topMargin =
                    DisplayUtils.dp2px(getContext(), 46);
        }
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
            checkShowError(false);
        }
    }

    private boolean mIsComplete = false;
//    private SetListener mMediaPlayStatus = (key, value, kv) -> {
//        String str = null;
//        mIsComplete = false;
//        switch (NumberUtils.string2Int(value)) {
//            case QbParameters.PLAY_STATUS_ERROR:
//                str = "出错";
//                hideLoading();
//
//                reConnect();
//                break;
//            case QbParameters.PLAY_STATUS_IDLE: // "空闲"
//                // do nothing
//                break;
//            case QbParameters.PLAY_STATUS_PAUSE:
//                str = "暂停中";
//                break;
//            case QbParameters.PLAY_STATUS_COMPLETION:
//                reConnect = 0;
//                onPlayComplete();
//                str = "完成";
//                break;
//            case QbParameters.PLAY_STATUS_PLAYING:
//                reConnect = 0;
//                str = "播放中";
//                break;
//            case QbParameters.PLAY_STATUS_PREPARED:
//                reConnect = 0;
//                str = "准备完成";
//                preparedComplete();
//
//                long progress = 0;
//                if (reConnectPosition > 0) {
//                    progress = reConnectPosition;
//                }
//                if (progress > 0 && progress < mMediaPlayer.getDuration()) {
//                    mMediaPlayer.seekTo(progress);
//                }
//                reConnectPosition = 0;
//
//                break;
//            case QbParameters.PLAY_STATUS_PREPARING:
//                showLoading();
//                str = "准备中";
//                break;
//        }
//
//        if (!TextUtils.isEmpty(str)) {
//            Log.i("DDD", "onSet: " + str);
//        }
//    };

    /**
     * 准备完成，开始播放
     */
    private void preparedComplete() {
        toggleVideoLayoutParams();

        playerStart();

        AudioManager am = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        // 回播设置进度.
        if (mControllerBottom != null) initPlayerSeekBar();
    }

    public void initPlayerSeekBar() {
        if (mMediaPlayer == null) return;

        final long currentPosition = mMediaPlayer.getCurrentPosition();
        final long duration = mMediaPlayer.getDuration();
        if (!isDragSeeking) {
            mPlayerSeekBar.setProgress((int) currentPosition);
        }
        mPlayerSeekBar.setMax((int) duration);
        mVideoDuration.setText(StringUtils.millSecondsToString((int) duration));
    }

    //播放完成
    private void onPlayComplete() {
        // 播放结束 重置 控制器状态.
        mVideoProgress.setText(StringUtils.millSecondsToString((int) mMediaPlayer.getDuration()));
        if (!isDragSeeking) {
            mPlayerSeekBar.setProgress(mPlayerSeekBar.getMax());
        }
        mVideoPlayState.setImageResource(R.drawable.ic_video_play);

//        mWeakReferenceHandler.cancel();

        mIsComplete = true;

        if (onVideoControlListener != null) {
            onVideoControlListener.onComplete();
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

        checkShowError(false);
    }

    /**
     * 开始播放
     */
    public void startPlayVideo(final VideoDetailInfo video) {
        if(video == null) {
            return;
        }

        this.video = video;

        mVideoTitle.setText(video.vedioTitle);

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
        mIsComplete = false;
        isPlayLocalVideo = false;
        reConnect = 0;
        reConnectPosition = 0;
    }

    private void playerPause() {
        if (mMediaPlayer == null) return;
        mVideoPlayState.setImageResource(R.drawable.ic_video_play);
        mMediaPlayer.pause();

        // TODO: 2017/6/7 暂停的时候控件不隐藏
//        mWeakReferenceHandler.cancel();
    }

    private void playerStart() {
        if (mMediaPlayer == null) return;

        Log.i("DDD", "playerStart");

        mVideoPlayState.setImageResource(R.drawable.ic_video_pause);

        if (mIsComplete) {
            reload();
        }

        hideComplete();
        hideLoading();
        mErrorView.hideError();

        if (!mMediaPlayer.isPlaying()) // TODO: 2017/6/13 check me 
            mMediaPlayer.start();

//        mWeakReferenceHandler.start();
        showVideoPanel();
    }

    private void reload() {
        if (mMediaPlayer == null) return;
        mMediaPlayer.stop();
        mMediaPlayer.start();

        showLoading();
    }

    public void updatePlayProgress() {
        long currentPosition = mMediaPlayer.getCurrentPosition();
        long duration = mMediaPlayer.getDuration();
        if (duration <= 0) return;
        float ratio = (float) currentPosition / duration;
        int pos = (int) (mPlayerSeekBar.getMax() * ratio);
        if (currentPosition <= duration)
            mVideoProgress.setText(StringUtils.millSecondsToString((int) currentPosition));
        if (!isDragSeeking) {
            mPlayerSeekBar.setProgress(pos);
        }
        mPlayerSeekBar.setMax((int) duration);
        mVideoDuration.setText(StringUtils.millSecondsToString((int) duration));

        // TODO: 2017/6/13  
        int cachePosition = (int) (mMediaPlayer.getBufferPercentage() + currentPosition);
        mPlayerSeekBar.setSecondaryProgress(cachePosition);
    }

    private void lock() {
        Log.i("DDD", "lock");
        mIsScreenLock = true;
        mScreenLock.setImageResource(R.drawable.video_locked);
    }

    private void unlock() {
        Log.i("DDD", "unlock");
        mIsScreenLock = false;
        mScreenLock.setImageResource(R.drawable.video_unlock);
    }

    private void showVideoPanel() {
        // Log.i("DDD", "showVideoPanel");
        isVideoPanelShowing = true;
        if (!mIsScreenLock) {
            mControllerBack.setVisibility(VISIBLE);
            mControllerTitle.setVisibility(VISIBLE);
            mControllerBottom.setVisibility(VISIBLE);
        } else {
            if (!DisplayUtils.isPortrait(getContext())) {
                mControllerBack.setVisibility(GONE);
            }
            mControllerTitle.setVisibility(GONE);
            mControllerBottom.setVisibility(GONE);
        }

        if (!DisplayUtils.isPortrait(getContext())) {
            mScreenLock.setVisibility(VISIBLE);
        }

        delayDismissVideoPanel();
    }

    private void dismissVideoPanel() {
        if (!DisplayUtils.isPortrait(getContext())) {
            // 横屏才消失
            mControllerBack.setVisibility(GONE);
        }
        mControllerTitle.setVisibility(GONE);
        mControllerBottom.setVisibility(GONE);
        mScreenLock.setVisibility(GONE);

        isVideoPanelShowing = false;
    }

    private void delayDismissVideoPanel() {
        // Log.i("DDD", "delayDismissVideoPanel");
        // 延迟消失
//        mWeakReferenceHandler.removeCallbacks(mDisappearRunnable);
//        mWeakReferenceHandler.postDelayed(mDisappearRunnable, DELAY_DISMISS_TIME);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mGestureDetector.onTouchEvent(event);

        if (event.getAction() == MotionEvent.ACTION_UP ||
                event.getAction() == MotionEvent.ACTION_CANCEL ||
                event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            mSystemUI.hide();

            // 快进结束，开始seek
            if (event.getAction() == MotionEvent.ACTION_UP &&
                    mProgressDialog.getVisibility() == VISIBLE) {
                int newProgress = mProgressDialog.getProgress();
                if (newProgress >= 0) {
                    seekComplete(newProgress);
                }
            }
            mProgressDialog.hide();
        }
        return true;
    }

    /**
     * 拖动结束，开始seekto操作
     *
     * @param newProgress 新的进度
     */
    private void seekComplete(int newProgress) {
        if (mIsComplete) {
            reload();
        }
        mMediaPlayer.seekTo(newProgress);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        if (mIsScreenLock) {
            return false;
        }

        //重置 手指行为
        mFingerBehavior = -1;
        mCurrentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        try {
            mCurrentBrightness = (int) (((Activity) getContext()).getWindow().
                    getAttributes().screenBrightness * mMaxBrightness);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (isVideoPanelShowing) {
            dismissVideoPanel();
        } else {
            showVideoPanel();
        }
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (mIsScreenLock) {
            return false;
        }

        final int width = getWidth();
        final int height = getHeight();
        if (width <= 0 || height <= 0) return false;

        /**
         * 根据手势起始2个点断言 后续行为. 规则如下:
         *  屏幕切分为正X:
         *  1.左右扇形区域为视频进度调节
         *  2.上下扇形区域 左半屏亮度调节 后半屏音量调节.
         */
        if (mFingerBehavior < 0) {
            float moveX = e2.getX() - e1.getX();
            float moveY = e2.getY() - e1.getY();
            if (Math.abs(moveX) >= Math.abs(moveY)) mFingerBehavior = FINGER_BEHAVIOR_PROGRESS;
            else if (e1.getX() <= width / 2) mFingerBehavior = FINGER_BEHAVIOR_BRIGHTNESS;
            else mFingerBehavior = FINGER_BEHAVIOR_VOLUME;
        }

        switch (mFingerBehavior) {
            case FINGER_BEHAVIOR_PROGRESS: { // 进度变化
                // 默认滑动一个屏幕 视频移动八分钟.
                int delProgress = (int) (1.0f * distanceX / width * 480 * 1000);
                // 更新快进弹框
                mProgressDialog.show(delProgress,
                        mMediaPlayer.getCurrentPosition(),
                        mMediaPlayer.getDuration());
                break;
            }
            case FINGER_BEHAVIOR_VOLUME: { // 音量变化
                float progress = mMaxVolume * (distanceY / height) + mCurrentVolume;

                if (progress <= 0) progress = 0;
                if (progress >= mMaxVolume) progress = mMaxVolume;

                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, Math.round(progress), 0);
                mSystemUI.show(VideoSystemOverlay.SystemType.VOLUME, mMaxVolume, Math.round(progress));

                mCurrentVolume = progress;
                break;
            }
            case FINGER_BEHAVIOR_BRIGHTNESS: { // 亮度变化
                try {
                    if (Settings.System.getInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE)
                            == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                        Settings.System.putInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE,
                                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                    }

                    int progress = (int) (mMaxBrightness * (distanceY / height) + mCurrentBrightness);

                    if (progress <= 0) progress = 0;
                    if (progress >= mMaxBrightness) progress = mMaxBrightness;

                    Window window = ((Activity) getContext()).getWindow();
                    WindowManager.LayoutParams params = window.getAttributes();
                    params.screenBrightness = progress / (float) mMaxBrightness;
                    window.setAttributes(params);

                    mSystemUI.show(VideoSystemOverlay.SystemType.BRIGHTNESS, mMaxBrightness, progress);

                    mCurrentBrightness = progress;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }

        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    private OnVideoControlListener onVideoControlListener;

    public void setOnVideoControlListener(OnVideoControlListener onVideoControlListener) {
        this.onVideoControlListener = onVideoControlListener;
    }
}
