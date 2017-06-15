package com.boredream.videoplayer.video;

import android.content.Context;
import android.content.res.Configuration;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.boredream.videoplayer.R;
import com.boredream.videoplayer.video.player.VideoPlayer;
import com.boredream.videoplayer.video.utils.DisplayUtils;
import com.boredream.videoplayer.video.utils.NetworkUtils;
import com.boredream.videoplayer.video.utils.ScreenUtils;

import java.util.Locale;

/**
 * 视频控制器，可替换或自定义样式
 */
public class MediaController extends FrameLayout {

    private boolean mIsChangeFluency; // 正在切换清晰度

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
    private VideoCatalogDialog mCatalogDialog;
    private VideoRatioDialog mRatioDialog;
    private View mViewComplete;
    private Button mViewCompleteBack;
    private ImageView mScreenLock;
    private VideoErrorView mErrorView;
    private TextView mVideoChangeFluency;

    private boolean isScreenLock;
    private boolean mShowing;
    private VideoPlayer mPlayer;

    public MediaController(Context context) {
        super(context);
        init();
    }

    public MediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MediaController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.video_media_controller, this);

        initControllerPanel();

        mCatalogDialog = (VideoCatalogDialog) findViewById(R.id.video_catalog_dialog);
        mRatioDialog = (VideoRatioDialog) findViewById(R.id.video_ratio_dialog);
        mRatioDialog.setOnVideoControlListener(new DefaultOnVideoControlListener() {
            @Override
            public void onRatioSelected(String fluency) {
                mRatioDialog.hide();

//                if (onVideoControlListener != null) {
//                    onVideoControlListener.onRatioSelected(fluency);
//                }
            }
        });
        mCatalogDialog.setOnVideoControlListener(new DefaultOnVideoControlListener() {
            @Override
            public void onCatalogItemSelected(int videoIndex) {
                mCatalogDialog.hide();
//                if (onVideoControlListener != null) {
//                    onVideoControlListener.onCatalogItemSelected(videoIndex);
//                }
            }
        });

        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
    }

    private void initControllerPanel() {
        // back
        mControllerBack = findViewById(R.id.video_back);
        mControllerBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (onVideoControlListener != null) {
//                    onVideoControlListener.onBack();
//                }
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
        mVideoRatio = (TextView) mControllerBottom.findViewById(R.id.video_ratio);
        mVideoCatalog = (TextView) mControllerBottom.findViewById(R.id.video_catalog);
        mVideoFullScreen = (ImageView) mControllerBottom.findViewById(R.id.video_full_screen);
        mVideoPlayState.setOnClickListener(mOnPlayerPauseClick);
        mVideoPlayState.setImageResource(R.drawable.ic_video_pause);
        mPlayerSeekBar.setOnSeekBarChangeListener(mSeekListener);
        mVideoRatio.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mRatioDialog.show();
            }
        });
        mVideoCatalog.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                mCatalogDialog.show(video);
            }
        });
        mVideoFullScreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (onVideoControlListener != null) {
//                    onVideoControlListener.onFullScreen();
//                }
            }
        });

        // lock
        mScreenLock = (ImageView) findViewById(R.id.player_lock_screen);
        mScreenLock.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isScreenLock) unlock();
                else lock();

                show();
            }
        });

        // complete
        mViewComplete = findViewById(R.id.video_controller_complete);
        mViewCompleteBack = (Button) findViewById(R.id.video_controller_complete_back);
        mViewCompleteBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideComplete();

