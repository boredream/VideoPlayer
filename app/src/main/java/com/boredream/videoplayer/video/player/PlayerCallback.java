package com.boredream.videoplayer.video.player;

import android.media.MediaPlayer;

public interface PlayerCallback {
    void onPrepared(MediaPlayer mp);

    void onVideoSizeChanged(MediaPlayer mp, int width, int height);

    void onBufferingUpdate(MediaPlayer mp, int percent);

    void onCompletion(MediaPlayer mp);

    boolean onError(MediaPlayer mp, int what, int extra);
}
