package com.lpy.common.custom;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import android.widget.TextView;

import com.lpy.common.R;


/**
 * @author lipeiyong
 * @date on 17-10-25  上午9:22
 */
public class CustomProgressDialog extends AlertDialog {

    public CustomProgressDialog(@NonNull Context context) {
        this(context, R.style.comm_dialog_style);
    }

    public CustomProgressDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    private TextView messageTv;
    private String msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.com_dialog_progress);
        messageTv = findViewById(R.id.messageTv);
        if (null != messageTv) {
            messageTv.setText(msg);
        }
        setCancelable(false);
    }

    @MainThread
    public void setMessage(String msg) {
        this.msg = msg;
        if (null != messageTv) {
            messageTv.setText(msg);
        }
    }
}
