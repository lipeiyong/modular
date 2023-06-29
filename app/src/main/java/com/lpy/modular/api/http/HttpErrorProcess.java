package com.lpy.modular.api.http;

import android.app.Application;
import android.util.SparseArray;
import android.widget.Toast;

import com.lpy.common.api.Resource;
import com.lpy.common.util.AppExecutors;

import java.util.concurrent.Executor;

//import javax.inject.Inject;
//import javax.inject.Singleton;

/**
 * @author lipeiyong
 */
//@Singleton
public class HttpErrorProcess {

    private final Executor appExecutors;
    private final Application application;
    private SparseArray<Runnable> runnableSparseArray;

//    @Inject
    HttpErrorProcess(AppExecutors appExecutors, Application application) {
        this.appExecutors = appExecutors.mainThread();
        this.application = application;
    }

    public HttpErrorProcess(Executor appExecutors, Application context) {
        this.appExecutors = appExecutors;
        this.application = context;
    }

    public HttpErrorProcess register(int code, Runnable runnable) {
        if (null == runnableSparseArray) {
            runnableSparseArray = new SparseArray<>(1);
        }
        runnableSparseArray.append(code, runnable);
        return this;
    }

    public void process(int code, String message) {
        Runnable runnable = null;
        if (runnableSparseArray != null) {
            runnable = runnableSparseArray.get(code);
            runnableSparseArray.delete(code);
        }
        if (runnable == null) {
            String result = getErrorMsg(code, message);
            if (result != null) {
                runnable = () -> Toast.makeText(application, result, Toast.LENGTH_SHORT).show();
            }
        }
        appExecutors.execute(runnable);
    }

    public String getErrorMsg(Resource resource) {
        return getErrorMsg(resource.errorCode, resource.errorMessage);
    }

    /**
     * 错误信息
     *
     * @param code
     * @param message
     * @return
     */
    public String getErrorMsg(int code, String message) {
        String errorMsg;
        switch (code) {
            case 0:
                return null;
            case 400://参数错误
                errorMsg = "服务器异常(400)";
                break;
            case 404:
                errorMsg = "服务器异常(404)";
                break;
            case 500:
                errorMsg = "服务器异常(500)";
                break;
            case 5001:
                errorMsg = "服务器错误(5001)";
                break;
            case 5002:
                errorMsg = "服务器异常(5002)";
                break;
            case 5003:
                errorMsg = "服务器异常(5003)";
                break;
            case 5004:
                errorMsg = "操作频繁(5004)";
                break;
            case 4001:
                errorMsg = "参数错误（参数为空或格式不正确 4001）";
                break;
            case 4004:
                errorMsg = "请绑定孩子(4004)";
                break;
            case 4009:
                errorMsg = "未找到家长用户(4009)";
                break;
            case 4012:
                errorMsg = "没有删除权限(4012)";
                break;
            case 4014:
                errorMsg = "不在学校内(4014)";
                break;
            case 4018:
                errorMsg = "未获取数据(4018)";
                break;
            case 4019:
                errorMsg = "没有访问权限(4019)";
                break;
            case 4020:
                errorMsg = "手表不在线(4020)";
                break;
            case 999:
                errorMsg = "无法连接到服务器，请检查网络";
                break;
            default:
                errorMsg = "未知错误(" + code + ") " + message;
                break;
        }
        return errorMsg;
    }

    private void hint(String message) {
        Toast.makeText(application, message, Toast.LENGTH_SHORT).show();
    }

    public void process(Resource resource) {
        process(resource.errorCode, resource.errorMessage);
    }
}
