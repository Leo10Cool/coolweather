package com.coolweather.app.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by �� on 2015/10/23 0023.
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
//                    Buffered�����һ����װ�࣬�����԰�װ�ַ��������ַ������뻺���
//                    �Ȱ��ַ�������������������˻�����flush��ʱ���ٶ����ڴ棬��Ϊ���ṩ����Ч�ʶ���Ƶġ�
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    if (listener != null) {
                        listener.onFinish(response.toString()); //�ص�onFinish����
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onError(e);    //�ص�onError����
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
