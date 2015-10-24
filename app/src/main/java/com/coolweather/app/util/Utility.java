package com.coolweather.app.util;

import android.text.TextUtils;
import android.util.Log;

import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

/**
 * ���ڷ��������ص�ʡ�������ݶ��ǡ�����|����,����|���С����ָ�ʽ�ģ�
 * �������ṩһ���������������ʹ�����������
 * �����Ĺ�������Ȱ����ŷָ����ٰ������߷ָ���
 * ���Ž������������������õ�ʵ���У������� CoolWeatherDB �е����� save()���������ݴ洢����Ӧ�ı��С�
 */
public class Utility {

    private static String TAG = "Utility";
    /*
    * �����ʹ�����������ص�ʡ������
    * */
    public synchronized static boolean handleProvinceResponse(CoolWeatherDB coolWeatherDB, String response) {
        Log.w(TAG,"a");
        if (!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");



            if (allProvinces != null && allProvinces.length > 0) {
                for (String p : allProvinces) {
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
//                    ���������������ݴ洢��Province��                }
                    coolWeatherDB.saveProvince(province);
                    Log.w(TAG, "b");
                }
                return true;
            }
        }
        return false;
    }

    /*
    * �����ʹ�����������ص��м�����
    * */
    public synchronized static boolean handleCityResponse(CoolWeatherDB coolWeatherDB, String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0) {
                for (String c : allCities) {
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
//                   ���������������ݴ洢��City��
                    coolWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;

    }

    /*
    * �����ʹ�����������ص��ؼ�����
    * */
    public synchronized static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB, String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCounties = response.split(",");
            if (allCounties != null && allCounties.length > 0) {
                for (String c : allCounties) {
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
//                    ���������������ݴ洢��County��
                    coolWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }

}
