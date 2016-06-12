/**
 * Copyright 2015 ZhangQu Li
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zgh.trshttp.provider.okhttp.body;

import android.os.Handler;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.ResponseBody;
import com.zgh.trshttp.callback.TRSFileDownloadHttpCallback;


import java.io.IOException;

import okio.*;

/**
 * Created by Vincent Woo
 * Date: 2015/12/23
 * Time: 11:22
 */
public class ProgressResponseBody extends ResponseBody {
    //实际的待包装响应体
    private final ResponseBody responseBody;
    //进度回调接口
    private final TRSFileDownloadHttpCallback progressListener;
    //包装完成的BufferedSource
    private BufferedSource bufferedSource;
    private Handler delivery;

    /**
     * 构造函数，赋值
     *
     * @param responseBody     待包装的响应体
     * @param progressListener 回调接口
     */
    public ProgressResponseBody(ResponseBody responseBody, TRSFileDownloadHttpCallback progressListener, Handler delivery) {
        this.responseBody = responseBody;
        this.progressListener = progressListener;
        this.delivery = delivery;
    }

    /**
     * 重写调用实际的响应体的contentType
     *
     * @return MediaType
     */
    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    /**
     * 重写调用实际的响应体的contentLength
     *
     * @return contentLength
     * @throws IOException 异常
     */
    @Override
    public long contentLength() throws IOException {
        return responseBody.contentLength();
    }

    /**
     * 重写进行包装source
     *
     * @return BufferedSource
     * @throws IOException 异常
     */
    @Override
    public BufferedSource source() throws IOException {
        if (bufferedSource == null) {
            //包装
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    /**
     * 读取，回调进度接口
     *
     * @param source Source
     * @return Source
     */
    private Source source(Source source) {

        return new ForwardingSource(source) {
            //当前读取字节数
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                final long bytesRead = super.read(sink, byteCount);
                //增加当前读取的字节数，如果读取完成了bytesRead会返回-1
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                //回调，如果contentLength()不知道长度，会返回-1
                if (progressListener != null) {
                    delivery.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                progressListener.onProgress(totalBytesRead, responseBody.contentLength(), bytesRead == -1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                return bytesRead;
            }
        };
    }
}