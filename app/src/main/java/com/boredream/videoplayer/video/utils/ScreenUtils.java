package com.boredream.videoplayer.video.utils;

import android.content.Context;
import android.content.res.Configuration;

public class ScreenUtils {

    /**
     * 获得当前屏幕的方向.
     * @return 是否竖屏.
     */
    public static boolean isPortrait(Context context) {
        int orientation = context.getResources().getConfiguration().orientation;
        return orientation == Configuration.ORIENTATION_PORTRAIT;
    }

}
