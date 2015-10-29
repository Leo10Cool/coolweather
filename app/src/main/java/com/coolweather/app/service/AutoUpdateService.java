package com.coolweather.app.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

public class AutoUpdateService extends Service {

    private static String TAG = "AutoUpdateService";

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//       开启子线程
        new Thread(new Runnable() {
            @Override
            public void run() {
//                调用updateWeather()方法来更新天气
                updateWeather();
            }
        }).start();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 3*60*60*1000;  //这是3小时的毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime()+anHour;
        Intent i = new Intent(this,AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getBroadcast(this,0,i,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent,flags,startId);
    }

//    更新天气信息
    private void updateWeather(){

//        将服务器返回的天气数据交给 Utility 的 handleWeatherResponse()方法去处理，
//        这样就可以把最新的天气信息存储到 SharedPreferences文件中。
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String  weatherCode = prefs.getString("weather_code","");
        String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(AutoUpdateService.this,response);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
