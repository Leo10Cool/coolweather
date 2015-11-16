package com.coolweather.app.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coolweather.app.R;
import com.coolweather.app.receiver.AutoUpdateReceiver;
import com.coolweather.app.service.AutoUpdateService;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

public class WeatherActivity extends ActionBarActivity implements View.OnClickListener {

    private static String TAG = "WeatherActivity";

    private LinearLayout weatherInfoLayout;
    //    显示城市名
    private TextView cityNameText;
    //    显示发布时间
    private TextView publishText;
    //    显示天气描述信息
    private TextView weatherDespText;
    //    显示气温1
    private TextView temp1Text;
    //    显示气温2
    private TextView temp2Text;
    //    显示当前日期
    private TextView currentDateText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);

        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        cityNameText = (TextView) findViewById(R.id.city_name);
        publishText = (TextView) findViewById(R.id.publish_text);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        temp1Text = (TextView) findViewById(R.id.temp1);
        temp2Text = (TextView) findViewById(R.id.temp2);
        currentDateText = (TextView) findViewById(R.id.current_date);

        findViewById(R.id.btn_switch_city).setOnClickListener(this);
        findViewById(R.id.btn_refresh_weather).setOnClickListener(this);

        String countyCode = getIntent().getStringExtra("county_code");  //尝试从 Intent(ChooseAreaActivity) 中取出县级代号
        if (!TextUtils.isEmpty(countyCode)) {
//            取到县级代号就会调用queryWeatherCode()方法
            publishText.setText("同步中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);


        } else {
//            没取到县级代号则会调用 showWeather()方法
            showWeather();
        }

    }

    //      查询县级代号所对应的天气代号
    private void queryWeatherCode(String countyCode) {
        String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
        queryFromServer(address, "countyCode"); //调用queryFromServer()方法来查询县级代号所对应的天气代号
    }

    //      查询天气代号所对应的天气
    private void queryWeatherInfo(String weatherCode) {
//        String address = "http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
        String address = "http://www.weather.com.cn/adat/cityinfo/" + weatherCode + ".html";
        queryFromServer(address, "weatherCode");
    }

    //    根据传入的地址和类型去向服务器查询天气代号或者天气信息。
    private void queryFromServer(final String address, final String type) {

        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

            @Override
            public void onFinish(String response) {
                if ("countyCode".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
//                        从服务器返回的数据解析天气代号
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2) {
                            String weatherCode = array[1];//天气代号
                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if ("weatherCode".equals(type)) {
//                    处理服务器返回的天气信息

//                    在 handleWeatherResponse() 方法中使用 JSONObject将数据全部解析出来，然后调用
//                    saveWeatherInfo()方法将所有的天气信息都存储到 SharedPreferences 文件中。
                    Utility.handleWeatherResponse(WeatherActivity.this, response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });

            }
        });

    }

    //    从SharedPreferences文件中读取存储的天气信息，并显示到界面上。
    private void showWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(prefs.getString("city_name", ""));
        temp1Text.setText(prefs.getString("temp1", ""));
        temp2Text.setText(prefs.getString("temp2", ""));
        weatherDespText.setText(prefs.getString("weather_desp", ""));
        publishText.setText("今天" + prefs.getString("publish_time", "") + "发布");
        currentDateText.setText(prefs.getString("current_date", ""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);


//        Intent intent = new Intent(this, AutoUpdateService.class);
//        startService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_weather, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,ChooseAreaActivity.class);
        intent.putExtra("from_weather_activity",true);
//        intent.putExtra("back_from_weather_activity",true);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_switch_city:
                Intent intent = new Intent(this, ChooseAreaActivity.class);
//                将Intent传入ChooseAreaActivity,来表示是从WeatherActivity跳转过来的
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
                break;
            case R.id.btn_refresh_weather:
                publishText.setText("同步中...");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                String weatherCode = prefs.getString("weather_code", "");
                if (!TextUtils.isEmpty(weatherCode)) {
                    queryWeatherInfo(weatherCode);

                }
                break;
            default:
                break;
        }
    }
}
