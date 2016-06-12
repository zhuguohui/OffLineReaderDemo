package com.zgh.trshttp.request;




import com.zgh.trshttp.TRSHttpUtil;

import java.io.File;
import java.util.HashMap;

/**
 * Created by Vincent Woo
 * Date: 2015/12/21
 * Time: 16:25
 */
public class TRSHttpRequest {
    private int method;
    private String url;
    private boolean isNeedCache;
    private HashMap<String, String> headers;
    private HashMap<String, String> params;
    private HashMap<String, File> files;
    private String tag;

    private TRSHttpRequest(Builder builder) {
        this.method = builder.method;
        this.url = builder.url;
        this.isNeedCache = builder.isNeedCache;
        this.headers = builder.headers;
        this.params = builder.params;
        this.files = builder.files;
        this.tag = builder.tag;
    }

    public int getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public boolean getIsNeedCache() {
        return isNeedCache;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public String getTag() {
        return tag;
    }

    public HashMap<String, File> getFiles(){
        return files;
    }

    public static class Builder {
        private int method;
        private String url;
        private boolean isNeedCache;
        private HashMap<String, String> headers;
        private HashMap<String, String> params;
        private HashMap<String, File> files;
        private String tag;

        public Builder() {
            this.method = TRSHttpUtil.GET;
            this.url = "";
            this.isNeedCache = true;
            this.headers = null;
            this.params = null;
            this.files = null;
            this.tag = null;
        }

        public Builder method(int method){
            this.method = method;
            return this;
        }

        public Builder url(String url){
            this.url = url;
            return this;
        }

        public Builder isNeedCache(boolean isNeedCache){
            this.isNeedCache = isNeedCache;
            return this;
        }

        public Builder headers(HashMap<String, String> headers){
            this.headers = headers;
            return this;
        }

        public Builder params(HashMap<String, String> params){
            this.params = params;
            return this;
        }

        public Builder files(HashMap<String, File> files){
            this.files = files;
            return this;
        }

        public Builder tag(String tag){
            this.tag = tag;
            return this;
        }

        public TRSHttpRequest build(){
            return new TRSHttpRequest(this);
        }
    }

}
