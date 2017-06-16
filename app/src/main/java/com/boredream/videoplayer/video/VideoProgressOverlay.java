package com.boredream.videoplayer.video;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.boredream.videoplayer.R;
import com.boredream.videoplayer.video.utils.StringUtils;

public class VideoProgressOverlay extends FrameLayout {
    private ImageView mSeekIcon;
    private TextView mSeekCurProgress;
    private TextView mSeekDuration;

    private int mDelSeekDialogProgress = -1;
    private int mSeekDialogStartProgress = -1;

    public VideoProgressOverlay(Context context) {
        super(context);
        init();
    }

    public VideoProgressOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VideoProgressOverlay(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VideoProgressOverlay(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.video_overlay_progress, this);

        mSeekIcon = (ImageView) findViewById(R.id.iv_seek_direction);
        mSeekCurProgress = (TextView) findViewById(R.id.tv_seek_current_progress);
        mSeekDuration = (TextView) findViewById(R.id.tv_seek_duration);
    }

    /**
     * 更新快进框
     *
     * @param delProgress 进度变化值
     * @param curPosition player当前进度
     * @param duration    player总长度
     */
    public void updateSeekDialog(int delProgress, int curPosition, int duration) {
        if (duration <= 0) return;

        // 获取第一次显示时的开始进度
        if (mSeekDialogStartProgress == -1) {
            Log.i("DDD", "updateSeekDialog: start seek = " + mSeekDialogStartProgress);
            mSeekDialogStartProgress = curPosition;
        }

        if(getVisibility() != View.VISIBLE) {
            setVisibility(View.VISIBLE);
        }

        mDelSeekDialogProgress -= delProgress;
        long newSeekProgress = getProgress(duration);

        if (delProgress > 0) {
            // 回退
            mSeekIcon.setImageResource(R.drawable.ic_video_back);
        } else {
            // 前进
            mSeekIcon.setImageResource(R.drawable.ic_video_speed);
        }
        mSeekCurProgress.setText(StringUtils.millSecondsToString((int) newSeekProgress));
        mSeekDuration.setText(StringUtils.millSecondsToString((int) duration));
    }

    public int getProgress(int duration) {
        int newSeekProgress = mSeekDialogStartProgress + mDelSeekDialogProgress;
        if (newSeekProgress <= 0) newSeekProgress = 0;
        if (newSeekProgress >= duration) newSeekProgress = duration;
        return newSeekProgress;
    }

    public void hide() {
        mSeekDialogStartProgress = -1;
        mDelSeekDialogProgress = -1;
        setVisibility(GONE);
    }

}
