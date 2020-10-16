package com.komlin.libcommon.util.android.audio;

import android.media.MediaPlayer;

import java.io.IOException;
import android.media.AudioManager;

/**
 * 播放录音管理类
 *
 * @author lipeiyong
 * @date 2019/9/25
 */
public class MediaManager {

    private static MediaPlayer mMediaPlayer;

    private static boolean isPause;

    //播放录音
    public static void playSound(String filePath, MediaPlayer.OnCompletionListener onCompletionListener) {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            //播放错误 防止崩溃
            mMediaPlayer.setOnErrorListener((mp, what, extra) -> {
                mMediaPlayer.reset();
                return false;
            });
        } else {
            mMediaPlayer.reset();
        }
        try {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(onCompletionListener);
            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.prepare();
            mMediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 如果 播放时间过长,如30秒
     * 用户突然来电话了,则需要暂停
     */
    public static void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            isPause = true;
        }
    }

    /**
     * 播放
     */
    public static void resume() {
        if (mMediaPlayer != null && isPause) {
            mMediaPlayer.start();
            isPause = false;
        }
    }

    /**
     * activity 被销毁  释放
     */
    public static void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