//                if (onVideoControlListener != null) {
//                    onVideoControlListener.onExit();
//                }
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

        // change
        mVideoChangeFluency = (TextView) findViewById(R.id.video_change_fluency);

        mPlayerSeekBar.setMax(1000);
    }

    public void setMediaPlayer(VideoPlayer player) {
        mPlayer = player;
        updatePausePlay();
    }

    public void toggleDisplay() {
        if (mShowing) {
            hide();
        } else {
            show();
        }
    }

    public void show() {
        show(3000);  // TODO: 2017/6/14 default time 3000
    }

    public void show(int timeout) {
        Log.i("DDD", "show: " + timeout);

        if (!mShowing) {
            // TODO: 2017/6/14
            setProgress();

            if (!isScreenLock) {
                mControllerBack.setVisibility(VISIBLE);
                mControllerTitle.setVisibility(VISIBLE);
                mControllerBottom.setVisibility(VISIBLE);
            } else {
                if (!ScreenUtils.isPortrait(getContext())) {
                    mControllerBack.setVisibility(GONE);
                }
                mControllerTitle.setVisibility(GONE);
                mControllerBottom.setVisibility(GONE);
            }

            if (!ScreenUtils.isPortrait(getContext())) {
                mScreenLock.setVisibility(VISIBLE);
            }

            mShowing = true;
        }

        updatePausePlay();

        // cause the progress bar to be updated even if mShowing
        // was already true.  This happens, for example, if we're
        // paused with the progress bar showing the user hits play.
        post(mShowProgress);

        if (timeout > 0) {
            removeCallbacks(mFadeOut);
            postDelayed(mFadeOut, timeout);
        }
    }

    private void hide() {
        if (!mShowing) {
            return;
        }

        if (!ScreenUtils.isPortrait(getContext())) {
            // 横屏才消失
            mControllerBack.setVisibility(GONE);
        }
        mControllerTitle.setVisibility(GONE);
        mControllerBottom.setVisibility(GONE);
        mScreenLock.setVisibility(GONE);

        // TODO: 2017/6/14 隐藏后不再更新进度条
//        removeCallbacks(mShowProgress);

        mShowing = false;
    }

    private final Runnable mFadeOut = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    private boolean mDragging;
    private long mDraggingProgress;
    private final Runnable mShowProgress = new Runnable() {
        @Override
        public void run() {
            int pos = setProgress();
            if (!mDragging && mShowing && mPlayer.isPlaying()) {
                postDelayed(mShowProgress, 1000 - (pos % 1000));
            }
        }
    };

    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        if (hours > 0) {
            return String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        }
    }

    private int setProgress() {
        if (mPlayer == null || mDragging) {
            return 0;
        }
        int position = mPlayer.getCurrentPosition();
        int duration = mPlayer.getDuration();
        if (mPlayerSeekBar != null) {
            if (duration > 0) {
                // use long to avoid overflow
                long pos = 1000L * position / duration;
                mPlayerSeekBar.setProgress((int) pos);
            }
            int percent = mPlayer.getBufferPercentage();
            mPlayerSeekBar.setSecondaryProgress(percent * 10);
        }

        mVideoProgress.setText(stringForTime(position));
        mVideoDuration.setText(stringForTime(duration));

        return position;
    }

    /**
     * 判断显示错误类型
     */
    public void checkShowError(boolean isNetChanged) {
//        boolean isConnect = NetworkUtils.isNetworkConnected(getContext());
//        boolean isMobileNet = NetworkUtils.isMobileConnected(getContext());
//        boolean isWifiNet = NetworkUtils.isWifiConnected(getContext());
//
//        if (isConnect) {
//            // 如果已经联网
//            if (mErrorView.getCurStatus() == VideoErrorView.STATUS_NO_NETWORK_ERROR && !(isMobileNet && !isWifiNet)) {
//                // 如果之前是无网络，应该提示“网络已经重新连上，请重试”，这里暂不处理
//            } else if (video == null) {
//                // 优先判断是否有video数据
//                showError(VideoErrorView.STATUS_VIDEO_DETAIL_ERROR);
//            } else if (isMobileNet && !isWifiNet && !mAllowUnWifiPlay && !isPlayLocalVideo) {
//                // 如果是手机流量，且未同意过播放，且非本地视频，则提示错误
//                mErrorView.showError(VideoErrorView.STATUS_UN_WIFI_ERROR);
//
//                if (isNetChanged) {
//                    mMediaPlayer.pause();
//                } else {
//                    mMediaPlayer.stop();
//                }
//            } else if (isWifiNet && isNetChanged && mErrorView.getCurStatus() == VideoErrorView.STATUS_UN_WIFI_ERROR) {
//                // 如果是wifi流量，且之前是非wifi错误，则恢复播放
//                playFromUnWifiError();
//            } else if (!isNetChanged) {
//                showError(VideoErrorView.STATUS_VIDEO_SRC_ERROR);
//            }
//        } else if (!isPlayLocalVideo) {
//            // 没网，且不是本地视频，则提示网络错误
//            showError(VideoErrorView.STATUS_NO_NETWORK_ERROR);
//        }
    }

    private void retry(int status) {
        Log.i("DDD", "retry " + status);

        switch (status) {
            case VideoErrorView.STATUS_VIDEO_DETAIL_ERROR:
                // 传递给activity
//                if (onVideoControlListener != null) {
//                    onVideoControlListener.onRetry(status);
//                }
                break;
            case VideoErrorView.STATUS_VIDEO_SRC_ERROR:
//                reload();
                break;
            case VideoErrorView.STATUS_UN_WIFI_ERROR:
                allowUnWifiPlay();
                break;
            case VideoErrorView.STATUS_NO_NETWORK_ERROR:
                // 无网络时
                if (NetworkUtils.isNetworkConnected(getContext())) {
//                    if (video == null) {
//                        // 如果video为空，重新请求详情
//                        retry(VideoErrorView.STATUS_VIDEO_DETAIL_ERROR);
//                    } else {
//                        // 如果有video，重新加载视频资源
//                        reload();
//                    }
                } else {
                    Toast.makeText(getContext(), "没有网络", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private boolean isDragSeeking; // 进度条正在手动拖动
    // There are two scenarios that can trigger the seekbar listener to trigger:
    //
    // The first is the user using the touchpad to adjust the posititon of the
    // seekbar's thumb. In this case onStartTrackingTouch is called followed by
    // a number of onProgressChanged notifications, concluded by onStopTrackingTouch.
    // We're setting the field "mDragging" to true for the duration of the dragging
    // session to avoid jumps in the position in case of ongoing playback.
    //
    // The second scenario involves the user operating the scroll ball, in this
    // case there WON'T BE onStartTrackingTouch/onStopTrackingTouch notifications,
    // we will simply apply the updated position without suspending regular updates.
    private final SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStartTrackingTouch(SeekBar bar) {
            show(3600000);

            mDragging = true;

            // By removing these pending progress messages we make sure
            // that a) we won't update the progress while the user adjusts
            // the seekbar and b) once the user is done dragging the thumb
            // we will post one of these messages to the queue again and
            // this ensures that there will be exactly one message queued up.
            removeCallbacks(mShowProgress);
        }

        @Override
        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (!fromuser) {
                // We're not interested in programmatically generated changes to
                // the progress bar's position.
                return;
            }

            long duration = mPlayer.getDuration();
            mDraggingProgress = (duration * progress) / 1000L;

            if (mVideoProgress != null) {
                mVideoProgress.setText(stringForTime((int) mDraggingProgress));
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar bar) {
            mPlayer.seekTo((int) mDraggingProgress);
            mDragging = false;
            mDraggingProgress = 0;
            setProgress();
            updatePausePlay();
            show();

            // Ensure that progress is properly updated in the future,
            // the call to show() does not guarantee this because it is a
            // no-op if we are already showing.
            post(mShowProgress);
        }
    };

    public void showComplete() {
        if (isScreenLock) {
            unlock();
        }
        mViewComplete.setVisibility(View.VISIBLE);
        hide();
    }

    public void hideComplete() {
        mViewComplete.setVisibility(View.GONE);
    }

    private void showError(int status) {
        mErrorView.showError(status);
        hide();
//        hideLoading();
//        hideComplete();
//        if (mMediaPlayer != null) {
//            mMediaPlayer.stop();
//        }

        // 如果提示了错误，则看需要解锁
        if (isScreenLock) {
            unlock();
        }
    }

    private void lock() {
        Log.i("DDD", "lock");
        isScreenLock = true;
        mScreenLock.setImageResource(R.drawable.video_locked);
    }

    private void unlock() {
        Log.i("DDD", "unlock");
        isScreenLock = false;
        mScreenLock.setImageResource(R.drawable.video_unlock);
    }

    private void allowUnWifiPlay() {
//        Log.i("DDD", "allowUnWifiPlay");
//        mAllowUnWifiPlay = true;
//
//        playFromUnWifiError();
    }

    private void playFromUnWifiError() {
        Log.i("DDD", "playFromUnWifiError");

//        if (mMediaPlayer != null && video != null) {
//            String dataSource = mMediaPlayer.getVideoPath();
//            if (TextUtils.isEmpty(dataSource)) {
//                startPlayVideo(video);
//            } else {
//                playerStart();
//            }
//        }
    }

    public void startUpdateFluency(String fluency) {
        mIsChangeFluency = true;

        String fluencyTitle = VideoFluencyConstants.getFluencyTitle(fluency);
        String str = String.format("正在切换到%s，请稍候... ", fluencyTitle);
        SpannableString ss = new SpannableString(str);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(0xFFFD7D6F);
        ss.setSpan(colorSpan, 5, 5 + fluencyTitle.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

        mVideoChangeFluency.setVisibility(View.VISIBLE);
        mVideoChangeFluency.setText(ss);
    }

    public void completeUpdateFluency(String fluency) {
        mIsChangeFluency = false;

        String fluencyTitle = VideoFluencyConstants.getFluencyTitle(fluency);
        String str = String.format("提醒您已切换到%s", fluencyTitle);
        SpannableString ss = new SpannableString(str);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(0xFFFD7D6F);
        ss.setSpan(colorSpan, 7, 7 + fluencyTitle.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

        mVideoRatio.setText(fluencyTitle);
        mVideoChangeFluency.setVisibility(View.VISIBLE);
        mVideoChangeFluency.setText(ss);
    }

    private OnClickListener mOnPlayerPauseClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            doPauseResume();
        }
    };

    private void updatePausePlay() {
        if (mPlayer.isPlaying()) {
            mVideoPlayState.setImageResource(R.drawable.ic_video_pause);
        } else {
            mVideoPlayState.setImageResource(R.drawable.ic_video_play);
        }
    }

    private void doPauseResume() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            removeCallbacks(mFadeOut);
        } else {
            mPlayer.start();
            show();
        }
        updatePausePlay();
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        Log.i("DDD", "onConfigurationChanged ... isPor = " + ScreenUtils.isPortrait(getContext()));
        super.onConfigurationChanged(newConfig);
        toggleVideoLayoutParams();
    }

    void toggleVideoLayoutParams() {
        final boolean isPortrait = ScreenUtils.isPortrait(getContext());

        if (isPortrait) {
            mControllerBack.setVisibility(VISIBLE);

            ((RelativeLayout.LayoutParams) mControllerTitle.getLayoutParams()).topMargin = 0;
            mVideoRatio.setVisibility(GONE);
            mVideoCatalog.setVisibility(GONE);
            mVideoFullScreen.setVisibility(VISIBLE);

            mCatalogDialog.setVisibility(GONE);
            mRatioDialog.setVisibility(GONE);

            mScreenLock.setVisibility(GONE);

            ((LinearLayout.LayoutParams) mViewCompleteBack.getLayoutParams()).topMargin =
                    DisplayUtils.dp2px(getContext(), 26);
        } else {
            ((RelativeLayout.LayoutParams) mControllerTitle.getLayoutParams()).topMargin =
                    DisplayUtils.dp2px(getContext(), 10);
//            mVideoRatio.setVisibility(isPlayLocalVideo ? GONE : VISIBLE);
            mVideoCatalog.setVisibility(VISIBLE);
            mVideoFullScreen.setVisibility(GONE);

            if (mShowing) {
                mScreenLock.setVisibility(VISIBLE);
            }

            ((LinearLayout.LayoutParams) mViewCompleteBack.getLayoutParams()).topMargin =
                    DisplayUtils.dp2px(getContext(), 46);
        }
    }

}
