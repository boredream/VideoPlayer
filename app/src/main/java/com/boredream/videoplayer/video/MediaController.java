package com.boredream.videoplayer.video;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * 视频控制器，可替换或自定义样式
 */
public class MediaController extends FrameLayout {

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
    private View mLoading;
    private TextView mVideoChangeFluency;

    public MediaController(Context context) {
        super(context);
    }

    public MediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MediaController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
