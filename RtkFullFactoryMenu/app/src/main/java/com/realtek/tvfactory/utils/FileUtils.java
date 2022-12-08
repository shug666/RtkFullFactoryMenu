package com.realtek.tvfactory.utils;

import static android.os.FileUtils.copy;

import android.content.Context;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.system.ErrnoException;
import android.system.Os;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class FileUtils {

    public static final String TAG = "FileUtils";

    private FileUtils() {
    }

    public static File getInternalFile(Context context, File file) {
        if (file == null) {
            return null;
        }

        StorageManager storageManager = context.getSystemService(StorageManager.class);
        StorageVolume volume = storageManager.getStorageVolume(file);
        if (volume == null || TextUtils.isEmpty(volume.getInternalPath())) {
            return file;
        }

        String filePath = file.getPath();

        String path = volume.getPath();
        if (!filePath.startsWith(path)) {
            return file;
        }
        String internalPath = volume.getInternalPath();
        return new File(filePath.replace(path, internalPath));
    }

    public static void copyDirectory(File source, File target) {
        if (source == null || !source.isDirectory()) {
            return;
        }
        if (target == null || !target.isDirectory()) {
            return;
        }
        if (!target.exists()) {
            if (!target.mkdirs()) {
                return;
            }
        }
        File[] children = source.listFiles();
        if (children == null || children.length == 0) {
            return;
        }
        try {
            for (File child : children) {
                File targetFile = new File(target, child.getName());
                copy(child, targetFile);
                targetFile.setWritable(true, false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteDirectory(File directory) {
        if (directory == null || !directory.isDirectory()) {
            return false;
        }
        if (!directory.exists()) {
            return true;
        }
        File[] children = directory.listFiles();
        if (children == null || children.length == 0) {
            return directory.delete();
        }
        for (File child : children) {
            if (!child.delete() && child.isDirectory()) {
                deleteDirectory(child);
            }
        }
        return directory.delete();
    }

    public static void chmodFile(File file, int mode) {
        if (file == null) {
            return;
        }
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children == null || children.length == 0) {
                return;
            }
            try {
                Os.chmod(file.getAbsolutePath(), mode);
            } catch (ErrnoException e) {
                Log.d(TAG, e.getMessage(), e);
            }
            for (File child : children) {
                chmodFile(child, mode);
            }
        } else {
            try {
                Os.chmod(file.getAbsolutePath(), mode);
            } catch (ErrnoException e) {
                Log.d(TAG, e.getMessage(), e);
            }
        }
    }

    public static boolean allFilesExist(File file, String... fileNames) {
        if (file == null || !file.exists()) {
            return false;
        }
        if (fileNames == null || fileNames.length == 0) {
            return false;
        }
        for (String name : fileNames) {
            File childFile = new File(file, name);
            if (!childFile.exists()) {
                return false;
            }
        }
        return true;
    }

    public static boolean copyFile (String fromPath, String file, String toPath) {
        if (fromPath == null || file == null || toPath == null){
            return false;
        }
        File FFile = new File(fromPath, file);
        if (!FFile.exists()) {
            return false;
        }
        try {
            File TFile = new File(toPath, file);
            copy(FFile, TFile);
            TFile.setWritable(true, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Get file context
     * @return string
     */
    public static String getFileContext(String fileName) {
        if (fileName == null || !(new File(fileName).exists())) {
            Log.e(TAG, "fileName=" + fileName);
            return "unknown";
        }
        return getFileContext(new File(fileName));
    }

    public static String getFileContext(File fileName) {
        InputStreamReader inputStreamReader = null;
        FileInputStream inputStream = null;
        BufferedReader reader = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            inputStream = new FileInputStream(fileName);
            inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            reader = new BufferedReader(inputStreamReader);
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                return stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Error:" + e);
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            Log.e(TAG, "Error:" + e1);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Error:" + e);
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Error:" + e);
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Error:" + e);
                }
            }
        }
        return "unknown";
    }
}
