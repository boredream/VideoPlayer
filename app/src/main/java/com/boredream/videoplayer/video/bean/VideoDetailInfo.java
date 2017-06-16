package com.boredream.videoplayer.video.bean;

public class VideoDetailInfo implements IVideoInfo {

    public String videoTitle;
    public String videoPath;

    @Override
    public String getVideoTitle() {
        return videoTitle;
    }

    @Override
    public String getVideoPath() {
        return videoPath;
    }
}
