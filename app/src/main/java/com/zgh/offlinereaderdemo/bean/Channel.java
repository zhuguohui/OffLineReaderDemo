package com.zgh.offlinereaderdemo.bean;

import com.zgh.offlinereader.OffLineLevelItem;
import com.zgh.offlinereaderdemo.util.GsonUtil;

import java.util.List;

/**
 * Created by yuelin on 2016/6/8.
 */
public class Channel implements OffLineLevelItem {
    String title;
    String url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean haveNextLevel() {
        return true;
    }

    @Override
    public String getWebUrl() {
        return null;
    }

    @Override
    public String getNextLevelListUrl() {
        return url;
    }

    @Override
    public List<OffLineLevelItem> getNextLevelList(String jsonStr) {
        List<OffLineLevelItem> items = GsonUtil.jsonToBeanList(jsonStr, NewsItem.class);
        return items;
    }
}
