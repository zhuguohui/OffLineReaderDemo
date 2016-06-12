package com.zgh.offlinereaderdemo.ui;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.zgh.offlinereader.server.OfflineReaderServer;
import com.zgh.offlinereaderdemo.R;
import com.zgh.offlinereaderdemo.adapter.NewsPageAdapter;
import com.zgh.offlinereaderdemo.bean.Channel;
import com.zgh.offlinereaderdemo.bean.NewsItem;
import com.zgh.offlinereaderdemo.util.GsonUtil;
import com.zgh.trshttp.TRSHttpUtil;
import com.zgh.trshttp.callback.TRSStringHttpCallback;
import com.zgh.trshttp.request.TRSHttpRequest;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ViewPager pager;
    TabLayout tab;
    NewsPageAdapter adapter;
    List<Channel> channels = new ArrayList<>();
    String url = "raw://news_list";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pager = (ViewPager) findViewById(R.id.pager);
        tab = (TabLayout) findViewById(R.id.tab);
        adapter = new NewsPageAdapter(getSupportFragmentManager(), channels);
        pager.setAdapter(adapter);
        tab.setupWithViewPager(pager);
        findViewById(R.id.btn_offread).setOnClickListener(this);
        loadData();
    }

    private void loadData() {
        TRSHttpRequest.Builder builder = new TRSHttpRequest.Builder();
        TRSHttpRequest request = builder.url(url).build();
        TRSHttpUtil.getInstance().loadString(request, new TRSStringHttpCallback() {
            @Override
            public void onResponse(String response) {
                List<Channel> items = GsonUtil.jsonToBeanList(response, Channel.class);
                channels.clear();
                channels.addAll(items);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onError(String error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent=new Intent(this, OfflineReaderServer.class);
        startService(intent);
    }
}
