package com.guohua.north_bulb.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.guohua.north_bulb.AppContext;
import com.guohua.north_bulb.R;
import com.guohua.north_bulb.fragmentNew.AppIntroFragments;
import com.guohua.north_bulb.util.Constant;
import com.guohua.north_bulb.util.ToolUtils;


public class AppIntroActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_app_intro);

        addSlide(AppIntroFragments.newInstance(R.layout.fragment_app_intro_fragment1, R.drawable.info1));
        addSlide(AppIntroFragments.newInstance(R.layout.fragment_app_intro_fragment1, R.drawable.info2));
        addSlide(AppIntroFragments.newInstance(R.layout.fragment_app_intro_fragment1, R.drawable.info3));
        addSlide(AppIntroFragments.newInstance(R.layout.fragment_app_intro_fragment1, R.drawable.info4));

        setBarColor(Color.parseColor("#88E1CD"));
        setSeparatorColor(Color.parseColor("#A9A9A9"));

        showSkipButton(true);
        setProgressButtonEnabled(true);

    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        AppContext.preferencePutBoolean(Constant.IS_APPINTRO, true);
        startSignInActivity();
    }

    private void startSignInActivity() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        AppContext.preferencePutBoolean(Constant.IS_APPINTRO, true);
        startSignInActivity();
    }
}
