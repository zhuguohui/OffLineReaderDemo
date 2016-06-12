package com.zgh.offlinereader.ui;

import android.view.View;

/**
 * Created by yuelin on 2016/6/8.
 */
public interface OffLineProgressUI {
    void showProgress();

    void closeProgress();

    void updateProgress(int progress);

}
