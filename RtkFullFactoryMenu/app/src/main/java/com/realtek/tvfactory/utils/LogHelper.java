package com.realtek.tvfactory.utils;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

import android.util.Log;

public class LogHelper {

    private LogHelper() {
    }

    public static void v(String tag, String msg) {
        Log.v(tag, (msg == null ? "null" : msg));
    }

    public static void v(String tag, String msg, Object... args) {
        v(tag, String.format(Locale.ROOT, msg, args));
    }

    public static void v(String tag, String msg, Throwable tr) {
        v(tag, String.format(Locale.ROOT, "%s\r\n%s", msg, Log.getStackTraceString(tr)));
    }

    public static void d(String tag, String msg) {
        Log.d(tag, (msg == null ? "null" : msg));
    }

    public static void d(String tag, String msg, Object... args) {
        d(tag, String.format(Locale.ROOT, msg, args));
    }

    public static void d(String tag, String msg, Throwable tr) {
        d(tag, String.format(Locale.ROOT, "%s\r\n%s", msg, Log.getStackTraceString(tr)));
    }

    public static void i(String tag, String msg) {
        Log.i(tag, "\033[36m".concat(msg == null ? "null" : msg).concat("\033[0m"));
    }

    public static void i(String tag, String msg, Object... args) {
        i(tag, String.format(Locale.ROOT, msg, args));
    }

    public static void i(String tag, String msg, Throwable tr) {
        i(tag, String.format(Locale.ROOT, "%s\r\n%s", msg, Log.getStackTraceString(tr)));
    }

    public static void w(String tag, String msg) {
        Log.w(tag, "\033[33m".concat(msg == null ? "null" : msg).concat("\033[0m"));
    }

    public static void w(String tag, String msg, Object... args) {
        w(tag, String.format(Locale.ROOT, msg, args));
    }

    public static void w(String tag, String msg, Throwable tr) {
        w(tag, String.format(Locale.ROOT, "%s\r\n%s", msg, Log.getStackTraceString(tr)));
    }

    public static void e(String tag, String msg) {
        Log.e(tag, "\033[31m".concat(msg == null ? "null" : msg).concat("\033[0m"));
    }

    public static void e(String tag, String msg, Object... args) {
        e(tag, String.format(Locale.ROOT, msg, args));
    }

    public static void e(String tag, String msg, Throwable tr) {
        e(tag, String.format(Locale.ROOT, "%s\r\n%s", msg, Log.getStackTraceString(tr)));
    }

    public static void write(File file, String msg) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(file, true);
            writer.write(msg.concat("\r\n"));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(writer);
        }
    }

    public static void write(File file, String msg, Object... args) {
        write(file, String.format(Locale.ROOT, msg, args));
    }

    public static void write(File file, String msg, Throwable tr) {
        write(file, "".concat(msg).concat("\r\n").concat(Log.getStackTraceString(tr)));
    }

    public static void close(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (IOException e) {
        }
    }

}
