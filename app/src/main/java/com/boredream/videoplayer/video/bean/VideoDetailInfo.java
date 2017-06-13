package com.boredream.videoplayer.video.bean;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VideoDetailInfo implements Serializable {

    public String anchorUserId;
    public String authorNickName;

    /**
     * 是否购买 0-未 1-是
     */
    public String description;
    public int seriesNumber;
    /**
     * 免费集数
     */
    public List<Integer> freeSeriesNumberList;
    public int seriesNumberTotal;
    /**
     * 当前用户是否订阅 0-未 1-是
     */
    public int subscribeFlag;
    /**
     * 是否可以试看 0-否 1-是
     */
    public int tryWatchFlag;
    public int albumBuyFlag;
    public long albumPrice;
    public String vedioAlbumCoverUrl;
    public String vedioAlbumId;
    public String vedioAlbumTitle;
    public int vedioAlbumPlayCount;
    public long vedioDuration;
    public String vedioId;
    public int vedioPlayCount;
    public long size;
    public String vedioTitle;
    /**
     * 视频地址。key为类型"common"/"high" ，value为url
     */
    public Map<String, String> vedioUrlDto;
    /**
     * 默认视频清晰度
     */
    public String defaultDefinition;


    // 自定义参数
    /**
     * 当前视频清晰度
     */
    private String curDefinition;

    public String getCurDefinition() {
        if (curDefinition == null) {
            if(vedioUrlDto != null) {
                String url = vedioUrlDto.get(defaultDefinition);
                if(TextUtils.isEmpty(url) && getDefinitionList().size() > 0) {
                    // 判断default对应的url，如果不存在则获取一个存在的地址
                    defaultDefinition = getDefinitionList().get(0);
                }
            }
            curDefinition = defaultDefinition;
        }
        return curDefinition;
    }

    public void setCurDefinition(String curDefinition) {
        this.curDefinition = curDefinition;
    }

    public List<String> getDefinitionList() {
        List<String> list = new ArrayList<>();
        if(vedioUrlDto != null) {
            for (String key : vedioUrlDto.keySet()) {
                if (TextUtils.isEmpty(vedioUrlDto.get(key))) {
                    continue;
                }
                list.add(key);
            }
        }
        return list;
    }

    public String getVideoUrl() {
        if (vedioUrlDto == null) return "";
        return vedioUrlDto.get(getCurDefinition());
    }

    /**
     * 专辑是否可用（免费、已付费都算可用）
     */
    public boolean isAlbumEnable() {
        return albumPrice == 0 || albumBuyFlag == 1;
    }

}
