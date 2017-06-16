package com.boredream.videoplayer.video.player;

import android.media.MediaPlayer;
import android.util.Log;

public class SimplePlayerCallback implements PlayerCallback {

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.i("PlayerCallback", "onPrepared: ");
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Log.i("PlayerCallback", "onBufferingUpdate: ");
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        Log.i("PlayerCallback", "onVideoSizeChanged: ");
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.i("PlayerCallback", "onCompletion: ");
    }

    @Override
    public void onError(MediaPlayer mp, int what, int extra) {
        Log.i("PlayerCallback", "onError: ");
    }

    @Override
    public void onLoadingChanged(boolean isShow) {

    }
}
