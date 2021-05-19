package com.guohua.north_bulb.fragmentNew;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guohua.north_bulb.AppContext;
import com.guohua.north_bulb.R;
import com.guohua.north_bulb.activity.AddTimerActivity;
import com.guohua.north_bulb.adapter.TimerListAdapter;
import com.guohua.north_bulb.bean.TimerModel;
import com.guohua.north_bulb.communication.BLEConstant;
import com.guohua.north_bulb.util.BLECodeUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AboutFragment extends Fragment {

    public static final String TAG = AboutFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_about, container, false);
        init();
        return rootView;
    }

    private View rootView;
    TextView txt_weliveupnorth, txt_Supportweliveupnorth;

    public Context mContext;

    private void init() {
        mContext = getActivity();
        findViewsByIds();
    }

    private void findViewsByIds() {

        txt_weliveupnorth = (TextView) rootView.findViewById(R.id.txt_weliveupnorth);
        txt_Supportweliveupnorth = (TextView) rootView.findViewById(R.id.txt_Supportweliveupnorth);

        txt_weliveupnorth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.weliveupnorth.com"));
                startActivity(myIntent);
            }
        });
        txt_Supportweliveupnorth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                emailIntent.setType("vnd.android.cursor.item/email");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"Support@weliveupnorth.com"});
                startActivity(Intent.createChooser(emailIntent, "Send Support mail..."));
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
