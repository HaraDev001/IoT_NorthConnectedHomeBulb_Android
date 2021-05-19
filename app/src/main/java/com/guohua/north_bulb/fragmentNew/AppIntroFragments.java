package com.guohua.north_bulb.fragmentNew;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.guohua.north_bulb.R;

public class AppIntroFragments extends Fragment {

    View mView;

    private int layoutResId, ivDrawable;



    private static final String ARG_LAYOUT_RES_ID = "layoutResId";
    private static final String ARG_LAYOUT_IV_DRAWABLE = "ivDrawable";

    public static AppIntroFragments newInstance(int layoutResId, int ivDrawable) {
        AppIntroFragments sampleSlide = new AppIntroFragments();

        Bundle bundleArgs = new Bundle();
        bundleArgs.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        bundleArgs.putInt(ARG_LAYOUT_IV_DRAWABLE, ivDrawable);
        sampleSlide.setArguments(bundleArgs);

        return sampleSlide;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ARG_LAYOUT_RES_ID) && getArguments().containsKey(ARG_LAYOUT_IV_DRAWABLE))
            layoutResId = getArguments().getInt(ARG_LAYOUT_RES_ID);
        ivDrawable = getArguments().getInt(ARG_LAYOUT_IV_DRAWABLE);
    }

    public AppIntroFragments() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_app_intro_fragment1, container, false);

        ImageView asd = (ImageView) mView.findViewById(R.id.iv_appintro);
        asd.setImageResource(ivDrawable);
        //Picasso.with(getActivity()).load(ivDrawable).into(ivAppIntro);
        return mView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
