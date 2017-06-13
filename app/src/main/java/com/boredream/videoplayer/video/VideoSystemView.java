package com.boredream.videoplayer.video;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.boredream.videoplayer.R;


/**
 * 定制音量|亮度样式
 * Created by david on 16/5/19.
 */
public class VideoSystemView extends FrameLayout {

    public static final int SYSTEM_UI_VOLUME        = 0x01;
    public static final int SYSTEM_UI_BRIGHTNESS    = 0x02;

    private TextView mSystemTitle;
    private ImageView mSystemImage;
    private ProgressBar mProgressBar;

    public VideoSystemView(Context context) {
        super(context);
        initialize(context);
    }

    public VideoSystemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    private void initialize(Context context) {
        LayoutInflater.from(context).inflate(R.layout.video_system_ui, this);

        mSystemTitle = (TextView) findViewById(R.id.system_ui_title);
        mSystemImage = (ImageView) findViewById(R.id.system_ui_image);
        mProgressBar = (ProgressBar) findViewById(R.id.system_ui_seek_bar);

        hide();
    }

    public void show(int ui, int max, int progress)  {
        if (ui == SYSTEM_UI_BRIGHTNESS) {
            mSystemTitle.setText("亮度");
            mSystemImage.setImageResource(R.drawable.system_ui_brightness);
        } else if (ui == SYSTEM_UI_VOLUME) {
            mSystemTitle.setText("音量");
            mSystemImage.setImageResource(progress == 0 ?
                    R.drawable.system_ui_no_volume : R.drawable.system_ui_volume);
        }
        mProgressBar.setMax(max);
        mProgressBar.setProgress(progress);
        setVisibility(VISIBLE);
    }

    public void hide() {
        setVisibility(GONE);
    }

}
