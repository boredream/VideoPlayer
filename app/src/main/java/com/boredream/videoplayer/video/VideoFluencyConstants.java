package com.boredream.videoplayer.video;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class VideoFluencyConstants {
    public static final String RATIO_FLUENCY = "fluency";
    public static final String RATIO_COMMON = "common";
    public static final String RATIO_HIGH = "high";
    public static final String RATIO_1080P = "blue";

    public static String getFluencyTitle(String fluency) {
        switch (fluency) {
            case RATIO_FLUENCY:
                return "流畅";
            case RATIO_COMMON:
                return "标清";
            case RATIO_HIGH:
                return "高清";
            case RATIO_1080P:
                return "1080P";
        }
        return "";
    }

    public static Integer getFluPriority(String fluency) {
        switch (fluency) {
            case RATIO_1080P:
                return 1;
            case RATIO_HIGH:
                return 2;
            case RATIO_COMMON:
                return 3;
            case RATIO_FLUENCY:
                return 4;
        }
        return 0;
    }

    public static void sortFluListByPriority(List<String> fluencyList) {
        Collections.sort(fluencyList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return getFluPriority(o1).compareTo(getFluPriority(o2));
            }
        });
    }
}
