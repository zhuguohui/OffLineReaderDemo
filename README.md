# OffLineReaderDemo
#1.先看效果
###加载动画
![这里写图片描述](http://img.blog.csdn.net/20160624162441345)

###加载完成，注意当前为飞行模式!
![这里写图片描述](http://img.blog.csdn.net/20160624163249452)

#2.使用
###1.让你的javabean实现OffLineLevelItem接口，因为我的这个离线阅读支持多级下载，比如Demo中的每个频道下面的第一页item都可以缓存。

```
package com.zgh.offlinereader;

import java.util.List;

/**
 * Created by zhuguohui on 2016/6/7.
 */
public interface OffLineLevelItem  {
    //是否有下一级
    boolean haveNextLevel();
    //内容url
    String getWebUrl();
    //下一级的url
    String getNextLevelListUrl();
    //生成下一级
    List<OffLineLevelItem> getNextLevelList(String jsonStr);
}

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
```

###2.初始化

```
   OfflineReaderServer.init(this, getCacheDir(), new MyFirstLevel(),new WaterWaveProgressUI(this));
```
###3.启动

```
 @Override
    public void onClick(View v) {
        Intent intent=new Intent(this, OfflineReaderServer.class);
        startService(intent);
    }
```
###4.记得在你的webview使用前调用

```
	//设置缓存目录
    WebViewHelper.setWebViewConfig(webView);
```

**就这么简单！**
