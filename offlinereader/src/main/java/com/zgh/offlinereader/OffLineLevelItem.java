package com.zgh.offlinereader;

import java.util.List;

/**
 * Created by yuelin on 2016/6/7.
 */
public interface OffLineLevelItem  {

    boolean haveNextLevel();

    String getWebUrl();

    String getNextLevelListUrl();

    List<OffLineLevelItem> getNextLevelList(String jsonStr);




}
