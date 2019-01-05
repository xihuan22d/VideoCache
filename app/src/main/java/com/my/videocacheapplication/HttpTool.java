package com.my.videocacheapplication;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpTool {

    private static final Handler mainHandler = new Handler(Looper.getMainLooper());


    public static void httpDownTest(final View view, final TextView textView, Activity activity, final String downloadUri) {
        view.setEnabled(false);
        final File filesDir = activity.getFilesDir();
        new Thread() {
            @Override
            public void run() {
                URL url;
                HttpURLConnection httpURLConnection = null;
                FileOutputStream fileOutputStream = null;
                BufferedInputStream bufferedInputStream = null;
                try {
                    url = new URL(downloadUri);
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setConnectTimeout(4000);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setRequestProperty("Charset", "utf-8");
                    httpURLConnection.connect();
                    String urlFilePath = httpURLConnection.getURL().getFile();
                    String fileName = urlFilePath.substring(urlFilePath.lastIndexOf(File.separatorChar) + 1);
                    File file = new File(filesDir, fileName);
                    fileOutputStream = new FileOutputStream(file);
                    int responseCode = httpURLConnection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = httpURLConnection.getInputStream();
                        int length = httpURLConnection.getContentLength();
                        bufferedInputStream = new BufferedInputStream(inputStream);
                        int len = 0;
                        int totle = 0;
                        byte[] bytes = new byte[1024];
                        long timeArea = System.currentTimeMillis();
                        long byteArea = 0;
                        while ((len = bufferedInputStream.read(bytes)) != -1) {
                            totle += len;
                            fileOutputStream.write(bytes, 0, len);
                            long nowTime = System.currentTimeMillis();
                            if (nowTime - timeArea > 1000) {//计算一秒的kb
                                final String tips = (totle - byteArea) / 1024 + "KB/s  进度:" + ((float) totle / length * 100) + "%";
                                mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        textView.setText(tips);
                                    }
                                });
                                timeArea = nowTime;
                                byteArea = totle;
                            }
                        }
                        fileOutputStream.close();
                        bufferedInputStream.close();
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                view.setEnabled(true);
                                textView.setText("下载完成");
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (bufferedInputStream != null) {
                        try {
                            bufferedInputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                }
            }
        }.start();
    }


}
