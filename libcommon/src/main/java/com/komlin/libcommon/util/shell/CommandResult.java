package com.komlin.libcommon.util.shell;

/**
 *
 * @author lipeiyong
 * @date 17-7-14
 */
public class CommandResult {
    /**
     * 0 success
     * otherwise error
     */
    public int result = -1;
    public String errorMsg;
    public String successMsg;

    @Override
    public String toString() {
        return "CommandResult{" +
                "result=" + result +
                ", errorMsg='" + errorMsg + '\'' +
                ", successMsg='" + successMsg + '\'' +
                '}';
    }
}
