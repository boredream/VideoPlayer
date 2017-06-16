package com.boredream.videoplayer.video;

public interface OnVideoControlListener {

    void onBack();

    void onFullScreen();

    void onRetry(int status);

    void onComplete();
}
