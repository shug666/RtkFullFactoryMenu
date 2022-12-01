package com.realtek.fullfactorymenu.utils;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.storage.DiskInfo;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class Tools {
	private static final String TAG = "Tools";
	private static Toast toast = null;
	
	public static boolean focusUp(ViewGroup group, boolean loop) {
		int count = group == null ? 0 : group.getChildCount();
		if (count == 0) {
			return false;
		}
		View focus = group.getFocusedChild();
		if (focus == null || count == 1) {
			return false;
		}
		int current = group.indexOfChild(focus);
		if (!loop && current == 0) {
			return false;
		}
		int last = (loop && current == 0) ? count - 1 : current - 1;
		View child = null;
		for (int i = last; i != current; i = (loop && i == 0) ? count - 1 : i - 1) {
			child = group.getChildAt(i);
			if (child.isEnabled() && child.isShown() && child.isFocusable()) {
				return child.requestFocus();
			}
		}
		return false;
	}

	public static boolean focusDown(ViewGroup group, boolean loop) {
		int count = group == null ? 0 : group.getChildCount();
		if (count == 0) {
			return false;
		}
		View focus = group.getFocusedChild();
		if (focus == null || count == 1) {
			return false;
		}
		int current = group.indexOfChild(focus);
		if (!loop && current == count - 1) {
			return false;
		}
		int next = (loop && current == count - 1) ? 0 : current + 1;
		View child = null;
		for (int i = next; i != current; i = (loop && i == count - 1) ? 0 : i + 1) {
			child = group.getChildAt(i);
			if (child.isEnabled() && child.isShown() && child.isFocusable()) {
				return child.requestFocus();
			}
		}
		return false;
	}
	
	public static int getIndex(String[] arr, String cont) {
		int index = -1;
		if (cont == null || "".equals(cont)) {
			return index;
		}
		for (int i = 0; i < arr.length; i++) {
			if (cont.equals(arr[i])) {
				index = i;
			}
		}
		return index;
	}
	
//	public static void runShellCmd(String cmdstring) {
//        try {
//            Process proc = Runtime.getRuntime().exec(cmdstring);
//            String output;
//            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(proc.getInputStream()));
//            while ((output = bufferedReader.readLine()) != null) {
//                Log.d(TAG,"[" + cmdstring + "], output:" + output);
//            }
//            bufferedReader.close();
//            proc.waitFor();
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
	
	public static String getFisrtUsbStroagePath(Context context) {
		List<String> usbPaths = getUsbPaths(context);
		if (usbPaths == null || usbPaths.size() == 0) {
		    return null;
		} else {
		    return usbPaths.get(0);
		}
	}
	
	public static List<String> getUsbPaths(Context context) {
        StorageManager manager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        List<VolumeInfo> list = manager.getVolumes();
        List<String> usbs = new ArrayList<String>();
        for (VolumeInfo volumeInfo : list) {
            if (volumeInfo.getType() == 0) {
                DiskInfo diskInfo = volumeInfo.getDisk();
                if (diskInfo != null) {
                    int stat = volumeInfo.getState();
                    if (stat == VolumeInfo.STATE_MOUNTED) {// i == 2 : mounted ,i == 5 :unmounted.
                        String path = volumeInfo.getPath().getPath();
                        Log.d(TAG, "path : " + path);
                        usbs.add(path);
                        return usbs;
                    }
                }
            }
        }
        return null;
    }
	
	public static void showDialog(Context context ,String title, String message) {
		AlertDialog dialog = new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
				.setTitle(title).setMessage(message).setCancelable(true).setPositiveButton("OK", null).create();
		dialog.show();
		dialog.getButton(DialogInterface.BUTTON_POSITIVE).requestFocus();
	}
	
	public static void showToast(Context context ,String message) {
	    if (toast == null) {
	        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
	    } else {
	        toast.setText(message);
	    }
	    toast.show();
	}

}
