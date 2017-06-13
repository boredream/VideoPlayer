package com.boredream.videoplayer.video;

public interface OnVideoControlListener {

    void onExit();

    void onBack();

    void onFullScreen();

    void onCatalogItemSelected(int videoIndex);

    void onRatioSelected(String fluency);

    void onRetry(int status);

    void onComplete();
}
