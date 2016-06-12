package com.zgh.offlinereaderdemo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.zgh.offlinereader.OffLineLevelItem;
import com.zgh.offlinereaderdemo.R;
import com.zgh.offlinereaderdemo.bean.NewsItem;
import com.zgh.offlinereaderdemo.ui.WebViewActivity;
import com.zgh.offlinereaderdemo.util.GsonUtil;
import com.zgh.trshttp.TRSHttpUtil;
import com.zgh.trshttp.callback.TRSStringHttpCallback;
import com.zgh.trshttp.request.TRSHttpRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuelin on 2016/6/8.
 */
public class NewsFragment extends Fragment implements AdapterView.OnItemClickListener {
    public static final String KEY_URL = "key_url";
    private String url;
    private ArrayAdapter adapter;
    private ListView listView;
    List<NewsItem> data = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getArguments().getString(KEY_URL, "");
        adapter = new ArrayAdapter<NewsItem>(getActivity(), android.R.layout.simple_list_item_1, data);
        loadData();
    }

    private void loadData() {
        TRSHttpRequest.Builder builder = new TRSHttpRequest.Builder();
        TRSHttpRequest request = builder.url(url).build();
        TRSHttpUtil.getInstance().loadString(request, new TRSStringHttpCallback() {
            @Override
            public void onResponse(String response) {
                List<NewsItem> items = GsonUtil.jsonToBeanList(response, NewsItem.class);
                adapter.clear();
                adapter.addAll(items);

            }

            @Override
            public void onError(String error) {

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        listView = (ListView) view.findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        NewsItem item = (NewsItem) parent.getAdapter().getItem(position);
        Intent intent=new Intent(getActivity(),WebViewActivity.class);
        intent.putExtra(WebViewActivity.KEY_URL,item.getUrl());
        getActivity().startActivity(intent);
    }
}
