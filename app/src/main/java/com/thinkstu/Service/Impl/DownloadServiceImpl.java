package com.thinkstu.Service.Impl;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.widget.*;

import java.io.*;

import okhttp3.*;

// 核心代码：下载服务
public class DownloadServiceImpl extends IntentService {
    OkHttpClient client = new OkHttpClient();

    public DownloadServiceImpl() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String urlToDownload = intent.getStringExtra("fileName");
        downloadFile(urlToDownload);
    }

    private void downloadFile(String fileName) {
        String[] split = fileName.split(" - ");
        String   s     = split[1].toLowerCase();
        s = s.substring(0, s.lastIndexOf(".")).replaceAll(" ", "-");
        String url = "https://freemusicarchive.org/track/" + s + "/download";
        Log.e("url_test", url);

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(() -> Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    // 服务器返回错误
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(() -> Toast.makeText(getApplicationContext(), "服务器错误", Toast.LENGTH_LONG).show());
                } else {
                    InputStream      input  = response.body().byteStream();
                    FileOutputStream output = openFileOutput(fileName, Context.MODE_PRIVATE);

                    byte[] data = new byte[4096];
                    int    count;
                    while ((count = input.read(data)) != -1) {
                        output.write(data, 0, count);
                    }
                    output.flush();
                    output.close();
                    input.close();
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(() -> Toast.makeText(getApplicationContext(), "下载成功", Toast.LENGTH_LONG).show());
                }
            }
        });
    }
}
