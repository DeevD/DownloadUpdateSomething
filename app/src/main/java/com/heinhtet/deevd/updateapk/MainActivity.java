package com.heinhtet.deevd.updateapk;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {

    Dialog progressDialog;
    DownloadUpdateApkAsync downloadUpdateApkAsync;
    String downloadUrl = "Something Links";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponent();

    }

    private void initComponent() {
        initDialog();
        downloadUpdateApkAsync = new DownloadUpdateApkAsync(this, progressDialog);
        downloadUpdateApkAsync.execute(downloadUrl);
    }

    private void initDialog() {
        progressDialog = new Dialog(this);
        progressDialog.setContentView(R.layout.download_pg_dialog);
        progressDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAni;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        if (downloadUpdateApkAsync.getStatus() == AsyncTask.Status.RUNNING) {
            downloadUpdateApkAsync.cancel(true);
        }
    }
}
