package com.boredream.videoplayer.video.utils;

public class StringUtils {
    public static String millSecondsToString(int seconds) {
        // TODO: 2017/6/7 优化写法
        seconds = seconds / 1000;
        String result = "";
        int hour, min, second;
        hour = seconds / 3600;
        min = (seconds - hour * 3600) / 60;
        second = seconds - hour * 3600 - min * 60;
        if (hour <= 0) {

        } else if (hour < 10) {
            result += "0" + hour + ":";
        } else {
            result += hour + ":";
        }
        if (min < 10) {
            result += "0" + min + ":";
        } else {
            result += min + ":";
        }
        if (second < 10) {
            result += "0" + second;
        } else {
            result += second;
        }
        return result;
    }
}
