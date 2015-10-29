package com.coolweather.app.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by 飞 on 2015/10/23 0023.
 */
public class HttpUtil {

    private static String TAG = "HttpUtil";

    public static void sendHttpRequest(final String address, final HttpCallbackListener listener) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
//                    Buffered类就是一个包装类，它可以包装字符流，将字符流放入缓存里，
//                    先把字符读到缓存里，到缓存满了或者你flush的时候，再读入内存，是为了提供读的效率而设计的。
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    if (listener != null) {
                        listener.onFinish(response.toString()); //回调onFinish方法
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onError(e);    //回调onError方法
                    }
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}
