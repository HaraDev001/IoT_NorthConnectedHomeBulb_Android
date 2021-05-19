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
import com.guohua.north_bulb.activity.MenuActivity;
import com.guohua.north_bulb.adapter.GroupListAdapter;
import com.guohua.north_bulb.bean.Device;
import com.guohua.north_bulb.bean.Group;
import com.guohua.north_bulb.communication.BLEConstant;
import com.guohua.north_bulb.interfaceListner.OnActivityResult;
import com.guohua.north_bulb.util.BLECodeUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GroupListFragment extends Fragment implements View.OnClickListener, OnActivityResult {

    public static final String TAG = GroupListFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_list_group, container, false);
        init();
        return rootView;
    }

    private View rootView;
    private ListView lstGroupList;
    private GroupListAdapter groupListAdapter;
    private Button btnAdd;
    ArrayList<Group> groupList;
    public Context mContext;

    private void init() {
        mContext = getActivity();
        MenuActivity.setInitListener(GroupListFragment.this);
        findViewsByIds();
    }


    private void findViewsByIds() {

        lstGroupList = (ListView) rootView.findViewById(R.id.lstGroupList);
        btnAdd = (Button) rootView.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        groupList = new ArrayList<>();
        groupListAdapter = new GroupListAdapter(mContext);
        lstGroupList.setAdapter(groupListAdapter);

        Gson gson = new Gson();
        Type type = new TypeToken<List<Group>>() {
        }.getType();

        String Grouplist = AppContext.preferenceGetString(BLECodeUtils.GROUP_LIST, "");

        Log.e(TAG, "json " + Grouplist);
        if (!TextUtils.isEmpty(Grouplist) && !Grouplist.equals("")) {

            groupList = gson.fromJson(Grouplist, type);
            Log.e(TAG, "fromJson " + groupList);
            for (int i = 0; i < groupList.size(); i++) {
                // AppContext.getInstance().addDevice(deviceList.get(i));
            }
            groupListAdapter.addAllGroups(groupList);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btnAdd:

                Intent intent = new Intent(mContext, AddGroupActivity.class);
                startActivityForResult(intent, BLEConstant.REQUEST_GROUP_ADD);

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "requestCode = " + requestCode + " resultCode = " + resultCode);

        if (requestCode == BLEConstant.REQUEST_GROUP_ADD && resultCode == BLEConstant.RESULT_GROUP_ADD) {
            try {
                Group group = new Group();
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    group = (Group) bundle.getSerializable(BLEConstant.EXTRA_GROUP_LIST);
                }

                groupListAdapter.addGroup(group);
                groupListAdapter.notifyDataSetChanged();
                if (groupList.contains(group)) {
                    Log.e(TAG, " same");
                } else {
                    Log.e(TAG, "not same");
                    groupList.add(group);
                }
                Gson gson = new Gson();
                Type type = new TypeToken<List<Group>>() {
                }.getType();
                String grouplistSTR = gson.toJson(groupList, type);

                AppContext.preferencePutString(BLECodeUtils.GROUP_LIST, grouplistSTR);

                Log.e(TAG, "json " + grouplistSTR);

                List<Device> fromJson = gson.fromJson(grouplistSTR, type);

                Log.e(TAG, "fromJson " + fromJson);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == BLEConstant.REQUEST_GROUP_EDI && resultCode == BLEConstant.RESULT_GROUP_EDIT) {
            try {

                Gson gson = new Gson();
                Type type = new TypeToken<List<Group>>() {
                }.getType();

                String Grouplist = AppContext.preferenceGetString(BLECodeUtils.GROUP_LIST, "");

                Log.e(TAG, "json " + Grouplist);
                if (!TextUtils.isEmpty(Grouplist) && !Grouplist.equals("")) {

                    groupList = gson.fromJson(Grouplist, type);
                    Log.e(TAG, "fromJson " + groupList);

                    groupListAdapter.addAllGroups(groupList);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == BLEConstant.REQUEST_GROUP_EDI && resultCode == BLEConstant.RESULT_GROUP_DELETE) {
            try {

                Gson gson = new Gson();
                Type type = new TypeToken<List<Group>>() {
                }.getType();

                String Grouplist = AppContext.preferenceGetString(BLECodeUtils.GROUP_LIST, "");

                Log.e(TAG, "json " + Grouplist);
                if (!TextUtils.isEmpty(Grouplist) && !Grouplist.equals("")) {

                    groupList = gson.fromJson(Grouplist, type);
                    Log.e(TAG, "fromJson " + groupList);

                    groupListAdapter.addAllGroups(groupList);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onactivityresult(int requestCode, int resultCode, Intent data) {

        onActivityResult(requestCode, resultCode, data);

    }
}
