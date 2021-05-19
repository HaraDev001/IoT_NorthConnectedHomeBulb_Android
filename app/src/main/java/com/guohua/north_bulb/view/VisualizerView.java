package com.guohua.north_bulb.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author Leo
 * @detail 音乐频谱 注意：LENGTH 要和 采样点数一样
 * @time 2015-11-17
 */
public class VisualizerView extends View {
    /* 控制有效数据中显示的竖条数和下落速度 */
    private static final int LENGTH = 1024;//要和采样点数一样
    private static final int NUMBER = 64;
    private static final int DOWN_SPEED = 32;
    private static final float STROKE_WIDTH = 27f;
    private static final int ALPHA = 88;

    /* 要画实际和倒影的点 */
    private byte[] mBytes;
    private float[] nowPoints;// 当前位置
    private float[] lastPoints; // 上一次位置
    private float[] reflectionPoints;// 倒影
    /* 画笔 */
    private Paint realPaint = new Paint();
    private Paint reflectionPaint = new Paint();// 倒影
    private static final int MIDDLE_COLOR = Color.rgb(255, 0, 0);
    private static final int STROKE_COLOR = Color.rgb(0, 255, 0);

    public VisualizerView(Context context) {
        super(context);
        init();
    }

    public VisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /* 初始化画笔 */
    private void init() {
        LinearGradient mRadialGradient = new LinearGradient(0, 0, 0, 400,
                new int[]{STROKE_COLOR, MIDDLE_COLOR}, null,
                Shader.TileMode.MIRROR);
        mBytes = null;
        realPaint.setStrokeWidth(STROKE_WIDTH);
        realPaint.setAntiAlias(true);
        realPaint.setShader(mRadialGradient);
        reflectionPaint.setStrokeWidth(STROKE_WIDTH);
        reflectionPaint.setAntiAlias(true);
        reflectionPaint.setShader(mRadialGradient);
        reflectionPaint.setAlpha(ALPHA);
    }

    /* 外部更新接口 */
    public void updateVisualizer(byte[] bytes) {
        mBytes = bytes;
        invalidate();
    }

    /**
     * 画图
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBytes == null || mBytes.length < 3) {
            return;
        }
        byte[] model1 = new byte[LENGTH / 2 + 1];
        model1[0] = (byte) Math.abs(mBytes[1]);
        for (int i = 2, j = 1; i < LENGTH / 4; i += 2, j++) {
            model1[j] = (byte) Math.hypot(mBytes[i], mBytes[i + 1]);
        }

        byte[] model = new byte[LENGTH / 2 + 1];
        model[0] = (byte) Math.abs(model1[1]);
        for (int i = 2, j = 1; i < LENGTH / 2; i += 2, j++) {
            model[j] = (byte) Math.max(model1[i], model1[i + 1]);
            if (j > 0) {
                model[j] = (byte) ((model[j] + model[j - 1]) / 2);
            }
        }

        if (nowPoints == null || nowPoints.length < NUMBER * 4) {
            nowPoints = new float[NUMBER * 4];
            reflectionPoints = new float[NUMBER * 4];
            lastPoints = new float[NUMBER * 4];
        }
        for (int i = 0; i < NUMBER; i++) {
            if (model[i] < 0) {
                model[i] = 127;
            }
            if (model[i] > 127) {
                model[i] = 127;
            }
            float zoom = (float) (14.5 * i / NUMBER + 2.5);
            if (model[i] < 5) {
                zoom = 4;
            }

            nowPoints[i * 4] = getWidth() * i / NUMBER;
            nowPoints[i * 4 + 1] = getHeight() / 5 * 3;
            nowPoints[i * 4 + 2] = getWidth() * i / NUMBER;
            nowPoints[i * 4 + 3] = 1 + getHeight() / 5 * 3 - model[i] * zoom;

            reflectionPoints[i * 4] = nowPoints[i * 4];
            reflectionPoints[i * 4 + 1] = nowPoints[i * 4 + 1] + 5;
            reflectionPoints[i * 4 + 2] = nowPoints[i * 4 + 2];
            reflectionPoints[i * 4 + 3] = nowPoints[i * 4 + 3] + model[i]
                    * zoom * 5 / 3;

            lastPoints[i * 4] = nowPoints[i * 4];
            lastPoints[i * 4 + 1] = nowPoints[i * 4];
            lastPoints[i * 4 + 2] = nowPoints[i * 4];
            if (lastPoints[i * 4 + 3] > 0
                    && nowPoints[i * 4 + 3] > lastPoints[i * 4 + 3]
                    + DOWN_SPEED) {
                nowPoints[i * 4 + 3] = lastPoints[i * 4 + 3] + DOWN_SPEED;
                reflectionPoints[i * 4 + 3] = (getHeight() * 2 - nowPoints[i * 4 + 3])
                        / 2 - DOWN_SPEED / 2;
            }
            lastPoints[i * 4 + 3] = nowPoints[i * 4 + 3];
        }
        canvas.drawLines(nowPoints, realPaint);
        canvas.drawLines(reflectionPoints, reflectionPaint);
    }
}