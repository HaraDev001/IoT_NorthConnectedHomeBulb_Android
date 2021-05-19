/*
 * Copyright (C) 2015 tyorikan
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.guohua.north_bulb.service;

import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.guohua.north_bulb.view.VisualizerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class RecordingSampler {

    private static final int RECORDING_SAMPLE_RATE = 44100;

    private AudioRecord mAudioRecord;
    private boolean mIsRecording;
    private int mBufSize;

    private CalculateVolumeListener mVolumeListener;
    private updatevisualizer mUpdatevisualizer;
    private int mSamplingInterval = 200;
    private Timer mTimer;


    public RecordingSampler(Context mContext) {
        initAudioRecord();
    }


    /**
     * setter of CalculateVolumeListener
     *
     * @param volumeListener CalculateVolumeListener
     */
    public void setVolumeListener(CalculateVolumeListener volumeListener) {
        mVolumeListener = volumeListener;
    }

    public void setUpdatevisualizerListener(updatevisualizer updatevisualizer) {
        mUpdatevisualizer = updatevisualizer;
    }

    /**
     * setter of samplingInterval
     *
     * @param samplingInterval interval volume sampling
     */
    public void setSamplingInterval(int samplingInterval) {
        mSamplingInterval = samplingInterval;
    }

    /**
     * getter isRecording
     *
     * @return true:recording, false:not recording
     */
    public boolean isRecording() {
        return mIsRecording;
    }

    private void initAudioRecord() {
        int bufferSize = AudioRecord.getMinBufferSize(
                RECORDING_SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
        );

        mAudioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                RECORDING_SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
        );

        if (mAudioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
            mBufSize = bufferSize;
            Log.e("getState =2= ", "bufferSize " + bufferSize);
            Log.e("getState =1= ", "" + mBufSize);
        }
    }

    /**
     * start AudioRecord.read
     */
    public void startRecording() {
        mTimer = new Timer();
        mAudioRecord.startRecording();
        mIsRecording = true;
        runRecording();
    }

    /**
     * stop AudioRecord.read
     */
    public void stopRecording() {
        mIsRecording = false;
        mTimer.cancel();
    }

    private void runRecording() {

        final byte buf[] = new byte[mBufSize];

        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // stop recording
                if (!mIsRecording) {
                    mAudioRecord.stop();
                    return;
                }

                mAudioRecord.read(buf, 0, mBufSize);

                int decibel = calculateDecibel(buf);

                final byte micBufferData[] = new byte[mBufSize];
                final int bytesPerSample = 2; // As it is 16bit PCM
                final double amplification = 100.0; // choose a number as you like
                for (int index = 0, floatIndex = 0; index < mBufSize - bytesPerSample + 1; index += bytesPerSample, floatIndex++) {
                    double sample = 0;
                    for (int b = 0; b < bytesPerSample; b++) {
                        int v = buf[index + b];
                        if (b < bytesPerSample - 1 || bytesPerSample == 1) {
                            v &= 0xFF;
                        }
                        sample += v << (b * 8);
                    }
                    double sample32 = amplification * (sample / 32768.0);
                    micBufferData[floatIndex] = (byte) sample32;
                }

                Handler mHandler = new Handler(Looper.getMainLooper());

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mUpdatevisualizer != null) {
                            mUpdatevisualizer.updateVisualizer(micBufferData);
                        }
                    }
                });

                // callback for return input value
                if (mVolumeListener != null) {
                    mVolumeListener.onCalculateVolume(decibel);
                }
            }
        }, 0, mSamplingInterval);
    }

    private int calculateDecibel(byte[] buf) {
        int sum = 0;
        for (int i = 0; i < mBufSize; i++) {
            sum += Math.abs(buf[i]);
        }
        // avg 10-50
        return sum / mBufSize;
    }

    /**
     * release member object
     */
    public void release() {
        stopRecording();
        mAudioRecord.release();
        mAudioRecord = null;
        mTimer = null;
    }

    public interface CalculateVolumeListener {

        /**
         * calculate input volume
         *
         * @param volume mic-input volume
         */
        void onCalculateVolume(int volume);
    }

    public interface updatevisualizer {

        /**
         * calculate input volume
         *
         * @param volume mic-input volume
         */
        void updateVisualizer(byte[] volume);
    }

}
