package com.guohua.north_bulb.fragmentNew;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guohua.north_bulb.AppContext;
import com.guohua.north_bulb.R;
import com.guohua.north_bulb.activity.AddGroupActivity;
import com.guohua.north_bulb.activity.AddTimerActivity;
import com.guohua.north_bulb.adapter.GroupListAdapter;
import com.guohua.north_bulb.adapter.TimerListAdapter;
import com.guohua.north_bulb.bean.Device;
import com.guohua.north_bulb.bean.Group;
import com.guohua.north_bulb.bean.TimerModel;
import com.guohua.north_bulb.communication.BLEConstant;
import com.guohua.north_bulb.util.BLECodeUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TimerListFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = TimerListFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_list_timer, container, false);
        init();
        return rootView;
    }

    private View rootView;
    private ListView lstTimerList;
    private TimerListAdapter timerListAdapter;
    private Button btnAdd;
    ArrayList<TimerModel> timerModels;
    public Context mContext;

    private void init() {
        mContext = getActivity();
        findViewsByIds();
    }


    private void findViewsByIds() {

        lstTimerList = (ListView) rootView.findViewById(R.id.lstTimerList);
        btnAdd = (Button) rootView.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        timerModels = new ArrayList<>();
        timerListAdapter = new TimerListAdapter(mContext);
        lstTimerList.setAdapter(timerListAdapter);

        Gson gson = new Gson();
        Type typeTimerModel = new TypeToken<List<TimerModel>>() {
        }.getType();

        String timerString = AppContext.preferenceGetString(BLECodeUtils.TIMER_LIST, "");

        Log.e(TAG, "json " + timerString);
        if (!TextUtils.isEmpty(timerString) && !timerString.equals("")) {

            timerModels = gson.fromJson(timerString, typeTimerModel);
            Log.e(TAG, "fromJson " + timerModels);

            timerListAdapter.addAllTimer(timerModels);

        }
    }

    @Override
    public void onStop() {
        super.onStop();

        // Dont forget to cancel the running timers
        timerListAdapter.cancelAllTimers();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btnAdd:

                Intent intent = new Intent(mContext, AddTimerActivity.class);
                startActivityForResult(intent, BLEConstant.REQUEST_TIMER_ADD);

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "requestCode = " + requestCode + " resultCode = " + resultCode);

        if (requestCode == BLEConstant.REQUEST_TIMER_ADD && resultCode == BLEConstant.RESULT_TIMER_ADD) {

            try {
                TimerModel timerModel = new TimerModel();
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    timerModel = (TimerModel) bundle.getSerializable(BLEConstant.EXTRA_TIMER_LIST);
                }
                timerListAdapter.addTimer(timerModel);
                timerListAdapter.notifyDataSetChanged();

                if (timerModels.contains(timerModel)) {
                    Log.e(TAG, "timer same");
                } else {
                    timerModels.add(timerModel);
                    Log.e(TAG, "timer not same");
                }
                Gson gson = new Gson();
                Type typeTimer = new TypeToken<List<TimerModel>>() {
                }.getType();
                String json = gson.toJson(timerModels, typeTimer);
                AppContext.preferencePutString(BLECodeUtils.TIMER_LIST, json);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
