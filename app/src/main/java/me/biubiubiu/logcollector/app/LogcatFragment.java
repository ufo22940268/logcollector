package me.biubiubiu.logcollector.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

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
import me.biubiubiu.logcollector.app.util.AppConstants;
import me.biubiubiu.logcollector.app.util.SystemManager;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class LogcatFragment extends MenuFragment {

    @InjectView(R.id.log_view)
    LogView mLogView;
    private boolean mRecording;
    private Process mLogcatProcess;
    private Activity mAct;
    private Thread mThread;

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

        mAct = getActivity();
        SystemManager.root(mAct);

        if (!mRecording) {
            mLogView.clean();
            mThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        startRecord();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            mThread.start();
        }
    }

    @Override
    public void onMenuItemSelected(int featureId, MenuItem item) {
        super.onMenuItemSelected(featureId, item);
        if (item.getItemId() == R.id.action_share) {
            onShare(item);
        }
    }

    public void onToggle(MenuItem item) {
        if (!mRecording) {
            mLogView.clean();
            mThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        startRecord();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            mThread.start();
        } else {
            stopRecord();
            getActivity().sendBroadcast(new Intent(AppConstants.ACTION_LOGCAT_STOPPED));
        }

        mRecording = !mRecording;
        item.setIcon(mRecording ? R.drawable.ic_action_stop : R.drawable.ic_action_play);
    }

    public void onViewLog(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.fromFile(new File(AppConstants.SDCARD_LOG));
        System.out.println("data = " + data);
        intent.setDataAndType(data, "text/plain");
        startActivity(intent);
    }

    @Override
    public int getMenu() {
        return R.menu.main;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action buttons
        switch (item.getItemId()) {
            case R.id.action_play:
                onToggle(item);
                return true;
            case R.id.action_share:
                onShare(item);
                return true;
            default:
                return getActivity().onOptionsItemSelected(item);
        }
    }

    private void startRecord() throws IOException {
        Process process = new ProcessBuilder()
                .command("logcat")
                .redirectErrorStream(true)
                .start();
        try {
            InputStream inputStream = process.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            FileOutputStream fileOutputStream = new FileOutputStream(AppConstants.SDCARD_LOG);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                bufferedWriter.write(line + "\n");
                final String finalLine = line;
                mAct.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLogView.appendLines(finalLine);
                    }
                });
                bufferedWriter.flush();
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

    public void onShare(MenuItem item) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(AppConstants.SDCARD_LOG)));
        startActivity(intent);
    }
}
