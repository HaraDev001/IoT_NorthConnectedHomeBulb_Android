package com.guohua.north_bulb.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.guohua.north_bulb.AppContext;
import com.guohua.north_bulb.R;
import com.guohua.north_bulb.bean.Device;
import com.guohua.north_bulb.communication.BLEConstant;
import com.guohua.north_bulb.dialog.ChooseFavColorDilaog;
import com.guohua.north_bulb.net.SendRunnable;
import com.guohua.north_bulb.net.ThreadPool;
import com.guohua.north_bulb.util.CircularTextView;
import com.guohua.north_bulb.util.CodeUtils;
import com.guohua.north_bulb.util.Constant;
import com.guohua.north_bulb.util.ToolUtils;

public class PalletActivity extends AppCompatActivity {
    private ThreadPool pool = null;
    public static final String TAG = PalletActivity.class.getSimpleName();
    public static final int COLOR_WARM = 100;
    public static final int COLOR_PURE = 101;
    public static final int COLOR_COOL = 102;
    public static final int COLOR_DAY = 103;
    public static final int COLOR_FAV1_CLICK = 5;
    public static final int COLOR_FAV2_CLICK = 6;
    public static final int COLOR_FAV3_CLICK = 7;
    public static final int COLOR_FAV4_CLICK = 8;
    public static final String COLOR_FAV1 = "COLOR_FAV1";
    public static final String COLOR_FAV2 = "COLOR_FAV2";
    public static final String COLOR_FAV3 = "COLOR_FAV3";
    public static final String COLOR_FAV4 = "COLOR_FAV4";


    private volatile static PalletActivity palletActivity = null;

    public static PalletActivity getInstance() {
        if (palletActivity == null) {
            synchronized (MenuActivity.class) {
                if (palletActivity == null) {
                    palletActivity = new PalletActivity();
                }
            }
        }
        return palletActivity;
    }

