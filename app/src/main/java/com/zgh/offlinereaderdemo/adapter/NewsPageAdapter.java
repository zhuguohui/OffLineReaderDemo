package com.zgh.offlinereaderdemo.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.zgh.offlinereaderdemo.bean.Channel;
import com.zgh.offlinereaderdemo.fragment.NewsFragment;

import java.util.List;

/**
 * Created by yuelin on 2016/6/8.
 */
public class NewsPageAdapter extends FragmentPagerAdapter {
    List<Channel> mChannels;

    public NewsPageAdapter(FragmentManager fm, List<Channel> channels) {
        super(fm);
        mChannels = channels;
    }

    @Override
    public Fragment getItem(int position) {
        NewsFragment fragment = new NewsFragment();
        Bundle data = new Bundle();
        data.putString(NewsFragment.KEY_URL, mChannels.get(position).getUrl());
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public int getCount() {
        return mChannels.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mChannels.get(position).getTitle();
    }
}
