package com.guohua.north_bulb.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guohua.north_bulb.AppContext;
import com.guohua.north_bulb.R;
import com.guohua.north_bulb.adapter.DeviceListFroGroupAdapter;
import com.guohua.north_bulb.adapter.ImageAdapter;
import com.guohua.north_bulb.bean.DatetimeBean;
import com.guohua.north_bulb.bean.Device;
import com.guohua.north_bulb.bean.Group;
import com.guohua.north_bulb.bean.TimerModel;
import com.guohua.north_bulb.communication.BLEConstant;
import com.guohua.north_bulb.net.SendRunnable;
import com.guohua.north_bulb.net.ThreadPool;
import com.guohua.north_bulb.util.BLECodeUtils;
import com.guohua.north_bulb.util.CodeUtils;
import com.guohua.north_bulb.util.Constant;
import com.guohua.north_bulb.wheel.ArrayWheelAdapter;
import com.guohua.north_bulb.wheel.OnWheelChangedListener;
import com.guohua.north_bulb.wheel.WheelView;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class AddTimerActivity extends Activity implements View.OnClickListener {

    String TAG = "AddTimerActivity";
    RelativeLayout rl_Group_save;
    WheelView wheel_Minute, wheel_Seconds, wheel_OnOff;
    public ListView lstDevice;
    public DeviceListFroGroupAdapter deviceListAdapter;
    public ArrayList<Device> deviceList;
    TimerModel timerModel;
    String selectedMinutes = "0", selectedSecond = "0", selectedStatus = "On";
    private DatetimeBean mDatetime;
    public int year;
    public int month;
    public int day;
    public int tmpDay;
    public int hour;
    public int minute;
    public int second;

    private static final String[] ARRAY_MINUTE = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
            "20", "21", "22", "23", "24", "25", "26", "27", "28", "29"};

    private static final String[] ARRAY_SECONDS = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
            "20", "21", "22", "23", "24", "25", "26", "27", "28", "29",
            "30", "31", "32", "33", "34", "35", "36", "37", "38", "39",
            "40", "41", "42", "43", "44", "45", "46", "47", "48", "49",
            "50", "51", "52", "53", "54", "55", "56", "57", "58", "59"};

    private static final String[] ARRAY_ON_OFF = new String[]{"On", "Off"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_timer);
        getintent();
        initView();

    }

    private void getintent() {
        timerModel = new TimerModel();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            timerModel = (TimerModel) bundle.getSerializable(BLEConstant.EXTRA_DEVICE_LIST);
        }
    }

    private void initView() {
        mDatetime = new DatetimeBean();
        rl_Group_save = (RelativeLayout) findViewById(R.id.rl_Group_save);
        lstDevice = (ListView) findViewById(R.id.lstDevice);
        wheel_Minute = (WheelView) findViewById(R.id.wheel_Minute);
        wheel_Seconds = (WheelView) findViewById(R.id.wheel_Seconds);
        wheel_OnOff = (WheelView) findViewById(R.id.wheel_OnOff);

        rl_Group_save.setOnClickListener(this);

        deviceList = new ArrayList<>();
        deviceListAdapter = new DeviceListFroGroupAdapter(AddTimerActivity.this, TAG);
        lstDevice.setAdapter(deviceListAdapter);
        Gson gson = new Gson();
        Type type = new TypeToken<List<Device>>() {
        }.getType();

        String Devicelist = AppContext.preferenceGetString(BLECodeUtils.DEVICE_LIST, "");

        if (!TextUtils.isEmpty(Devicelist) && !Devicelist.equals("")) {

            deviceList = gson.fromJson(Devicelist, type);
            for (int i = 0; i < deviceList.size(); i++) {
                deviceList.get(i).setSelected(false);
            }
            deviceListAdapter.addAllDevices(deviceList);
        }


        ArrayWheelAdapter<String> ampmAdapter = new ArrayWheelAdapter<>(this, ARRAY_MINUTE);
        ampmAdapter.setItemResource(R.layout.wheel_item_time);
        ampmAdapter.setItemTextResource(R.id.time_item);
        wheel_Minute.setViewAdapter(ampmAdapter);
        wheel_Minute.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                selectedMinutes = ARRAY_MINUTE[newValue];
            }
        });
        ArrayWheelAdapter<String> ampmAdapterSecond = new ArrayWheelAdapter<>(this, ARRAY_SECONDS);
        ampmAdapterSecond.setItemResource(R.layout.wheel_item_time);
        ampmAdapterSecond.setItemTextResource(R.id.time_item);
        wheel_Seconds.setViewAdapter(ampmAdapterSecond);
        wheel_Seconds.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                selectedSecond = ARRAY_SECONDS[newValue];
            }
        });

        ArrayWheelAdapter<String> ampmAdapterOnOff = new ArrayWheelAdapter<>(this, ARRAY_ON_OFF);
        ampmAdapterOnOff.setItemResource(R.layout.wheel_item_time);
        ampmAdapterOnOff.setItemTextResource(R.id.time_item);
        wheel_OnOff.setViewAdapter(ampmAdapterOnOff);
        wheel_OnOff.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                selectedStatus = ARRAY_ON_OFF[newValue];
            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.rl_Group_save:


                deviceList = deviceListAdapter.getAllDevices();
                Device devices = new Device();
                boolean anySelectDevice = false;
                for (int i = 0; i < deviceList.size(); i++) {

                    if (deviceList.get(i).isSelected()) {
                        anySelectDevice = true;
                        devices = deviceList.get(i);
                        break;
                    }
                }

                if (!anySelectDevice) {
                    Toast.makeText(AddTimerActivity.this, "Please select device !", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (selectedMinutes.equalsIgnoreCase("0") && selectedSecond.equalsIgnoreCase("0")) {
                    Toast.makeText(AddTimerActivity.this, "Please select duration !", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.e(TAG, "selectedMinutes " + selectedMinutes);
                Log.e(TAG, "selectedSecond " + selectedSecond);
                Log.e(TAG, "selectedStatus " + selectedStatus);

                long select_Total_MS = ((Integer.parseInt(selectedMinutes) * 60) + Integer.parseInt(selectedSecond)) * 1000;
                int selecteSecond = (int) (select_Total_MS / 1000);
                Log.e(TAG, "select_Total_MS " + select_Total_MS);
                Log.e(TAG, "selecteSecond " + selecteSecond);
                String data;
                if (selectedStatus.equalsIgnoreCase("On")) {
                    data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_DELAY_OPEN, new Object[]{selecteSecond});
                } else {
                    data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_DELAY_CLOSE, new Object[]{selecteSecond});
                }

                Log.e(TAG, "data " + data);
                Log.e(TAG, "devices " + devices.toString());

                ThreadPool.getInstance().addOtherTask(new SendRunnable(devices.getDeviceAddress(), data));

                Calendar calendar = Calendar.getInstance();

                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                tmpDay = calendar.get(Calendar.DAY_OF_MONTH);
                hour = calendar.get(Calendar.HOUR_OF_DAY);
                minute = calendar.get(Calendar.MINUTE);
                second = calendar.get(Calendar.SECOND);

                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute + (Integer.parseInt(selectedMinutes)));
                calendar.set(Calendar.SECOND, second + Integer.parseInt(selectedSecond));
                String cdate = (String) DateFormat.format("yyyy-MM-dd HH:mm:ss", calendar.getTime());
                Date selecteddate = null;
                Log.e(TAG, "cdate " + cdate);
                SimpleDateFormat sdUTC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    selecteddate = sdUTC.parse(cdate);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e(TAG, "selecteddate  " + selecteddate);

                if (selectedStatus.equalsIgnoreCase("On")) {
                    timerModel.setStatus(true);
                } else {
                    timerModel.setStatus(false);
                }

                timerModel.setDevices(devices);
                timerModel.setDate(selecteddate);

                Intent intent = new Intent();
                Bundle b = new Bundle();
                b.putSerializable(BLEConstant.EXTRA_TIMER_LIST, timerModel);
                intent.putExtras(b);
                setResult(BLEConstant.RESULT_TIMER_ADD, intent);
                finish();


                break;
        }
    }


    public void back(View view) {
        this.finish();
    }
}
