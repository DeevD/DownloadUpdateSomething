 package com.heinhtet.deevd.updateapk;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by Hein Htet on 4/6/2018.
 */
public class DownloadUpdateApkAsync extends AsyncTask<String, String, String> {
    private Context mContext;
    private Dialog progressDialog;
    private TextView percentTv, progressNumTv, sizeTv;

    private String TAG = "DownloadUpdateAsyn";
    private String PATH = Environment.getExternalStorageDirectory() + "/Download/";
    private ProgressBar progressBar;
    private String mProgressNumberFormat = "%1d/%2d";
    private NumberFormat mProgressPercentFormat = NumberFormat.getPercentInstance();
    private int lengthOfFile;


    public DownloadUpdateApkAsync(Context context, Dialog dialog) {
        progressDialog = dialog;
        mContext = context;
        initComponent();
    }

    private void initComponent() {
        progressBar = progressDialog.findViewById(R.id.progress_view);
        percentTv = progressDialog.findViewById(R.id.percent_tv);
        progressNumTv = progressDialog.findViewById(R.id.progress_number_tv);
        sizeTv = progressDialog.findViewById(R.id.size_tv);
        if (progressBar != null) {
            progressBar.setMax(100);
            progressBar.setProgress(0);
        }
        mProgressPercentFormat.setMaximumFractionDigits(0);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.show();
    }


    @Override
    protected String doInBackground(String... params) {
        int count = 0;
        try {
            Log.i(TAG, "doInbackground  " + params.toString());

            URL url = new URL(params[0]);

            URLConnection connection = url.openConnection();
            connection.connect();
            lengthOfFile = connection.getContentLength();

            InputStream input = new BufferedInputStream(url.openStream());
            File file = new File(PATH);
            file.mkdirs();
            File outputFile = new File(file, "SomthingFileName");
            if (outputFile.exists()) {
                outputFile.delete();
            }

            OutputStream output = new FileOutputStream(outputFile);
            byte data[] = new byte[1024];
            long total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                publishProgress("" + (int) (total * 100 / lengthOfFile));
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        Log.i(TAG, "onProgressUpdate" + values);
        progressBar.setProgress(Integer.parseInt(values[0]));
        updateProgress(Integer.parseInt(values[0]));
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        progressDialog.dismiss();
        openFile(PATH + "Mahar.apk");
    }

    private void updateProgress(int progress) {
        int max = progressBar.getMax();
        if (mProgressNumberFormat != null) {
            String format = mProgressNumberFormat;
            progressNumTv.setText(String.format(format, progress, max));
        } else {
            progressNumTv.setText("");
        }
        if (mProgressPercentFormat != null) {
            double percent = (double) progress / (double) max;
            SpannableString tmp = new SpannableString(mProgressPercentFormat.format(percent));
            tmp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                    0, tmp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            percentTv.setText(tmp);
        }
        if (lengthOfFile != 0) {
            sizeTv.setText(bytes2String(lengthOfFile));
        }
    }

    private void openFile(String fileName) {
        File toInstall = new File(fileName);
        if (toInstall != null) {
            Uri uri = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(mContext, "com.abccontent.mahartv.fileprovider", toInstall);
            } else {
                uri = Uri.fromFile(toInstall);
            }
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            mContext.startActivity(intent);
        } else {
            Toast.makeText(mContext, "File not exists", Toast.LENGTH_SHORT).show();
        }
    }

    private static double SPACE_KB = 1024;
    private static double SPACE_MB = 1024 * SPACE_KB;
    private static double SPACE_GB = 1024 * SPACE_MB;
    private static double SPACE_TB = 1024 * SPACE_GB;

    private static String bytes2String(long sizeInBytes) {
        NumberFormat nf = new DecimalFormat();
        nf.setMaximumFractionDigits(2);
        try {
            if (sizeInBytes < SPACE_KB) {
                return nf.format(sizeInBytes) + " Byte(s)";
            } else if (sizeInBytes < SPACE_MB) {
                return nf.format(sizeInBytes / SPACE_KB) + " KB";
            } else if (sizeInBytes < SPACE_GB) {
                return nf.format(sizeInBytes / SPACE_MB) + " MB";
            } else if (sizeInBytes < SPACE_TB) {
                return nf.format(sizeInBytes / SPACE_GB) + " GB";
            } else {
                return nf.format(sizeInBytes / SPACE_TB) + " TB";
            }
        } catch (Exception e) {
            return sizeInBytes + " Byte(s)";
        }
    }
}

