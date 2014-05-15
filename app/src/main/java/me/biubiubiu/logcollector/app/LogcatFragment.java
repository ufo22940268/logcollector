package me.biubiubiu.logcollector.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.biubiubiu.logcollector.app.ui.LogView;
import me.biubiubiu.logcollector.app.util.SystemManager;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class LogcatFragment extends Fragment implements View.OnClickListener {

    public static final String SDCARD_LOG = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "a.log";
    @InjectView(R.id.toggle)
    TextView mToggle;
    @InjectView(R.id.view_log)
    Button mViewLog;
    @InjectView(R.id.log_view)
    LogView mLogView;
    private boolean mRecording;
    private Process mLogcatProcess;
    private Activity mAct;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.logcat_fragment, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mToggle.setOnClickListener(this);

        mAct = getActivity();
        String apkRoot = "chmod 777 " + mAct.getPackageCodePath();
        SystemManager.RootCommand(apkRoot);
    }


    public void onToggle(View view) {

        if (!mRecording) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        startRecord();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            stopRecord();
            mViewLog.setVisibility(View.VISIBLE);
        }

        mRecording = !mRecording;
        mToggle.setText(mRecording ? "停止" : "录制");
    }

    public void onViewLog(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.fromFile(new File(SDCARD_LOG));
        System.out.println("data = " + data);
        intent.setDataAndType(data, "text/plain");
        startActivity(intent);
    }

    private void startRecord() throws IOException {
        Process process = new ProcessBuilder()
                .command("logcat")
                .redirectErrorStream(true)
                .start();
        try {
            InputStream inputStream = process.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            FileOutputStream fileOutputStream = new FileOutputStream(SDCARD_LOG);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                bufferedWriter.write(line);
                final String finalLine = line;
                mAct.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLogView.appendLine(finalLine);
                    }
                });
            }

            inputStream.close();
            fileOutputStream.close();

        } finally {
            process.destroy();
        }
    }

    private void stopRecord() {
        if (mLogcatProcess != null) {
            mLogcatProcess.destroy();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toggle:
                onToggle(v);
                break;
            case R.id.view_log:
                onViewLog(v);
                break;
        }
    }
}
