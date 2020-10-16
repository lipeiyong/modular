package com.komlin.libcommon.util.log;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.komlin.libcommon.util.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import timber.log.Timber;

/**
 * @author lipeiyong
 * @date 2019/6/4 18:50
 */
public class FileLoggingTree extends Timber.Tree {

    public static final String TAG = "GPS";

    private FileOutputStream fileStream;

    private Context context;
    private SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");

    public FileLoggingTree(Context context) {
        this.context = context;
    }

    /**
     * 使用Timber自定义吧log信息存储到文件中
     * 其中String CacheDiaPath = context.getCacheDir().toString();
     */
    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        Log.i("Watch-" + tag, message);
        if (TextUtils.isEmpty(Constants.SD_ROOT_PATH)) {
            return;
        }

        if (!"ceshi".equals(tag)) {
            return;
        }

        File file = new File(context.getFilesDir(), "gps.txt");
        try {
            if (!file.exists()) {
                if (file.createNewFile()) {
                    Log.v("日志存储路径", "file.path:" + file.getAbsolutePath() + ",message:[" + message + "]");
                }
            }
            if (fileStream == null) {
                fileStream = new FileOutputStream(file);
            }
            saveMessage(message + "|time [" + sdf.format(new Date()) + "]");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveMessage(String message) {
        if (null == fileStream) {
            return;
        }
        byte[] bytes = message.getBytes();
        try {
            fileStream.write(bytes);
            fileStream.write('\n');
            fileStream.flush();
        } catch (IOException e) {
            try {
                fileStream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
