package com.guohua.north_bulb.bean;

import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.guohua.north_bulb.util.Constant;
import com.guohua.north_bulb.util.LogUtil;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Aladdin on 2016-8-24.
 * SceneMode 协议
 *                         1byte  SceneDatasHead                    |  1byte  |  1byte  |  1byte  |  1byte  |  1byte  |  1byte  |  1byte  |  1byte  |  1byte  |  1byte   |  1byte    |  1byte   |  1byte
 *  1     1       1      0     1      1          0          1       |  detaR  |  detaG  |  detaB  |  TdetaG |  TdetaG |  TdetaG |  TdetaG |  TdetaG |  TdetaG |  TdetaG  |  TdetaG   |  TdetaG  |   sum
 *默认  红上升  绿上升  蓝下降  默认 红到终继续  绿到终保持   蓝到终继续   | 红渐变值 |绿渐变值  | 蓝渐变值 |  红渐变时| 绿渐变时 | 蓝渐变时 |红渐变起点| 绿渐变起点| 蓝渐变起点| 红渐变终点| 绿渐变终点| 蓝渐变终点 |  校验
 */
public class SceneListInfo {

    public List<SceneInfo> sceneListInfo;

    public static class SceneInfo implements Serializable, Cloneable{
        public int SceneInfoId = -1;
        public String SceneName = "炫彩渐变";
        public String SceneDescription = "Diy Your livingcolors Light";
        public int RED_GRADIENT_DELAY = 0;
        public int GREEN_GRADIENT_DELAY = 0;
        public int BLUE_GRADIENT_DELAY = 0;
        public int IsStartGradientRampService = 0;
        public int SCENESTOPGAPVALUE = 50;
        public int SCENEGRADIENTGAPVALUE = 10;
        public boolean SceneGradientGapRedCBChecked = false;
        public boolean SceneGradientGapGreenCBChecked = false;
        public boolean SceneGradientGapBlueCBChecked = false;
        public int SceneCurClickColorImgOnOff[] = {0, 0, 0, 0};
        public int SceneDatasHead[] = {1, 0, 0, 0, 1, 0, 0, 0};
        public int SceneDefaultColor[] = {0, 0, 0, 0, 0, Constant.COLORMAXVALUE, Constant.COLORMAXVALUE, Constant.COLORMAXVALUE}; //白红绿蓝的起点和终点变化值
        public int SceneGradientRampStopGap[] = {SCENESTOPGAPVALUE, SCENESTOPGAPVALUE, SCENESTOPGAPVALUE, SCENESTOPGAPVALUE};
        public int SceneGradientRampGradientGap[] = {SCENEGRADIENTGAPVALUE, SCENEGRADIENTGAPVALUE, SCENEGRADIENTGAPVALUE, SCENEGRADIENTGAPVALUE};

        public SceneInfo(){

        }

        public SceneInfo(int sceneInfoId, String sceneName, int RED_GRADIENT_DELAY, int GREEN_GRADIENT_DELAY, int BLUE_GRADIENT_DELAY, int isStartGradientRampService,
                         int SCENESTOPGAPVALUE, int SCENEGRADIENTGAPVALUE, boolean sceneGradientGapRedCBChecked,
                         boolean sceneGradientGapGreenCBChecked, boolean sceneGradientGapBlueCBChecked, int[] sceneCurClickColorImgOnOff, int[] sceneDatasHead, int[] sceneDefaultColor,
                         int[] sceneGradientRampStopGap, int[] sceneGradientRampGradientGap) {
            this.SceneInfoId = sceneInfoId;
            this.SceneName = sceneName;
            this.RED_GRADIENT_DELAY = RED_GRADIENT_DELAY;
            this.GREEN_GRADIENT_DELAY = GREEN_GRADIENT_DELAY;
            this.BLUE_GRADIENT_DELAY = BLUE_GRADIENT_DELAY;
            this.IsStartGradientRampService = isStartGradientRampService;
            this.SCENESTOPGAPVALUE = SCENESTOPGAPVALUE;
            this.SCENEGRADIENTGAPVALUE = SCENEGRADIENTGAPVALUE;
            this.SceneGradientGapRedCBChecked = sceneGradientGapRedCBChecked;
            this.SceneGradientGapGreenCBChecked = sceneGradientGapGreenCBChecked;
            this.SceneGradientGapBlueCBChecked = sceneGradientGapBlueCBChecked;
            this.SceneCurClickColorImgOnOff = sceneCurClickColorImgOnOff;
            this.SceneDatasHead = sceneDatasHead;
            this.SceneDefaultColor = sceneDefaultColor;
            this.SceneGradientRampStopGap = sceneGradientRampStopGap;
            this.SceneGradientRampGradientGap = sceneGradientRampGradientGap;
        }

        /*public SceneInfo cloneSceneInfo(SceneInfo ss){
            return new SceneListInfo.SceneInfo(ss.SceneInfoId, ss.SceneName, ss.RED_GRADIENT_DELAY, ss.GREEN_GRADIENT_DELAY,
                    ss.BLUE_GRADIENT_DELAY, ss.IsStartGradientRampService, ss.SCENESTOPGAPVALUE, ss.SCENEGRADIENTGAPVALUE, ss.SceneGradientGapRedCBChecked,
                    ss.SceneGradientGapGreenCBChecked, ss.SceneGradientGapBlueCBChecked, ss.SceneCurClickColorImgOnOff, ss.SceneDatasHead, ss.SceneDefaultColor,
                    ss.SceneGradientRampStopGap, ss.SceneGradientRampGradientGap);
        }*/

