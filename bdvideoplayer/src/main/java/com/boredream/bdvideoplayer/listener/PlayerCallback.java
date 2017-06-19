package com.boredream.bdvideoplayer.listener;

import android.media.MediaPlayer;

/**
 * 视频操作回调，是将系统MediaPlayer的常见回调封装了起来
 */
public interface PlayerCallback {

    /**
     * 准备完成
     */
    void onPrepared(MediaPlayer mp);

    /**
     * 视频size变化
     */
    void onVideoSizeChanged(MediaPlayer mp, int width, int height);

    /**
     * 缓存更新变化
     *
     * @param percent 缓冲百分比
     */
    void onBufferingUpdate(MediaPlayer mp, int percent);

    /**
     * 播放完成
     */
    void onCompletion(MediaPlayer mp);

    /**
     * 视频错误
     *
     * @param what  错误类型
     *              <ul>
     *              <li>{@link android.media.MediaPlayer#MEDIA_ERROR_UNKNOWN}
     *              <li>{@link android.media.MediaPlayer#MEDIA_ERROR_SERVER_DIED}
     *              </ul>
     * @param extra 特殊错误码
     *              <ul>
     *              <li>{@link android.media.MediaPlayer#MEDIA_ERROR_IO}
     *              <li>{@link android.media.MediaPlayer#MEDIA_ERROR_MALFORMED}
     *              <li>{@link android.media.MediaPlayer#MEDIA_ERROR_UNSUPPORTED}
     *              <li>{@link android.media.MediaPlayer#MEDIA_ERROR_TIMED_OUT}
     *              <li><code>MEDIA_ERROR_SYSTEM (-2147483648)</code> - low-level system error.
     *              </ul>
     */
    void onError(MediaPlayer mp, int what, int extra);

    /**
     * 视频加载状态变化
     *
     * @param isShow 是否显示loading
     */
    void onLoadingChanged(boolean isShow);

    /**
     * 视频状态变化
     * <p><img src="../../../../../../images/mediaplayer_state_diagram.gif"
     * alt="MediaPlayer State diagram" border="0" /></p>
     *
     * @param curState 当前视频状态
     *                 <ul>
     *                 <li>{@link com.boredream.bdvideoplayer.BDVideoPlayer#STATE_ERROR}
     *                 <li>{@link com.boredream.bdvideoplayer.BDVideoPlayer#STATE_IDLE}
     *                 <li>{@link com.boredream.bdvideoplayer.BDVideoPlayer#STATE_PREPARING}
     *                 <li>{@link com.boredream.bdvideoplayer.BDVideoPlayer#STATE_PREPARED}
     *                 <li>{@link com.boredream.bdvideoplayer.BDVideoPlayer#STATE_PLAYING}
     *                 <li>{@link com.boredream.bdvideoplayer.BDVideoPlayer#STATE_PAUSED}
     *                 <li>{@link com.boredream.bdvideoplayer.BDVideoPlayer#STATE_PLAYBACK_COMPLETED}
     *                 </ul>
     */
    void onStateChanged(int curState);
}
