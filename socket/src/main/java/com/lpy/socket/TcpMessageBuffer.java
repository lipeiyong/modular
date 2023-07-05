package com.lpy.socket;


import android.util.Log;

import java.net.Socket;
import java.util.Locale;

/**
 * 用于解决Tcp的粘包问题
 * 以分隔符分割收到的数据
 * <p>
 * 允许单次解析{@link Integer#MAX_VALUE}长度的数据
 * <p>
 * 注意：如果解析后剩余未被解析的长度仍大于{@link TcpMessageBuffer#MAX_SIZE},那么会丢弃全部缓存的数据
 *
 * @author lipeiyong
 * @since 2023/07/05 16:17
 */
public class TcpMessageBuffer {
    private static final String TAG = "TcpMessageBuffer";

    /**
     * 分割符
     */
    private static final byte SUB_CHAR = '@';

    /**
     * 最大缓存区长度,建议和{@link Socket#getReceiveBufferSize()}保持一致
     */
    private static final int MAX_SIZE = 1024 * 8;
    /**
     * 缓存区初始长度
     */
    private static int INIT_SIZE = 1024;

    /**
     * 缓存区
     */
    private byte[] buffer = new byte[INIT_SIZE];
    /**
     * 有效数据起点
     */
    private int point;
    /**
     * 有效数据长度
     */
    private int count;

    public String[] parse(byte[] data) {
        return parse(data, data.length);
    }

    public String[] parse(byte[] buf, int size) {
        //上次结余过长，全部丢弃
        if (count >= MAX_SIZE) {
            Log.e(TAG, String.format("TcpMessageBuffer :: clear all cache ! count = [%d]", count));
            point = 0;
            count = 0;
        }
        //释放临时占用
        if (buffer.length > MAX_SIZE) {
            Log.e(TAG, String.format(Locale.CANADA, "TcpMessageBuffer :: release length from [%d] to [%d]", buffer.length, MAX_SIZE));
            byte[] newBuffer = new byte[MAX_SIZE];
            System.arraycopy(buffer, point, newBuffer, 0, count);
            buffer = newBuffer;
            point = 0;
        }

        int newDataLength = size + count;
        //数据量大于最大值，扩充缓存区(保证缓存区够用)
        if (newDataLength > buffer.length) {
            Log.e(TAG, String.format(Locale.CANADA, "TcpMessageBuffer :: fix length from [%d] to [%d]", buffer.length, newDataLength));
            byte[] newBuffer = new byte[newDataLength];
            System.arraycopy(buffer, point, newBuffer, 0, count);
            buffer = newBuffer;
            point = 0;
        }
        //数据到达尾部，移动缓存区(保证新数据有位置)
        if (newDataLength + point > buffer.length) {
            System.arraycopy(buffer, point, buffer, 0, count);
            point = 0;
        }
        //把新数据加入到缓存区尾部
        System.arraycopy(buf, 0, buffer, point + count, size);
        count += size;
        return check();
    }

    /**
     * 不处理分隔符，存在粘包问题
     *
     * @return
     */
    private String[] check1() {
        String[] strings = new String[1];
        strings[0] = new String(buffer, 0, count);
        point = 0;
        count = 0;
        return strings;
    }

    private String[] check() {
        int start = point;
        int lastCount = 0;
        int subPoint = -1;
        int[][] subBuf = new int[1][3];
        for (int i = 0; i < count; i++) {
            if (buffer[point + i] == SUB_CHAR) {
                int count = i - lastCount;
                subBuf = checkSub(subBuf, ++subPoint);
                subBuf[subPoint][0] = start;
                subBuf[subPoint][1] = start + count;
                subBuf[subPoint][2] = count;
                start = point + i + 1;
                lastCount += (count + 1);
            }
        }
        String[] strings = new String[subPoint + 1];
        for (int i = 0; i <= subPoint; i++) {
            int[] sub = subBuf[i];
            strings[i] = new String(buffer, sub[0], sub[2]);
            point = sub[1] + 1;
            count -= (sub[2] + 1);
        }
        return strings;
    }

    private int[][] checkSub(int[][] subs, int next) {
        if (subs.length < next + 1) {
            int[][] subBuf = new int[next + 2][3];
            System.arraycopy(subs, 0, subBuf, 0, subs.length);
            return subBuf;
        }
        return subs;
    }
}
