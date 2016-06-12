package com.zgh.offlinereaderdemo.bean;

import com.zgh.offlinereader.OffLineLevelItem;
import com.zgh.offlinereaderdemo.util.GsonUtil;

import java.util.List;

/**
 * Created by yuelin on 2016/6/8.
 */
public class MyFirstLevel implements OffLineLevelItem {
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
        return "raw://news_list";
    }

    @Override
    public List<OffLineLevelItem> getNextLevelList(String jsonStr) {
        List<OffLineLevelItem> items = GsonUtil.jsonToBeanList(jsonStr, Channel.class);
        return items;
    }
}
