package com.realtek.tvfactory.user;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import android.os.storage.DiskInfo;
import android.os.storage.VolumeInfo;
import java.util.ArrayList;
import java.util.Locale;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.storage.StorageManager;
import android.util.Log;
import android.widget.Toast;

public class LogCatService extends IntentService {

    public LogCatService() {
        super("LogCatServer");
    }

    private static final String TAG = "LogCatService";
    private static final List<Process> mProcessList = Collections.synchronizedList(new ArrayList<Process>());
    public static final int TOAST_CLOSE_WINDOWN = 1;
    public static final int TOAST_START_RECORD = 2;
    public static final int TOAST_CMD_INFO = 3;
    public static final int TOAST_NO_DEVICES = 4;
    public static final int TOAST_NO_FILE = 5;
    public static final int TOAST_LOGCAT_FINISH = 6;
    private static final int PROCESS_CHECK = 546;
    private File mLogfile = null;
    private String logPath = null;
    private static final String KERNEL_PATH = "/mnt/vendor/rtdlog";
    private static final String RECOVERY_PATH = "/cache/recovery";
    private static final String ANR_PATH = "/data/anr";
    private static final String TOMBSTONE_PATH = "/data/tombstones";
    private static final int CHOOSE_LOGCAT = 0;
    private static final int CHOOSE_KERNEL = 1;
    private static final int CHOOSE_RECOVERY = 2;
    private static final int CHOOSE_ANR = 3;
    private static final int CHOOSE_TOMBSTONE = 4;
    private static final int CHOOSE_LOGCAT_KERNEL = 5;
    private static final int CHOOSE_ALL = 6;
    private static Process mProcess = null;
    SimpleDateFormat fileTime = new SimpleDateFormat("_yyyyMMddHHmmss_", Locale.getDefault());

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    private Handler mHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            Log.d(TAG, "mHandle action: " + msg.what + " Thread:" + Thread.currentThread().getName());
            switch (msg.what) {
                case LogCatService.TOAST_CLOSE_WINDOWN:
                    Toast.makeText(getApplicationContext(), "Copy log completed!", Toast.LENGTH_LONG).show();
                    break;
                case LogCatService.TOAST_START_RECORD:
                    Toast.makeText(getBaseContext(), "Writing logs to " + logPath + "... Please do not remove the usb disk!", Toast.LENGTH_LONG).show();
                    this.postDelayed(runnable, 1000);
                    break;
                case LogCatService.TOAST_CMD_INFO:
                    Toast.makeText(getBaseContext(), "Excuting an order...." + msg.obj, Toast.LENGTH_LONG).show();
                    break;
                case LogCatService.TOAST_NO_DEVICES:
                    Toast.makeText(getBaseContext(), "No external storage device detected.", Toast.LENGTH_LONG).show();
                    break;
                case TOAST_NO_FILE:
                    Toast.makeText(getBaseContext(), msg.obj + " is empty!", Toast.LENGTH_LONG).show();
                    break;
                case TOAST_LOGCAT_FINISH:
                    Toast.makeText(getBaseContext(), "Logcat write completed!", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate thread:" + Thread.currentThread().getName());
        StorageManager manager = (StorageManager) getSystemService(STORAGE_SERVICE);
        List<VolumeInfo> list = manager.getVolumes();
        for (VolumeInfo volumeInfo : list) {
            if (volumeInfo.getType() == VolumeInfo.TYPE_PUBLIC) {
                DiskInfo diskInfo = volumeInfo.getDisk();
                if (diskInfo != null) {
                    int i = volumeInfo.getState();
                    if (i == VolumeInfo.STATE_MOUNTED) {// i == 2 : mounted ,i == 5 :unmounted.
                        logPath = volumeInfo.getPath().getPath();
                    }
                }
            }
        }
        if (logPath == null || logPath.length() == 0) {
            mHandle.sendEmptyMessage(TOAST_NO_DEVICES);
            return;
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy thread:" + Thread.currentThread().getName());
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = "";
        int mode = -1;
        if (intent != null && intent.getExtras() != null) {
            action = intent.getExtras().getString("action");
            mode = intent.getExtras().getInt("mode");
        }
        Log.d(TAG, String.format("onHandleIntent thread:%s action:%s mode:%d logPath:%s",Thread.currentThread().getName(), action, mode, logPath));
        if (action.equals("start")) {
            switch (mode) {
                case CHOOSE_LOGCAT:
                case CHOOSE_LOGCAT_KERNEL:
                case CHOOSE_ALL:
                    startWrite();
                    break;
                default:
                    break;
            }
        } else if (action.equals("stop")) {
            switch (mode) {
                case CHOOSE_KERNEL:
                case CHOOSE_RECOVERY:
                case CHOOSE_ANR:
                case CHOOSE_TOMBSTONE:
                case CHOOSE_LOGCAT_KERNEL:
                    CreateFilePath(mode);
                    break;
                case CHOOSE_ALL:
                    CreateFilePath(CHOOSE_KERNEL);
//                    CreateFilePath(CHOOSE_RECOVERY);
                    CreateFilePath(CHOOSE_ANR);
                    CreateFilePath(CHOOSE_TOMBSTONE);
                    break;
                default:
                    break;
            }
            if (mode == CHOOSE_LOGCAT || mode == CHOOSE_LOGCAT_KERNEL || mode == CHOOSE_ALL) {
                if (mProcess != null) {
                    mHandle.postDelayed(() -> {
                        mProcess.destroy();
                        Message msg = new Message();
                        msg.what = TOAST_LOGCAT_FINISH;
                        if (logPath != null && logPath.length() > 0) {
                            mHandle.sendMessage(msg);
                        }
                    }, 1000);
                }
            }
        }
    }

    private void startWrite() {
        try {
            SimpleDateFormat sDateFormat = new SimpleDateFormat("_yyyyMMddHHmmss_");
            String date = sDateFormat.format(new java.util.Date());
            mLogfile = new File(logPath + "/log" + date + "[logcat]" + ".txt");
            if (!mLogfile.exists()) {
                Log.d(TAG, String.format("file:%s create:%b",mLogfile.getPath(), mLogfile.createNewFile()));
            }
            String cmd = String.format("logcat -f %s",mLogfile.getPath());
            Log.d(TAG, String.format("run cmd is:%s", cmd));
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("logcat -G 5M");
            mProcess = runtime.exec(cmd);
            mHandle.sendEmptyMessage(TOAST_START_RECORD);
            Log.d(TAG, String.format("isAlive:%b",mProcess != null && mProcess.isAlive()));
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "***********error************" + e.getMessage());
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            long siez = 0;
            if (mLogfile != null) {
                siez = mLogfile.length();
            }
            Log.d(TAG, String.format("LogAlive:%b size:%d",mProcess != null && mProcess.isAlive(), siez));
            if (mProcess.isAlive()) {
                mHandle.postDelayed(this, 1000);
            }
        }
    };

    private boolean isNeedCopy(String dir) {
        File file_dir = new File(dir);
        if (file_dir.exists() && file_dir.isDirectory()) {
            File[] files = file_dir.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    if (file.canRead()) {
                        return true;
                    }
                }
            }
        }
        Message message = new Message();
        message.what = TOAST_NO_FILE;
        message.obj = dir;
        mHandle.sendMessage(message);
        return false;
    }

    private void copyAllFiles(String source, String target) {
        File kernel_dir = new File(target);
        if (!kernel_dir.exists()) {
            boolean ret = kernel_dir.mkdir();
            Log.d(TAG, String.format("create dir:%s is %b",target , ret));
        }
        String cpCmd = String.format("cp -rf %s/* %s", source, target);
        String[] cmd = {"sh", "-c", cpCmd};
        Log.d(TAG, String.format("copyKernel cmd:%s", cpCmd));
        try {
            Process exec = Runtime.getRuntime().exec(cmd);
            mProcessList.add(exec);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mHandle.removeMessages(PROCESS_CHECK);
        mHandle.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Start check process alive " + mProcessList.size());
                int size = mProcessList.size();
                for (Process next : mProcessList) {
                    if (!next.isAlive()) {
                        size--;
                    }
                }
                if (size == 0) {
                    mProcessList.clear();
                    mHandle.sendEmptyMessage(TOAST_CLOSE_WINDOWN);
                    Log.d(TAG, "is cp compile");
                } else {
                    mHandle.postDelayed(this, 1000);
                }
            }
        }, PROCESS_CHECK, 1000);
    }

    private void CreateFilePath(int mode) {
        try {
            switch (mode) {
                case CHOOSE_KERNEL:
                case CHOOSE_LOGCAT_KERNEL:
                    if (isNeedCopy(KERNEL_PATH)) {
                        String targetPath = String.format("%s/log%s[%s]", logPath, fileTime.format(new Date()), "Kernel");
                        copyAllFiles(KERNEL_PATH, targetPath);
                    }
                    break;
                case CHOOSE_RECOVERY:
                    if (isNeedCopy(RECOVERY_PATH)) {
                        String targetPath = String.format("%s/log%s[%s]", logPath, fileTime.format(new Date()), "Recovery");
                        copyAllFiles(RECOVERY_PATH, targetPath);
                    }
                    break;
                case CHOOSE_ANR:
                    if (isNeedCopy(ANR_PATH)) {
                        String targetPath = String.format("%s/log%s[%s]", logPath, fileTime.format(new Date()), "ANR");
                        copyAllFiles(ANR_PATH, targetPath);
                    }
                    break;
                case CHOOSE_TOMBSTONE:
                    if (isNeedCopy(TOMBSTONE_PATH)) {
                        String targetPath = String.format("%s/log%s[%s]", logPath, fileTime.format(new Date()), "TOMBSTONE");
                        copyAllFiles(TOMBSTONE_PATH, targetPath);
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception -->" + e);
        }
    }
}
