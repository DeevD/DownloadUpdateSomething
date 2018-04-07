package com.heinhtet.deevd.updateapk;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Hein Htet on 4/6/18.
 */
public class ProgressDialog extends AlertDialog {

    public ProgressDialog(@NonNull Context context) {
        super(context);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_pg_dialog);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
    }

}
