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
    //    ��ʾ������
    private TextView cityNameText;
    //    ��ʾ����ʱ��
    private TextView publishText;
    //    ��ʾ����������Ϣ
    private TextView weatherDespText;
    //    ��ʾ����1
    private TextView temp1Text;
    //    ��ʾ����2
    private TextView temp2Text;
    //    ��ʾ��ǰ����
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

        String countyCode = getIntent().getStringExtra("county_code");  //���Դ� Intent(ChooseAreaActivity) ��ȡ���ؼ�����
        if (!TextUtils.isEmpty(countyCode)) {
//            ȡ���ؼ����žͻ����queryWeatherCode()����
            publishText.setText("ͬ����...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);


        } else {
//            ûȡ���ؼ����������� showWeather()����
            showWeather();
        }

    }

    //      ��ѯ�ؼ���������Ӧ����������
    private void queryWeatherCode(String countyCode) {
        String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
        queryFromServer(address, "countyCode"); //����queryFromServer()��������ѯ�ؼ���������Ӧ����������
    }

    //      ��ѯ������������Ӧ������
    private void queryWeatherInfo(String weatherCode) {
//        String address = "http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
        String address = "http://www.weather.com.cn/adat/cityinfo/" + weatherCode + ".html";
        queryFromServer(address, "weatherCode");
    }

    //    ���ݴ���ĵ�ַ������ȥ���������ѯ�������Ż���������Ϣ��
    private void queryFromServer(final String address, final String type) {

        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

            @Override
            public void onFinish(String response) {
                if ("countyCode".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
//                        �ӷ��������ص����ݽ�����������
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2) {
                            String weatherCode = array[1];//��������
                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if ("weatherCode".equals(type)) {
//                    ������������ص�������Ϣ

//                    �� handleWeatherResponse() ������ʹ�� JSONObject������ȫ������������Ȼ�����
//                    saveWeatherInfo()���������е�������Ϣ���洢�� SharedPreferences �ļ��С�
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
                        publishText.setText("ͬ��ʧ��");
                    }
                });

            }
        });

    }

    //    ��SharedPreferences�ļ��ж�ȡ�洢��������Ϣ������ʾ�������ϡ�
    private void showWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(prefs.getString("city_name", ""));
        temp1Text.setText(prefs.getString("temp1", ""));
        temp2Text.setText(prefs.getString("temp2", ""));
        weatherDespText.setText(prefs.getString("weather_desp", ""));
        publishText.setText("����" + prefs.getString("publish_time", "") + "����");
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
//                ��Intent����ChooseAreaActivity,����ʾ�Ǵ�WeatherActivity��ת������
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
                break;
            case R.id.btn_refresh_weather:
                publishText.setText("ͬ����...");
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
