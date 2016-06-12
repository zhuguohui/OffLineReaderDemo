package com.zgh.offlinereaderdemo.bean;

import com.zgh.offlinereader.OffLineLevelItem;

import java.util.List;

/**
 * Created by yuelin on 2016/6/8.
 */
public class NewsItem implements OffLineLevelItem{
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
    public String toString() {
        return title;
    }

    @Override
    public boolean haveNextLevel() {
        return false;
    }

    @Override
    public String getWebUrl() {
        return url;
    }

    @Override
    public String getNextLevelListUrl() {
        return null;
    }

    @Override
    public List<OffLineLevelItem> getNextLevelList(String jsonStr) {
        return null;
    }
}
