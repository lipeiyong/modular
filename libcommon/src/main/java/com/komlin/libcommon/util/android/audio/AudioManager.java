package com.komlin.libcommon.util.android.audio;

import android.app.Application;
import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import javax.inject.Inject;

/**
 * 录音管理器
 *
 * @author lipeiyong
 * @date 2019/9/25
 */
public class AudioManager {
    private static final String PATH_VOICE = "/KomlinMsg/voice";

    Application context;

    public AudioStateListener mListener;
    private MediaRecorder mMediaRecorder;
    private String mDir;
    private String mCurrentFilePath;
    private boolean isPrepared;

    @Inject
    public AudioManager(Application application) {
        this.context = application;
        mDir = Objects.requireNonNull(application.getExternalFilesDir(PATH_VOICE)).getAbsolutePath();
    }

    public void setOnAudioStateListener(AudioStateListener listener) {
        mListener = listener;
    }

    /**
     * 准备
     */
    public void prepareAudio() {
        try {
            isPrepared = false;
            File dir = new File(mDir);
            if (!dir.exists()) {
                dir.mkdir();
            }
            String fileName = generateFileName();

            File file = new File(dir, fileName);

            mCurrentFilePath = file.getAbsolutePath();

            mMediaRecorder = new MediaRecorder();
            //设置输出文件
            mMediaRecorder.setOutputFile(file.getAbsolutePath());
            //设置MediaRecorder的音频源为麦克风
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //设置音频格式
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
            //设置音频的格式为amr
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            //准备结束
            isPrepared = true;
            if (mListener != null) {
                mListener.wellPrepared();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成UUID唯一标示符
     * 算法的核心思想是结合机器的网卡、当地时间、一个随即数来生成GUID
     * .amr音频文件
     */
    private String generateFileName() {
        return UUID.randomUUID().toString() + ".amr";
    }

    public int getVoiceLevel(int maxLevel) {
        if (isPrepared) {
            //获得最大的振幅getMaxAmplitude() 1-32767
            try {
                return maxLevel * mMediaRecorder.getMaxAmplitude() / 32768 + 1;
            } catch (Exception e) {

            }
        }
        return 1;
    }

    public void release() {
        mMediaRecorder.stop();
        mMediaRecorder.release();
        mMediaRecorder = null;
    }

    public void cancel() {
        release();
        if (mCurrentFilePath != null) {
            File file = new File(mCurrentFilePath);
            file.delete();
            mCurrentFilePath = null;
        }
    }

    public String getCurrentFilePath() {
        return mCurrentFilePath;
    }

    /**
     * 回调准备完毕
     */
    public interface AudioStateListener {
        void wellPrepared();
    }
}
