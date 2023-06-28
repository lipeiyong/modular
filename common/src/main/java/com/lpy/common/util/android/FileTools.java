package com.lpy.common.util.android;

import android.os.Environment;
import android.os.StatFs;
import androidx.annotation.IntDef;

import com.lpy.common.util.Constants;
import com.lpy.common.util.NumberTools;
import com.lpy.common.util.encryp.BigFileMD5;
import com.lpy.common.util.shell.CommandResult;
import com.lpy.common.util.shell.ShellCommand;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author lipeiyong
 * @date 17-7-4
 */
public class FileTools {

    public static final int TYPE_TEMP_IMAGE = 1;
    public static final int TYPE_PHOTO = 2;
    public static final int TYPE_VIDEO = 3;
    public static final int TYPE_THUMB = 4;
    public static final int TYPE_LOG = 5;
    public static final int TYPE_APK = 6;
    public static final int TYPE_MODEL = 7;
    public static final int TYPE_HTTP_MODEL = 8;
    public static final int TYPE_AUDIO = 9;
    private static final String ROOT_PATH = Constants.STORAGE_ROOT_PATH;
    private static final String VIDEO_DIR = "/storage/emulated/legacy/Movies/";
    private static final String PHOTO_DIR = "/storage/emulated/legacy/DCIM/";
    private static final String IMAGE_DIR = "/storage/emulated/legacy/Pictures/";
    private static final String THUMB_DIR = "/storage/emulated/legacy/thumb/";
    private static final String LOG_DIR = "/storage/emulated/legacy/crash/";
    private static final String APK_DIR = "/storage/emulated/legacy/apk/";
    private static final String MODEL_DIR = "/storage/emulated/legacy/model/";
    private static final String HTTP_MODEL_DIR = "/storage/emulated/legacy/httpModel/";
    private static final String AUDIO_DIR = "/storage/emulated/legacy/audio/";
//    private static final String AUDIO_DIR = "/data/data/com.lpy.watch/files/";

    public static String getFileDirPath(@type int type) {
        switch (type) {
            case TYPE_TEMP_IMAGE:
                return IMAGE_DIR;
            case TYPE_PHOTO:
                return PHOTO_DIR;
            case TYPE_VIDEO:
                return VIDEO_DIR;
            case TYPE_THUMB:
                return THUMB_DIR;
            case TYPE_MODEL:
                return MODEL_DIR;
            case TYPE_HTTP_MODEL:
                return HTTP_MODEL_DIR;
            case TYPE_LOG:
                return LOG_DIR;
            case TYPE_APK:
                return APK_DIR;
            case TYPE_AUDIO:
                return AUDIO_DIR;
            default:
                return ROOT_PATH;
        }
    }

    public static File getFileDir(@type int type) {
        File parent;
        switch (type) {
            case TYPE_TEMP_IMAGE:
                parent = new File(IMAGE_DIR);
                break;
            case TYPE_PHOTO:
                parent = new File(PHOTO_DIR);
                break;
            case TYPE_VIDEO:
                parent = new File(VIDEO_DIR);
                break;
            case TYPE_THUMB:
                parent = new File(THUMB_DIR);
                break;
            case TYPE_MODEL:
                parent = new File(MODEL_DIR);
                break;
            case TYPE_HTTP_MODEL:
                parent = new File(HTTP_MODEL_DIR);
                break;
            case TYPE_LOG:
                parent = new File(LOG_DIR);
                break;
            case TYPE_APK:
                parent = new File(APK_DIR);
                break;
            case TYPE_AUDIO:
                parent = new File(AUDIO_DIR);
                break;
            default:
                parent = new File(ROOT_PATH);
                break;
        }
        boolean exists = parent.exists();
        boolean isDirectory = parent.isDirectory();
        if (exists && !isDirectory) {
            parent.delete();
        }
        if (!exists) {
            boolean success = parent.mkdirs();
            if (!success) {
                parent = new File(ROOT_PATH);
            }
        }
        return parent;
    }

    /**
     * 检查存储空间是否大于500MB并且图片和视频暂用空间小于1G
     * <p>
     * 以后如果需要知道详细的每个文件夹的大小可以使用shell命令:du -ks 'fileDirPath'
     *
     * @return true 够用
     */
    public static boolean isStorageEnough() {
        int photoSize = FileTools.getDirSize(FileTools.getFileDirPath(FileTools.TYPE_PHOTO));
        int videoSize = FileTools.getDirSize(FileTools.getFileDirPath(FileTools.TYPE_VIDEO));
        int remainSize = 1024 - photoSize - videoSize;
        return getRemainSize() > 500 && remainSize > 0;
    }

    /**
     * 获取剩余空间
     *
     * @return 单位MB
     */
    @SuppressWarnings("deprecation")
    public static int getRemainSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        long blockSize = sf.getBlockSize();
        long freeBlocks = sf.getAvailableBlocks();
        return (int) ((freeBlocks * blockSize) / 1024 / 1024);
    }

    /**
     * @return 单位 MB
     */
    public static int getDirSize(String path) {
        CommandResult exec = ShellCommand.exec("du -ks " + path, true);
        if (exec.result == 0) {
            return NumberTools.tryFindInt(exec.successMsg, 0) / 1024;
        } else {
            return 0;
        }
    }

    public static File createFile(int type, String name) {
        File parent = getFileDir(type);
        File result = new File(parent, name);
        if (!result.exists()) {
            try {
                boolean newFile = result.createNewFile();
                if (newFile) {
                    return result;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 校验md5
     */
    public static boolean checkMd5(File file, String md5) {
        String newMd5 = BigFileMD5.getMD5(file);
        assert newMd5 != null;
        return newMd5.equalsIgnoreCase(md5);
    }

    public static boolean isSDExists() {
        File file = new File(Constants.SD_ROOT_PATH);
        return file.exists() && file.canRead();
    }

    public static boolean isUSBExists() {
        File file = new File(Constants.USB_ROOT_PATH);
        return file.exists() && file.canRead();
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TYPE_TEMP_IMAGE, TYPE_PHOTO, TYPE_VIDEO, TYPE_THUMB, TYPE_LOG, TYPE_APK, TYPE_MODEL, TYPE_HTTP_MODEL, TYPE_AUDIO})
    @interface type {

    }

}