        @Override
        public String toString() {
            return "SceneInfo{" +
                    "SceneInfoId=" + SceneInfoId +
                    ", SceneName='" + SceneName + '\'' +
                    ", SceneDescription='" + SceneDescription + '\'' +
                    ", RED_GRADIENT_DELAY=" + RED_GRADIENT_DELAY +
                    ", GREEN_GRADIENT_DELAY=" + GREEN_GRADIENT_DELAY +
                    ", BLUE_GRADIENT_DELAY=" + BLUE_GRADIENT_DELAY +
                    ", IsStartGradientRampService=" + IsStartGradientRampService +
                    ", SCENESTOPGAPVALUE=" + SCENESTOPGAPVALUE +
                    ", SCENEGRADIENTGAPVALUE=" + SCENEGRADIENTGAPVALUE +
                    ", SceneGradientGapRedCBChecked=" + SceneGradientGapRedCBChecked +
                    ", SceneGradientGapGreenCBChecked=" + SceneGradientGapGreenCBChecked +
                    ", SceneGradientGapBlueCBChecked=" + SceneGradientGapBlueCBChecked +
                    ", SceneCurClickColorImgOnOff=" + Arrays.toString(SceneCurClickColorImgOnOff) +
                    ", SceneDatasHead=" + Arrays.toString(SceneDatasHead) +
                    ", SceneDefaultColor=" + Arrays.toString(SceneDefaultColor) +
                    ", SceneGradientRampStopGap=" + Arrays.toString(SceneGradientRampStopGap) +
                    ", SceneGradientRampGradientGap=" + Arrays.toString(SceneGradientRampGradientGap) +
                    '}';
        }

        @Override
        public Object clone() throws CloneNotSupportedException {//http://blog.csdn.net/centre10/article/details/6847973  http://blog.csdn.net/vincevincevincevince/article/details/18177231
            SceneInfo cloneSceneInfo = null;
            try
            {
                cloneSceneInfo = (SceneInfo) super.clone();//浅拷贝
                /*cloneSceneInfo.SceneInfoId = this.SceneInfoId;
                cloneSceneInfo.SceneName = this.SceneName;
                cloneSceneInfo.SceneDescription = this.SceneDescription;
                cloneSceneInfo.RED_GRADIENT_DELAY = this.RED_GRADIENT_DELAY;
                cloneSceneInfo.GREEN_GRADIENT_DELAY = this.GREEN_GRADIENT_DELAY;
                cloneSceneInfo.BLUE_GRADIENT_DELAY = this.BLUE_GRADIENT_DELAY;
                cloneSceneInfo.IsStartGradientRampService = this.IsStartGradientRampService;
                cloneSceneInfo.SCENESTOPGAPVALUE = this.SCENESTOPGAPVALUE;
                cloneSceneInfo.SCENEGRADIENTGAPVALUE = this.SCENEGRADIENTGAPVALUE;
                cloneSceneInfo.SceneGradientGapRedCBChecked = this.SceneGradientGapRedCBChecked;
                cloneSceneInfo.SceneGradientGapGreenCBChecked = this.SceneGradientGapGreenCBChecked;
                cloneSceneInfo.SceneGradientGapBlueCBChecked = this.SceneGradientGapBlueCBChecked;*/
                cloneSceneInfo.SceneCurClickColorImgOnOff = this.SceneCurClickColorImgOnOff.clone();  //深拷贝
                cloneSceneInfo.SceneDatasHead = this.SceneDatasHead.clone();
                cloneSceneInfo.SceneDefaultColor = this.SceneDefaultColor.clone();
                cloneSceneInfo.SceneGradientRampStopGap = this.SceneGradientRampStopGap.clone();
                cloneSceneInfo.SceneGradientRampGradientGap = this.SceneGradientRampGradientGap.clone();
            } catch (CloneNotSupportedException e){
                e.printStackTrace();
            }
            return cloneSceneInfo;
        }
    }

    /**
     * 保存情景模式
     * @param sp
     */
    public void saveSceneListInfo(SharedPreferences sp, SceneListInfo sceneListInfo) {
        Gson gson = new Gson();
        String json = gson.toJson(sceneListInfo);
        LogUtil.info("SceneListInfo", "要保存的sceneListInfo数据是：" + json);
        sp.edit().putString("SceneListInfo", json).commit();
    }

    /**
     * 读取情景模式
     * @param sp
     * @return
     */
    public static SceneListInfo getSceneListInfo(SharedPreferences sp) {
        String json = sp.getString("SceneListInfo", "");
        LogUtil.info("SceneListInfo", "json = " + json);
        if(TextUtils.isEmpty(json)) {
            return new SceneListInfo();
        }
        LogUtil.info("SceneListInfo", "json = " + json);
        Gson gson = new Gson();
        SceneListInfo sceneListInfo = gson.fromJson(json, SceneListInfo.class);
        LogUtil.info("SceneListInfo", "保存的sceneListInfo信息是：" + sceneListInfo.toString());

        return sceneListInfo;
    }



}
