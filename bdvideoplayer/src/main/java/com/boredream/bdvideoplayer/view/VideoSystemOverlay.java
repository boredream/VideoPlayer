package com.boredream.bdvideoplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.boredream.bdvideoplayer.R;

/**
 * 滑动变化音量|亮度框
 */
public class VideoSystemOverlay extends FrameLayout {

    public enum SystemType {
        VOLUME, BRIGHTNESS
    }

    private TextView mSystemTitle;
    private ImageView mSystemImage;
    private ProgressBar mProgressBar;

    public VideoSystemOverlay(Context context) {
        super(context);
        initialize(context);
    }

    public VideoSystemOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    private void initialize(Context context) {
        LayoutInflater.from(context).inflate(R.layout.video_overlay_system, this);

        mSystemTitle = (TextView) findViewById(R.id.system_ui_title);
        mSystemImage = (ImageView) findViewById(R.id.system_ui_image);
        mProgressBar = (ProgressBar) findViewById(R.id.system_ui_seek_bar);

        hide();
    }

    public void show(SystemType type, int max, int progress) {
        if (type == SystemType.BRIGHTNESS) {
            mSystemTitle.setText("亮度");
            mSystemImage.setImageResource(R.drawable.system_ui_brightness);
        } else if (type == SystemType.VOLUME) {
            mSystemTitle.setText("音量");
            mSystemImage.setImageResource(progress == 0
                    ? R.drawable.system_ui_no_volume
                    : R.drawable.system_ui_volume);
        }
        mProgressBar.setMax(max);
        mProgressBar.setProgress(progress);
        setVisibility(VISIBLE);
    }

    public void hide() {
        setVisibility(GONE);
    }

}