    Context mContext;
    private SeekBar changeBrightness;
    private ImageView changeColor, iv_music, iv_Shake;
    private ImageButton switcher;
    private CircularTextView tvFavColor1,tvFavColor2,tvFavColor3,tvFavColor4;
    private CircularTextView tvWarmColor,tvPureColor,tvCoolColor,tvDayColor;
    private int currentColor = Color.GREEN;
    private int currentBrightness = 255;
    private Bitmap bmp = null;
    private int FavColorClick = -1;
    private ImageView ivMarker;
    float initialMarkerX,initialMarkerY;
    Device device;
    RelativeLayout llWarm,llPure,llCool,llDay;
    //TextView tvTempR,tvTempG,tvTempB,tvTempA;
    private boolean isFavColorClicked = false;

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        // Inflate the layout for this fragment
        setContentView(R.layout.activity_pallet);
        mContext = PalletActivity.this;
        getintent();
        init();
    }

    private void getintent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            device = new Device();
            device = (Device) bundle.getSerializable(BLEConstant.EXTRA_DEVICE_LIST);
        }
    }

    private void init() {
        pool = ThreadPool.getInstance();
        findViewsByIds();
        initValues();
    }

    private void findViewsByIds() {

        RelativeLayout  llFavColor1,llFavColor2,llFavColor3,llFavColor4;
        changeBrightness = (SeekBar) findViewById(R.id.sb_brightness_main);
        changeColor = (ImageView) findViewById(R.id.iv_color_main);

        iv_music = (ImageView) findViewById(R.id.iv_music);
        iv_Shake = (ImageView) findViewById(R.id.iv_Shake);

        switcher = (ImageButton) findViewById(R.id.btn_switch_main);
        TextView tv_title_title = (TextView) findViewById(R.id.tv_title_title);
        tv_title_title.setText(getString(R.string.colorpallet));
        ImageView add = (ImageView) findViewById(R.id.iv_back_title);
        add.setImageResource(R.drawable.icon_back);

        llWarm = (RelativeLayout) findViewById(R.id.ll_red_color);
        llPure = (RelativeLayout) findViewById(R.id.ll_orange_color);
        llCool = (RelativeLayout) findViewById(R.id.ll_green_color);
        llDay = (RelativeLayout) findViewById(R.id.ll_blue_color);
        llFavColor1 = (RelativeLayout) findViewById(R.id.ll_fav1_color);
        llFavColor2 = (RelativeLayout) findViewById(R.id.ll_fav2_color);
        llFavColor3 = (RelativeLayout) findViewById(R.id.ll_fav3_color);
        llFavColor4 = (RelativeLayout) findViewById(R.id.ll_fav4_color);
        ivMarker = (ImageView) findViewById(R.id.iv_color_marker);

        Log.e("tag","get x = "+ivMarker.getX());
        Log.e("tag","get x = "+ivMarker.getY());
        tvFavColor1 = (CircularTextView) findViewById(R.id.tv_fav1_color);
        tvFavColor2 = (CircularTextView) findViewById(R.id.tv_fav2_color);
        tvFavColor3 = (CircularTextView) findViewById(R.id.tv_fav3_color);
        tvFavColor4 = (CircularTextView) findViewById(R.id.tv_fav4_color);

        tvWarmColor = (CircularTextView) findViewById(R.id.tv_warm_color);
        tvPureColor = (CircularTextView) findViewById(R.id.tv_pure_color);
        tvCoolColor = (CircularTextView) findViewById(R.id.tv_cool_color);
        tvDayColor = (CircularTextView) findViewById(R.id.tv_day_color);



//        tvTempA = (TextView) findViewById(R.id.tv_temp_A);
//        tvTempR = (TextView) findViewById(R.id.tv_temp_r);
//        tvTempG = (TextView) findViewById(R.id.tv_temp_G);
//        tvTempB = (TextView) findViewById(R.id.tv_temp_B);

        setSelectedFavColor();
        add.setOnClickListener(mOnClickListener);
        switcher.setOnClickListener(mOnClickListener);
        iv_music.setOnClickListener(mOnClickListener);
        iv_Shake.setOnClickListener(mOnClickListener);
        llFavColor1.setOnClickListener(mOnClickListener);
        llFavColor2.setOnClickListener(mOnClickListener);
        llFavColor3.setOnClickListener(mOnClickListener);
        llFavColor4.setOnClickListener(mOnClickListener);
        llWarm.setOnClickListener(mOnClickListener);
        llDay.setOnClickListener(mOnClickListener);
        llCool.setOnClickListener(mOnClickListener);
        llPure.setOnClickListener(mOnClickListener);


        llFavColor1.setOnLongClickListener(mOnLongClickListener);
        llFavColor2.setOnLongClickListener(mOnLongClickListener);
        llFavColor3.setOnLongClickListener(mOnLongClickListener);
        llFavColor4.setOnLongClickListener(mOnLongClickListener);

        changeColor.setOnTouchListener(mOnTouchListener);
        changeBrightness.setOnSeekBarChangeListener(mOnSeekBarChangeListener);

        setDefaultCircleDefaultColors();
    }

    private void setDefaultCircleDefaultColors(){

        tvWarmColor.setSolidColorWithStrokeWidthAndColor(getResources().getString(R.color.warm),getResources().getString(R.color.black),1);
        tvCoolColor.setSolidColorWithStrokeWidthAndColor(getResources().getString(R.color.cool),getResources().getString(R.color.black),1);
        tvDayColor.setSolidColorWithStrokeWidthAndColor(getResources().getString(R.color.day),getResources().getString(R.color.black),1);
        tvPureColor.setSolidColorWithStrokeWidthAndColor(getResources().getString(R.color.pure),getResources().getString(R.color.black),1);

    }
    private void setSelectedFavColor(){

        GradientDrawable drawable;

        if (AppContext.preferenceGetInteger(COLOR_FAV1,0)!=0){
            tvFavColor1.setSolidColorWithStrokeWidthAndColor(getHexStringColorFromInt(AppContext.preferenceGetInteger(COLOR_FAV1,0)),getResources().getString(R.color.black),1);
            /*drawable = (GradientDrawable) tvFavColor1.getBackground();
            drawable.setColor(AppContext.preferenceGetInteger(COLOR_FAV1,0));*/
        }else{
            tvFavColor1.setSolidColorWithStrokeWidthAndColor(getResources().getString(R.color.defaultFavColor),getResources().getString(R.color.black),1);
            AppContext.preferencePutInteger(COLOR_FAV1, getFavColorHex(255,226,226,226)); // for default fav1 red500 color
        }

        if (AppContext.preferenceGetInteger(COLOR_FAV2,0)!=0){
            /*drawable = (GradientDrawable) tvFavColor2.getBackground();
            drawable.setColor(AppContext.preferenceGetInteger(COLOR_FAV2,0));*/
            tvFavColor2.setSolidColorWithStrokeWidthAndColor(getHexStringColorFromInt(AppContext.preferenceGetInteger(COLOR_FAV2,0)),getResources().getString(R.color.black),1);
        }else{
            tvFavColor2.setSolidColorWithStrokeWidthAndColor(getResources().getString(R.color.defaultFavColor),getResources().getString(R.color.black),1);
            AppContext.preferencePutInteger(COLOR_FAV2, getFavColorHex(255,226,226,226));   // for default fav2 blue color
        }


        if (AppContext.preferenceGetInteger(COLOR_FAV3,0)!=0){
            /*drawable = (GradientDrawable) tvFavColor3.getBackground();
            drawable.setColor(AppContext.preferenceGetInteger(COLOR_FAV3,0));*/
            tvFavColor3.setSolidColorWithStrokeWidthAndColor(getHexStringColorFromInt(AppContext.preferenceGetInteger(COLOR_FAV3,0)),getResources().getString(R.color.black),1);
        }else{
            tvFavColor3.setSolidColorWithStrokeWidthAndColor(getResources().getString(R.color.defaultFavColor),getResources().getString(R.color.black),1);
            AppContext.preferencePutInteger(COLOR_FAV3, getFavColorHex(255,226,226,226)); // for default fav3 cyan color
        }


        if (AppContext.preferenceGetInteger(COLOR_FAV4,0)!=0){
            /*drawable = (GradientDrawable) tvFavColor4.getBackground();
            drawable.setColor(AppContext.preferenceGetInteger(COLOR_FAV4,0));*/
            tvFavColor4.setSolidColorWithStrokeWidthAndColor(getHexStringColorFromInt(AppContext.preferenceGetInteger(COLOR_FAV4,0)),getResources().getString(R.color.black),1);
        }else{
            tvFavColor4.setSolidColorWithStrokeWidthAndColor(getResources().getString(R.color.defaultFavColor),getResources().getString(R.color.black),1);
            AppContext.preferencePutInteger(COLOR_FAV4, getFavColorHex(255,226,226,226));  // for default fav4 purple color
        }

    }

    private void initValues() {

        bmp = ((BitmapDrawable) changeColor.getDrawable()).getBitmap();
        // changeBrightness.setProgress(currentBrightness);

        if (device.isSelected()) {
            switcher.setImageResource(R.drawable.icon_light_on);
        } else {
            switcher.setImageResource(R.drawable.icon_light_off);
        }

        ivMarker.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                initialMarkerX = ivMarker.getX();
                initialMarkerY = ivMarker.getY();
                currentColor = bmp.getPixel((int) initialMarkerX, (int) initialMarkerY);
                Log.e("tag","get x = "+ivMarker.getX()+" , get y = "+ivMarker.getY());
                ivMarker.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {

            GradientDrawable drawable;
            int favColor;
            switch (view.getId()){

                case R.id.ll_fav1_color:

                    /*favColor = AppContext.preferenceGetInteger(COLOR_FAV1,0);
                    Log.e(TAG, "fav1 color = "+favColor);

                        isFavColorClicked = true;
                        FavColorClick = COLOR_FAV1_CLICK;
                        ChooseFavColorDilaog.showFavAlertDialog(mContext);*/

                    changeTheColor();
                    fav1Selector();
                    tvFavColor1.setSolidColor(getHexStringColorFromInt(currentColor));
                    AppContext.preferencePutInteger(COLOR_FAV1, currentColor);
                    /*drawable = (GradientDrawable) tvFavColor1.getBackground();
                    drawable.setColor(getResources().getColor(android.R.color.transparent));
                    AppContext.preferencePutInteger(COLOR_FAV1,0);*/
                    break;
                case R.id.ll_fav2_color:

                    /*favColor = AppContext.preferenceGetInteger(COLOR_FAV2,0);
                    Log.e(TAG, "fav1 color = "+favColor);

                        isFavColorClicked = true;
                        FavColorClick = COLOR_FAV2_CLICK;
                        ChooseFavColorDilaog.showFavAlertDialog(mContext);*/

                    changeTheColor();
                    fav2Selector();
                    /*drawable = (GradientDrawable) tvFavColor2.getBackground();
                    drawable.setColor(currentColor);*/
                    tvFavColor2.setSolidColor(getHexStringColorFromInt(currentColor));
                    AppContext.preferencePutInteger(COLOR_FAV2, currentColor);
                    /*drawable = (GradientDrawable) tvFavColor2.getBackground();
                    drawable.setColor(getResources().getColor(android.R.color.transparent));
                    AppContext.preferencePutInteger(COLOR_FAV2,0);*/
                    break;
                case R.id.ll_fav3_color:

                    /*favColor = AppContext.preferenceGetInteger(COLOR_FAV3,0);
                    Log.e(TAG, "fav1 color = "+favColor);

                        isFavColorClicked = true;
                        FavColorClick = COLOR_FAV3_CLICK;
                        ChooseFavColorDilaog.showFavAlertDialog(mContext);
*/
                    changeTheColor();
                    fav3Selector();
                    /*drawable = (GradientDrawable) tvFavColor3.getBackground();
                    drawable.setColor(currentColor);*/
                    tvFavColor3.setSolidColor(getHexStringColorFromInt(currentColor));
                    AppContext.preferencePutInteger(COLOR_FAV3, currentColor);
                    /*drawable = (GradientDrawable) tvFavColor3.getBackground();
                    drawable.setColor(getResources().getColor(android.R.color.transparent));
                    AppContext.preferencePutInteger(COLOR_FAV3,0);*/
                    break;
                case R.id.ll_fav4_color:

                    /*favColor = AppContext.preferenceGetInteger(COLOR_FAV4,0);
                    Log.e(TAG, "fav1 color = "+favColor);

                        isFavColorClicked = true;
                        FavColorClick = COLOR_FAV4_CLICK;
                        ChooseFavColorDilaog.showFavAlertDialog(mContext);
*/

                    changeTheColor();
                    fav4Selector();
                    /*drawable = (GradientDrawable) tvFavColor4.getBackground();
                    drawable.setColor(currentColor);*/
                    tvFavColor4.setSolidColor(getHexStringColorFromInt(currentColor));
                    AppContext.preferencePutInteger(COLOR_FAV4, currentColor);
                    /*drawable = (GradientDrawable) tvFavColor4.getBackground();
                    drawable.setColor(getResources().getColor(android.R.color.transparent));
                    AppContext.preferencePutInteger(COLOR_FAV4,0);*/
                    break;
            }
            return true;
        }
    };

    private String getHexStringColorFromInt(int color){

       return String.format("#%06X", (0xFFFFFF & color));
    }
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int favColor;

            switch (v.getId()) {
                case R.id.btn_switch_main:
                    switchLight(device.isSelected());
                    break;

                case R.id.iv_back_title:

                    Log.e(TAG, "device := " + device.toString());

                    Intent intent1 = new Intent();
                    Bundle b1 = new Bundle();
                    b1.putSerializable(BLEConstant.EXTRA_DEVICE_LIST, device);
                    intent1.putExtras(b1);
                    setResult(BLEConstant.RESULT_COLOR_PALETTE, intent1);
                    finish();

                    break;

                case R.id.iv_music:

                    ToolUtils.requestPermissions(PalletActivity.this, Manifest.permission.RECORD_AUDIO, Constant.MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
                    ToolUtils.requestPermissions(PalletActivity.this, Manifest.permission.MODIFY_AUDIO_SETTINGS, Constant.MY_PERMISSIONS_REQUEST_MODIFY_AUDIO_SETTINGS);

                    if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(mContext, Manifest.permission.MODIFY_AUDIO_SETTINGS) == PackageManager.PERMISSION_GRANTED) {

                        Intent intent = new Intent(mContext, VisualizerActivity.class);
                        Bundle b = new Bundle();
                        b.putSerializable(BLEConstant.EXTRA_DEVICE_LIST, device);
                        intent.putExtras(b);
                        startActivity(intent);

                    } else {

                        Toast.makeText(mContext, R.string.prompt_recordvideo_permission, Toast.LENGTH_LONG).show();
                    }

                    break;

                case R.id.iv_Shake:

                    Intent intent = new Intent(PalletActivity.this, ShakeActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable(BLEConstant.EXTRA_DEVICE_LIST, device);
                    intent.putExtras(b);
                    startActivity(intent);

                    break;
                case R.id.ll_red_color:
                    warmSelector();
                    changeFixColors(COLOR_WARM);
                    break;
                case R.id.ll_orange_color:
                    pureSelector();
                    changeFixColors(COLOR_PURE);
                    break;
                case R.id.ll_green_color:
                    coolSelector();
                    changeFixColors(COLOR_COOL);
                    break;
                case R.id.ll_blue_color:
                    daySelector();
                    changeFixColors(COLOR_DAY);
                    break;
                case R.id.ll_fav1_color:

                    favColor = AppContext.preferenceGetInteger(COLOR_FAV1,0);
                    fav1Selector();
                    ChangeFavColors(favColor);

                    break;
                case R.id.ll_fav2_color:

                    favColor = AppContext.preferenceGetInteger(COLOR_FAV2,0);
                    fav2Selector();
                    ChangeFavColors(favColor);
                    break;
                case R.id.ll_fav3_color:

                    favColor = AppContext.preferenceGetInteger(COLOR_FAV3,0);
                    fav3Selector();
                    ChangeFavColors(favColor);
                    break;
                case R.id.ll_fav4_color:

                    favColor = AppContext.preferenceGetInteger(COLOR_FAV4,0);
                    fav4Selector();
                    ChangeFavColors(favColor);
                    break;

            }
        }
    };

    private void fav1Selector(){

        tvFavColor1.setStrokeWidth(3);
        tvFavColor2.setStrokeWidth(1);
        tvFavColor3.setStrokeWidth(1);
        tvFavColor4.setStrokeWidth(1);
        tvDayColor.setStrokeWidth(1);
        tvCoolColor.setStrokeWidth(1);
        tvPureColor.setStrokeWidth(1);
        tvWarmColor.setStrokeWidth(1);
    }

    private void fav2Selector(){

        tvFavColor1.setStrokeWidth(1);
        tvFavColor2.setStrokeWidth(3);
        tvFavColor3.setStrokeWidth(1);
        tvFavColor4.setStrokeWidth(1);
        tvDayColor.setStrokeWidth(1);
        tvCoolColor.setStrokeWidth(1);
        tvPureColor.setStrokeWidth(1);
        tvWarmColor.setStrokeWidth(1);
    }
    private void fav3Selector(){

        tvFavColor1.setStrokeWidth(1);
        tvFavColor2.setStrokeWidth(1);
        tvFavColor3.setStrokeWidth(3);
        tvFavColor4.setStrokeWidth(1);
        tvDayColor.setStrokeWidth(1);
        tvCoolColor.setStrokeWidth(1);
        tvPureColor.setStrokeWidth(1);
        tvWarmColor.setStrokeWidth(1);
    }
    private void fav4Selector(){

        tvFavColor1.setStrokeWidth(1);
        tvFavColor2.setStrokeWidth(1);
        tvFavColor3.setStrokeWidth(1);
        tvFavColor4.setStrokeWidth(3);
        tvDayColor.setStrokeWidth(1);
        tvCoolColor.setStrokeWidth(1);
        tvPureColor.setStrokeWidth(1);
        tvWarmColor.setStrokeWidth(1);
    }

    private void warmSelector(){
        /*GradientDrawable drawable,drawableCool,drawablePure,drawableDay;

        drawable = (GradientDrawable) tvWarmColor.getBackground();
        drawable.setStroke(5,Color.BLACK);
        drawable.setCornerRadius(100.0f);

        drawableCool = (GradientDrawable) tvCoolColor.getBackground();
        drawableCool.setStroke(1,Color.BLACK);

        drawablePure = (GradientDrawable) tvPureColor.getBackground();
        drawablePure.setStroke(1,Color.BLACK);

        drawableDay  = (GradientDrawable) tvDayColor.getBackground();
        drawableDay.setStroke(1,Color.BLACK);*/

        //tvWarmColor.setStrokeColor("#000000");
        //tvWarmColor.setSolidColor("#FFE6B300");
        tvWarmColor.setStrokeWidth(3);
        tvPureColor.setStrokeWidth(1);
        tvCoolColor.setStrokeWidth(1);
        tvDayColor.setStrokeWidth(1);
        tvFavColor1.setStrokeWidth(1);
        tvFavColor2.setStrokeWidth(1);
        tvFavColor3.setStrokeWidth(1);
        tvFavColor4.setStrokeWidth(1);

    }

    private void coolSelector(){
        /*GradientDrawable drawable,drawableCool,drawablePure,drawableDay;

        drawable = (GradientDrawable) tvWarmColor.getBackground();
        drawable.setStroke(1,Color.BLACK);

        drawableCool = (GradientDrawable) tvCoolColor.getBackground();
        drawableCool.setStroke(5,Color.BLACK);

        drawablePure = (GradientDrawable) tvPureColor.getBackground();
        drawablePure.setStroke(1,Color.BLACK);

        drawableDay  = (GradientDrawable) tvDayColor.getBackground();
        drawableDay.setStroke(1,Color.BLACK);*/

        tvCoolColor.setStrokeWidth(3);
        tvWarmColor.setStrokeWidth(1);
        tvPureColor.setStrokeWidth(1);
        tvDayColor.setStrokeWidth(1);
        tvFavColor1.setStrokeWidth(1);
        tvFavColor2.setStrokeWidth(1);
        tvFavColor3.setStrokeWidth(1);
        tvFavColor4.setStrokeWidth(1);
    }
    private void pureSelector(){
        /*GradientDrawable drawable,drawableCool,drawablePure,drawableDay;

        drawable = (GradientDrawable) tvWarmColor.getBackground();
        drawable.setStroke(1,Color.BLACK);

        drawableCool = (GradientDrawable) tvCoolColor.getBackground();
        drawableCool.setStroke(1,Color.BLACK);

        drawablePure = (GradientDrawable) tvPureColor.getBackground();
        drawablePure.setStroke(5,Color.BLACK);
        drawable.setCornerRadius(100.0f);

        drawableDay  = (GradientDrawable) tvDayColor.getBackground();
        drawableDay.setStroke(1,Color.BLACK);*/

        tvPureColor.setStrokeWidth(3);
        tvCoolColor.setStrokeWidth(1);
        tvWarmColor.setStrokeWidth(1);
        tvDayColor.setStrokeWidth(1);
        tvFavColor1.setStrokeWidth(1);
        tvFavColor2.setStrokeWidth(1);
        tvFavColor3.setStrokeWidth(1);
        tvFavColor4.setStrokeWidth(1);
    }

    private void daySelector(){
        /*GradientDrawable drawable,drawableCool,drawablePure,drawableDay;

        drawable = (GradientDrawable) tvWarmColor.getBackground();
        drawable.setStroke(1,Color.BLACK);

        drawableCool = (GradientDrawable) tvCoolColor.getBackground();
        drawableCool.setStroke(1,Color.BLACK);

        drawablePure = (GradientDrawable) tvPureColor.getBackground();
        drawablePure.setStroke(1,Color.BLACK);

        drawableDay  = (GradientDrawable) tvDayColor.getBackground();
        drawableDay.setStroke(5,Color.BLACK);*/

        tvPureColor.setStrokeWidth(1);

        tvCoolColor.setStrokeWidth(1);
        tvWarmColor.setStrokeWidth(1);
        tvDayColor.setStrokeWidth(3);
        tvFavColor1.setStrokeWidth(1);
        tvFavColor2.setStrokeWidth(1);
        tvFavColor3.setStrokeWidth(1);
        tvFavColor4.setStrokeWidth(1);
    }

    private void saveThePassword(Device device, String password) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(PalletActivity.this);
        String deviceAddress;
        deviceAddress = device.getDeviceAddress();
        final String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_PASSWORD, new String[]{sp.getString(deviceAddress, CodeUtils.password), password});
        final String finalDeviceAddress = deviceAddress;
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 5; i++) {
                    ThreadPool.getInstance().addTask(new SendRunnable(finalDeviceAddress, data));
                    try {
                        Thread.sleep(Constant.HANDLERDELAY / 3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        System.out.println(" centerfragment changePassword deviceAddress: " + deviceAddress + "; data:  " + data);
        sp.edit().putString(deviceAddress, password).apply();
    }

    private void switchLight(boolean flag) {

        final String data;

        if (!flag) {
            data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_SWITCH, new Object[]{Constant.CMD_OPEN_LIGHT});
        } else {
            data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_SWITCH, new Object[]{Constant.CMD_CLOSE_LIGHT});
        }
        saveThePassword(device, "0000");

        Boolean ischange = AppContext.getInstance().on_off(device.getDeviceAddress(), data);

        if (device.isSelected()) {
            if (ischange) {
                device.setSelected(false);
                switcher.setImageResource(R.drawable.icon_light_off);
            }
        } else {
            if (ischange) {
                device.setSelected(true);
                switcher.setImageResource(R.drawable.icon_light_on);
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

//                saveThePassword(device, "0000");
//
//                Boolean ischange = AppContext.getInstance().on_off(device.getDeviceAddress(), data);
//
//                if (device.isSelected()) {
//                    if (ischange) {
//                        device.setSelected(false);
//                        switcher.setImageResource(R.drawable.icon_light_off);
//                    }
//                } else {
//                    if (ischange) {
//                        device.setSelected(true);
//                        switcher.setImageResource(R.drawable.icon_light_on);
//                    }
//                }

                try {
                    Thread.sleep(Constant.HANDLERDELAY / 3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        if (device.isSelected()) {
            currentColor = Color.argb(255, 255, 255, 255);
        } else {
            currentColor = Color.argb(0, 0, 0, 0);
        }
    }


    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int id = seekBar.getId();
            if (id == R.id.sb_brightness_main) {
                currentBrightness = progress;
                changeColorBrightness(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int id = seekBar.getId();
            int progress = seekBar.getProgress();
            if (id == R.id.sb_brightness_main) {
                currentBrightness = progress;
                changeColorBrightness(progress);
            }
        }
    };

    private void changeColorBrightness(int progress) {

        String data;
        int alpha = 0;
        int red = 0;
        int green = 0;
        int blue = 0;
        if (currentColor == Color.WHITE) {
            data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_CONTROL,
                    new Object[]{progress, progress, progress, progress});
        } else {
            alpha = 0;
            red = Color.red(currentColor) * progress / 255;
            green = Color.green(currentColor) * progress / 255;
            blue = Color.blue(currentColor) * progress / 255;
            data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_CONTROL, new Object[]{alpha, red, green, blue});
        }
        pool.addOtherTask(new SendRunnable(device.getDeviceAddress(), data));
        if (device != null) {
            device.setSelected(true);
            switcher.setImageResource(R.drawable.icon_light_on);
        }
    }

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            float x = event.getX();
            float y = event.getY();


            if (!validate(x, y)) {
                return false;
            }


            System.out.println("x: " + x + "; y:" + y);

            currentColor = bmp.getPixel((int) x, (int) y);
            switcher.setImageResource(R.drawable.icon_light_on);

            int action = event.getAction();
            switch (action) {

                case MotionEvent.ACTION_UP:
                    AppContext.getInstance().currentColor = currentColor;
                    break;
                case MotionEvent.ACTION_MOVE:

                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) ivMarker.getLayoutParams();
                    params.leftMargin = (int) (x-50);
                    params.topMargin = (int) (y-50);
                    ivMarker.setLayoutParams(params);

//                    ivMarker.setX(x-dx);
//                    ivMarker.setX(y-dy);
                    break;
            }


            /*if (isFavColorClicked){

                int selectedColor = getFavColorHex();
                Log.e(TAG, "selected color = "+ String.valueOf(selectedColor));
                Log.e(TAG, "fav color click number ="+ String.valueOf(FavColorClick));
                if (selectedColor!=0) {
                    switch (FavColorClick) {
                        case COLOR_FAV1_CLICK:

                            GradientDrawable drawable = (GradientDrawable) tvFavColor1.getBackground();
                            drawable.setColor(selectedColor);
                           // tvFavColor1.setBackgroundColor(selectedColor);
                            AppContext.preferencePutInteger(COLOR_FAV1, selectedColor);
                            break;
                        case COLOR_FAV2_CLICK:

                            drawable = (GradientDrawable) tvFavColor2.getBackground();
                            drawable.setColor(selectedColor);
                            //tvFavColor2.setBackgroundColor(selectedColor);
                            AppContext.preferencePutInteger(COLOR_FAV2, selectedColor);
                            break;
                        case COLOR_FAV3_CLICK:

                            drawable = (GradientDrawable) tvFavColor3.getBackground();
                            drawable.setColor(selectedColor);
                            //tvFavColor3.setBackgroundColor(selectedColor);
                            AppContext.preferencePutInteger(COLOR_FAV3, selectedColor);
                            break;
                        case COLOR_FAV4_CLICK:

                            drawable = (GradientDrawable) tvFavColor4.getBackground();
                            drawable.setColor(selectedColor);
                           // tvFavColor4.setBackgroundColor(selectedColor);
                            AppContext.preferencePutInteger(COLOR_FAV4, selectedColor);
                            break;
                    }
                }
                isFavColorClicked = false;
            }else*/
                changeTheColor();


            return true;
        }
    };

   /* private int getFavColorHex(){

        int alpha = Color.alpha(currentColor) * currentBrightness / 255;
        int red = Color.red(currentColor) * currentBrightness / 255;
        int green = Color.green(currentColor) * currentBrightness / 255;
        int blue = Color.blue(currentColor) * currentBrightness / 255;

        return Color.argb(alpha,red,green,blue);

    }*/
    private int getFavColorHex(int alpha,int red,int green,int blue){

        alpha = alpha * currentBrightness / 255;
        red = red * currentBrightness / 255;
        green = green * currentBrightness / 255;
        blue = blue * currentBrightness / 255;

        return Color.argb(alpha,red,green,blue);

    }

    private void changeTheColor() {
        int alpha = Color.alpha(currentColor) * currentBrightness / 255;
        int red = Color.red(currentColor) * currentBrightness / 255;
        int green = Color.green(currentColor) * currentBrightness / 255;
        int blue = Color.blue(currentColor) * currentBrightness / 255;

//        tvTempA.setText("A : "+alpha);
//        tvTempR.setText("R : "+red);
//        tvTempG.setText("G : "+green);
//        tvTempB.setText("B : "+blue);

        fireColorChangeCommand(alpha,red,green,blue);
    }


    private void changeFixColors(int selectedColor){

        int alpha = 0,red = 0,green = 0,blue = 0;
        switch (selectedColor){
            case COLOR_WARM:
                alpha = currentBrightness;
                green = 214* currentBrightness / 255;blue = 212* currentBrightness/ 255;
                red = 253 * currentBrightness / 255;
                currentColor = getResources().getColor(R.color.warm);
                break;
            case COLOR_PURE:
                alpha = currentBrightness;
                blue = 178* currentBrightness / 255;
                red = 254 * currentBrightness / 255;
                green = 223 * currentBrightness / 255;
                currentColor = getResources().getColor(R.color.pure);
                break;
            case COLOR_COOL:
                alpha = currentBrightness;
                blue = 254* currentBrightness / 255;
                red = 226* currentBrightness / 255;
                green = 242 * currentBrightness / 255;
                currentColor = getResources().getColor(R.color.cool);
                break;
            case COLOR_DAY:
                alpha = currentBrightness;
                red = 252* currentBrightness / 255;
                green = 250* currentBrightness / 255;
                blue = 250* currentBrightness / 255;
                currentColor = getResources().getColor(R.color.day);
                break;
        }

        fireColorChangeCommand(alpha,red,green,blue);

    }

    private void fireColorChangeCommand(int alpha,int red,int green,int blue){

        if ((red == 0 && green == 0 && blue == 0) || (red < Constant.HARDWARELEDMINCOLORVALUE &&
                green < Constant.HARDWARELEDMINCOLORVALUE && blue < Constant.HARDWARELEDMINCOLORVALUE)) {
            return;
        }

        String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_CONTROL, new Object[]{alpha, red, green, blue});
        pool.addTask(new SendRunnable(device.getDeviceAddress(), data));
        if (device != null) {
            device.setSelected(true);
            switcher.setImageResource(R.drawable.icon_light_on);
        }
    }
    private void ChangeFavColors(int selectedColor){

        currentColor = selectedColor;
//        currentBrightness = Color.alpha(selectedColor);
        int alpha = Color.alpha(selectedColor) * currentBrightness / 255;
        int red = Color.red(selectedColor) * currentBrightness / 255;
        int green = Color.green(selectedColor) * currentBrightness / 255;
        int blue = Color.blue(selectedColor) * currentBrightness / 255;

        Log.e(TAG,"fav colors -> alpha = "+alpha+" red = "+red+" green = "+green+" blue = "+blue);
        fireColorChangeCommand(alpha,red,green,blue);

    }
    private boolean validate(float x, float y) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();


        System.out.println("bmp.getWidth(): " + width + ";bmp.getHeight():" + height);

        if (x < 0 || y < 0 || x >= width || y >= height) {
            return false;
        }
        double diameter = width < height ? width : height;
        int centerX = width / 2;
        int centerY = height / 2;
        double side = Math.sqrt(Math.pow(Math.abs(x - centerX), 2) + Math.pow(Math.abs(y - centerY), 2));
        double minGap = 40;
        if (side > minGap && side < diameter) {
            return true;
        }
        return false;
    }

}
