package com.coolweather.app.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.coolweather.app.R;
import com.coolweather.app.SwitchButton.SwitchButton;
import com.coolweather.app.service.AutoUpdateService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetActivity extends Activity implements CompoundButton.OnCheckedChangeListener {

    private static String TAG = "SetActivity";

    private static int time = 3600000;
    public static int hours;

    Map<String,Integer> map = new HashMap<String,Integer>();

    private SwitchButton sb;

    private SharedPreferences.Editor editor ;
    private SharedPreferences.Editor editorHour;

    private SharedPreferences settings;

    private SharedPreferences prefs;


    private List<String> listHour = new ArrayList<String>();

    private List<Map<String,Integer>> listMap = new ArrayList<Map<String, Integer>>();
    private Spinner spinnerHour;
    private ArrayAdapter<String> adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_layout);
        sb = (SwitchButton) findViewById(R.id.sb_set_service);
        sb.setOnCheckedChangeListener(this);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        settings = getSharedPreferences("hour_settings", Context.MODE_PRIVATE);
        if (prefs.getBoolean("service_run",false)){
            sb.setChecked(true);
        }else {
            sb.setChecked(false);
        }



        listHour.add("1Сʱ");
        listHour.add("2Сʱ");
        listHour.add("3Сʱ");
        listHour.add("6Сʱ");
        listHour.add("12Сʱ");


        map.put("1Сʱ",1*time);
        map.put("2Сʱ",2*time);
        map.put("3Сʱ",3*time);
        map.put("6Сʱ",6*time);
        map.put("12Сʱ", 12 * time);
//        listHour.add(map);


        spinnerHour = (Spinner) findViewById(R.id.sp_anhour);
//        Ϊ�����б���������
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,listHour);
//        Ϊ���������������б�����ʱ�Ĳ˵�
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        ����������ӵ������б���
        spinnerHour.setAdapter(adapter);
//        Ϊ�����б���Ӽ����� ���¼���ѡ�У�

//        ��סspinnerѡ��
        int positionHour = settings.getInt("SelectedPosition",0);
        spinnerHour.setSelection(positionHour);

        spinnerHour.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                String sInfo = adapter.getItem(position).toString();
                Toast.makeText(getApplicationContext(),sInfo,Toast.LENGTH_SHORT).show();
                hours = map.get(adapter.getItem(position).toString());
                Log.e(TAG, String.valueOf(hours));

                editorHour = settings.edit();
                editorHour.putInt("SelectedPosition",position);
                editorHour.commit();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//        �����˵�����������ѡ����¼�����
        spinnerHour.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
//        �����˵�����������ѡ���ı��¼�����
        spinnerHour.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_set, menu);
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
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Toast.makeText(this, "" + isChecked, Toast.LENGTH_SHORT).show();

        editor = prefs.edit();

        if (isChecked){
            Log.e(TAG, "��");
            editor.putBoolean("service_run", true);
            editor.commit();
            Intent startIntent = new Intent(this, AutoUpdateService.class);
            startService(startIntent);
        }else {
            Log.e(TAG, "��");
            editor.putBoolean("service_run", false);
            editor.commit();
            Intent stopIntent = new Intent(this,AutoUpdateService.class);
            stopService(stopIntent);
        }
    }
}
