package com.lpy.common.util.shell;


import androidx.annotation.WorkerThread;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import timber.log.Timber;

/**
 * @author lipeiyong
 * @date 17-7-11
 */
public class ShellCommand {

    private final static String COMMAND_SU = "su";
    private final static String COMMAND_SH = "sh";
    private final static String COMMAND_EXIT = "exit\n";
    private final static String COMMAND_LINE_END = "\n";


    @WorkerThread
    public static void exec(String... commands) {
        try {
            Process process = Runtime.getRuntime().exec(commands);
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 执行命令—单条
     */
    @WorkerThread
    public static CommandResult exec(String command, boolean isRoot) {
        String[] commands = {command};
        return exec(commands, isRoot);
    }

    /**
     * 执行命令-多条
     */
    @WorkerThread
    public static CommandResult exec(String[] commands, boolean isRoot) {
        CommandResult commandResult = new CommandResult();
        if (commands == null || commands.length == 0) {
            return commandResult;
        }
        Process process = null;
        DataOutputStream os = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg;
        StringBuilder errorMsg;
        try {
            process = Runtime.getRuntime().exec(isRoot ? COMMAND_SU : COMMAND_SH);
            os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command != null) {
                    os.write(command.getBytes());
                    os.writeBytes(COMMAND_LINE_END);
                    os.flush();
                }
            }
            os.writeBytes(COMMAND_EXIT);
            os.flush();
            commandResult.result = process.waitFor();
            //获取错误信息
            successMsg = new StringBuilder();
            errorMsg = new StringBuilder();
            successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;
            while ((s = successResult.readLine()) != null) {
                successMsg.append(s);
            }
            while ((s = errorResult.readLine()) != null) {
                errorMsg.append(s);
            }
            commandResult.successMsg = successMsg.toString();
            commandResult.errorMsg = errorMsg.toString();
        } catch (Exception e) {
            Timber.w(e);
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (IOException e) {
                Timber.w(e);
            }
            if (process != null) {
                process.destroy();
            }
        }
        return commandResult;
    }
}
