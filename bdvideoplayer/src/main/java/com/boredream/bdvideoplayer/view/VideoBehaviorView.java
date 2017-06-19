package com.boredream.bdvideoplayer.view;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

// TODO: 2017/6/16 避免继承

/**
 * 视频手势View，注意添加 player.setAudioStreamType(AudioManager.STREAM_MUSIC);
 */
public class VideoBehaviorView extends FrameLayout implements GestureDetector.OnGestureListener {

    private GestureDetector mGestureDetector;

    public static final int FINGER_BEHAVIOR_PROGRESS = 0x01;  //进度调节
    public static final int FINGER_BEHAVIOR_VOLUME = 0x02;  //音量调节
    public static final int FINGER_BEHAVIOR_BRIGHTNESS = 0x03;  //亮度调节
    private int mFingerBehavior;
    private float mCurrentVolume; // 鉴于音量范围值比较小 使用float类型施舍五入处理.
    private int mMaxVolume;
    private int mCurrentBrightness, mMaxBrightness;

    protected Activity activity;
    protected AudioManager am;

    public VideoBehaviorView(Context context) {
        super(context);
        init();
    }

    public VideoBehaviorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VideoBehaviorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Context context = getContext();
        if (context instanceof Activity) {
            mGestureDetector = new GestureDetector(context.getApplicationContext(), this);
            activity = (Activity) context;
            am = (AudioManager) (context.getSystemService(Context.AUDIO_SERVICE));
        } else {
            throw new RuntimeException("VideoBehaviorView context must be Activity");
        }

        mMaxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mMaxBrightness = 255;
    }

    protected void endGesture(int behaviorType) {
        // sub
    }

    protected void updateSeekUI(int delProgress) {
        // sub
    }

    protected void updateVolumeUI(int max, int progress) {
        // sub
    }

    protected void updateLightUI(int max, int progress) {
        // sub
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_CANCEL:
                endGesture(mFingerBehavior);
                break;
        }
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        //重置 手指行为
        mFingerBehavior = -1;
        mCurrentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        try {
            mCurrentBrightness = (int) (activity.getWindow().getAttributes().screenBrightness * mMaxBrightness);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
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
            if (Math.abs(moveX) >= Math.abs(moveY))
                mFingerBehavior = FINGER_BEHAVIOR_PROGRESS;
            else if (e1.getX() <= width / 2) mFingerBehavior = FINGER_BEHAVIOR_BRIGHTNESS;
            else mFingerBehavior = FINGER_BEHAVIOR_VOLUME;
        }

        switch (mFingerBehavior) {
            case FINGER_BEHAVIOR_PROGRESS: { // 进度变化
                // 默认滑动一个屏幕 视频移动八分钟.
                int delProgress = (int) (1.0f * distanceX / width * 480 * 1000);
                // 更新快进弹框
                updateSeekUI(delProgress);
                break;
            }
            case FINGER_BEHAVIOR_VOLUME: { // 音量变化
                float progress = mMaxVolume * (distanceY / height) + mCurrentVolume;

                if (progress <= 0) progress = 0;
                if (progress >= mMaxVolume) progress = mMaxVolume;

                am.setStreamVolume(AudioManager.STREAM_MUSIC, Math.round(progress), 0);
                updateVolumeUI(mMaxVolume, Math.round(progress));
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

                    Window window = activity.getWindow();
                    WindowManager.LayoutParams params = window.getAttributes();
                    params.screenBrightness = progress / (float) mMaxBrightness;
                    window.setAttributes(params);

                    updateLightUI(mMaxBrightness, progress);

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
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

}
