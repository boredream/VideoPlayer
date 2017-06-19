package com.boredream.bdvideoplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.boredream.bdvideoplayer.R;
import com.boredream.bdvideoplayer.utils.StringUtils;

/**
 * 滑动快进快退进度框
 */
public class VideoProgressOverlay extends FrameLayout {
    private ImageView mSeekIcon;
    private TextView mSeekCurProgress;
    private TextView mSeekDuration;

    private int mDuration = -1;
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

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.video_overlay_progress, this);

        mSeekIcon = (ImageView) findViewById(R.id.iv_seek_direction);
        mSeekCurProgress = (TextView) findViewById(R.id.tv_seek_current_progress);
        mSeekDuration = (TextView) findViewById(R.id.tv_seek_duration);
    }

    /**
     * 显示进度框
     *
     * @param delProgress 进度变化值
     * @param curPosition player当前进度
     * @param duration    player总长度
     */
    public void show(int delProgress, int curPosition, int duration) {
        if (duration <= 0) return;

        // 获取第一次显示时的开始进度
        if (mSeekDialogStartProgress == -1) {
            Log.i("DDD", "show: start seek = " + mSeekDialogStartProgress);
            mSeekDialogStartProgress = curPosition;
        }

        if (getVisibility() != View.VISIBLE) {
            setVisibility(View.VISIBLE);
        }

        mDuration = duration;
        mDelSeekDialogProgress -= delProgress;
        int targetProgress = getTargetProgress();

        if (delProgress > 0) {
            // 回退
            mSeekIcon.setImageResource(R.drawable.ic_video_back);
        } else {
            // 前进
            mSeekIcon.setImageResource(R.drawable.ic_video_speed);
        }
        mSeekCurProgress.setText(StringUtils.stringForTime(targetProgress));
        mSeekDuration.setText(StringUtils.stringForTime(mDuration));
    }

    /**
     * 获取滑动结束后的目标进度
     */
    public int getTargetProgress() {
        if (mDuration == -1) {
            return -1;
        }

        int newSeekProgress = mSeekDialogStartProgress + mDelSeekDialogProgress;
        if (newSeekProgress <= 0) newSeekProgress = 0;
        if (newSeekProgress >= mDuration) newSeekProgress = mDuration;
        return newSeekProgress;
    }

    public void hide() {
        mDuration = -1;
        mSeekDialogStartProgress = -1;
        mDelSeekDialogProgress = -1;
        setVisibility(GONE);
    }

}
