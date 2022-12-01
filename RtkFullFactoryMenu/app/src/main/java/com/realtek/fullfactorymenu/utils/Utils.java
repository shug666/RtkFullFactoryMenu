package com.realtek.fullfactorymenu.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.Enumeration;
import java.net.NetworkInterface;
import java.net.InetAddress;
import java.util.Collections;
import java.util.List;
import java.net.SocketException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.XmlResourceParser;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.storage.StorageManager;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class Utils {

    public static final String TAG = "Utils";

    private Utils() {
    }

    public static String resourceNameOf(Context context, int id) {
        Resources resources = context.getResources();
        try {
            String type = resources.getResourceTypeName(id);
            String name = resources.getResourceEntryName(id);
            return String.format(Locale.ROOT, "@%s:%s", type, name);
        } catch (NotFoundException e) {
            return "unknown";
        }
    }

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
            if (!loop && i == 0) {
                return false;
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
            if (!loop && i == count - 1) {
                return false;
            }
        }
        return false;
    }

    public static <T> T previous(ArrayList<T> list, T current, boolean loop, Predicate<T> predicate) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        int currentIndex = list.indexOf(current);
        int count = list.size();
        if (!loop && currentIndex == 0) {
            return current;
        }
        T t = null;
        int previous = (loop && currentIndex == 0) ? count - 1 : currentIndex - 1;
        for (int i = previous; i != currentIndex; i = (loop && i == 0) ? count - 1 : i - 1) {
            t = list.get(i);
            if (predicate == null || predicate.apply(t)) {
                return t;
            }
            if (!loop && i == 0) {
                return current;
            }
        }
        return null;
    }

    public static <T> T next(ArrayList<T> list, T current, boolean loop, Predicate<T> predicate) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        int currentIndex = list.indexOf(current);
        int count = list.size();
        if (!loop && currentIndex == count - 1) {
            return current;
        }
        T t = null;
        int next = (loop && currentIndex == count - 1) ? 0 : currentIndex + 1;
        for (int i = next; i != currentIndex; i = (loop && i == count - 1) ? 0 : i + 1) {
            t = list.get(i);
            if (predicate == null || predicate.apply(t)) {
                return t;
            }
            if (!loop && i == count - 1) {
                return current;
            }
        }
        return null;
    }

    public static <T> T findItemByPredicate(ArrayList<T> list, Predicate<T> predicate) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        if (predicate == null) {
            return null;
        }

        T result = null;
        int count = list.size();
        for (int i = 0; i < count; i++) {
            result = list.get(i);
            if (predicate.apply(result)) {
                return result;
            }
        }
        return null;
    }

    public static <T> int findItemIndexByPredicate(T[] list, Predicate<T> predicate) {
        if (list == null || list.length == 0) {
            return -1;
        }
        if (predicate == null) {
            return -1;
        }

        T result = null;
        int count = list.length;
        for (int i = 0; i < count; i++) {
            result = list[i];
            if (predicate.apply(result)) {
                return i;
            }
        }
        return -1;
    }

    public static final String[] readThisStringArrayXml(Context context, XmlResourceParser parser, String endTag,
            String[] name) throws XmlPullParserException, java.io.IOException {
        String currentBrand = Constants.BRAND;

        parser.next();

        ArrayList<String> items = new ArrayList<String>();

        int eventType = parser.getEventType();

        String condition = null;
        String brand = null;
        String item = null;
        do {
            if (eventType == XmlResourceParser.START_TAG) {
                if ("if".equals(parser.getName())) {
                    condition = parser.getAttributeValue(null, "condition");
                    brand = parser.getAttributeValue(null, "brand");

                    boolean matches = brandMatches(currentBrand, brand);
                    if ("true".equals(condition)) {
                        if (!matches) {
                            XmlUtils.skipCurrentTag(parser);

                            eventType = parser.getEventType();
                            continue;
                        }
                    } else if ("false".equals(condition)) {
                        if (matches) {
                            XmlUtils.skipCurrentTag(parser);

                            eventType = parser.getEventType();
                            continue;
                        }
                    } else {
                        XmlUtils.skipCurrentTag(parser);

                        eventType = parser.getEventType();
                        continue;
                    }

                    eventType = parser.next();
                    continue;
                }

                if ("item".equals(parser.getName())) {
                    try {
                        int resourceId = parser.getAttributeResourceValue(null, "value", 0);
                        if (resourceId == 0) {
                            item = parser.getAttributeValue(null, "value");
                        } else {
                            item = context.getString(resourceId);
                        }
                        items.add(item);
                    } catch (NullPointerException e) {
                        throw new XmlPullParserException("Need value attribute in item");
                    } catch (NumberFormatException e) {
                        throw new XmlPullParserException("Not a number in value attribute in item");
                    }
                } else {
                    throw new XmlPullParserException("Expected item tag at: " + parser.getName());
                }
            } else if (eventType == XmlResourceParser.END_TAG) {
                if (parser.getName().equals(endTag)) {
                    String[] array = new String[items.size()];
                    return (array = items.toArray(array));
                } else if ("if".equals(parser.getName())) {

                } else if ("item".equals(parser.getName())) {

                } else {
                    throw new XmlPullParserException("Expected " + endTag + " end tag at: " + parser.getName());
                }
            }
            eventType = parser.next();
        } while (eventType != XmlResourceParser.END_DOCUMENT);

        throw new XmlPullParserException("Document ended before " + endTag + " end tag");
    }

    public static String[] readStringArray(Context context, XmlResourceParser parser, String name)
            throws XmlPullParserException, IOException {
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException();
        }

        int eventType = parser.getEventType();
        do {
            if (eventType == XmlResourceParser.START_TAG) {
                if ("string-array".equals(parser.getName())) {
                    String itemName = parser.getAttributeValue(null, "name");
                    if (name.equals(itemName)) {
                        return readThisStringArrayXml(context, parser, "string-array", new String[] { name });
                    }
                }
            }
            eventType = parser.next();
        } while (eventType != XmlResourceParser.END_DOCUMENT);

        throw new XmlPullParserException("Document ended before 'string-array' end tag");
    }

    public static int[] readIntArray(XmlPullParser parser, String name)
            throws XmlPullParserException, IOException {
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException();
        }

        int eventType = parser.getEventType();
        do {
            if (eventType == XmlResourceParser.START_TAG) {
                if ("int-array".equals(parser.getName())) {
                    String itemName = parser.getAttributeValue(null, "name");
                    if (name.equals(itemName)) {
                        return XmlUtils.readThisIntArrayXml(parser, "int-array", new String[] { name });
                    }
                }
            }
            eventType = parser.next();
        } while (eventType != XmlResourceParser.END_DOCUMENT);

        throw new XmlPullParserException("Document ended before 'long-array' end tag");
    }

    public static long[] readLongArray(XmlPullParser parser, String name)
            throws XmlPullParserException, IOException {
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException();
        }

        int eventType = parser.getEventType();
        do {
            if (eventType == XmlResourceParser.START_TAG) {
                if ("long-array".equals(parser.getName())) {
                    String itemName = parser.getAttributeValue(null, "name");
                    if (name.equals(itemName)) {
                        return XmlUtils.readThisLongArrayXml(parser, "long-array", new String[] { name });
                    }
                }
            }
            eventType = parser.next();
        } while (eventType != XmlResourceParser.END_DOCUMENT);

        throw new XmlPullParserException("Document ended before 'long-array' end tag");
    }

    public static <T> T valueInArray(T[] array, int index, T defaultValue) {
        if (array == null || array.length == 0) {
            return defaultValue;
        }
        if (index < 0 || index >= array.length) {
            return defaultValue;
        }
        return array[index];
    }

    public static <T> int indexOf(T[] array, T item) {
        if (array == null || array.length == 0) {
            return -1;
        }

        for (int i = 0; i < array.length; i++) {
            if (Objects.equals(array[i], item)) {
                return i;
            }
        }
        return -1;
    }

    public static <T> int firstIndexOf(T[] list, Predicate<T> predicate) {
        if (list == null || list.length == 0) {
            return -1;
        }
        if (predicate == null) {
            return -1;
        }

        T result = null;
        int count = list.length;
        for (int i = 0; i < count; i++) {
            result = list[i];
            if (predicate.apply(result)) {
                return i;
            }
        }
        return -1;
    }

    private static boolean brandMatches(String currentBrand, String brands) {
        if (TextUtils.isEmpty(brands)) {
            return true;
        }
        String[] brandArray = brands.split("\\|");
        for (String brand : brandArray) {
            if (Objects.equals(currentBrand, brand)) {
                return true;
            }
        }
        return false;
    }

    public static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName == null) {
                    continue;
                }
                if (!intf.getName().equalsIgnoreCase(interfaceName)) {
                    continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac == null) {
                    return "";
                }
                StringBuilder buf = new StringBuilder();
                for (int idx = 0; idx < mac.length; idx++) {
                    buf.append(String.format("%02X:", mac[idx]));
                }
                if (buf.length() > 0) {
                    buf.deleteCharAt(buf.length() - 1);
                }
                return buf.toString();
            }
        } catch (Exception ignored) {

        }
        return "";
        /*try {
            // this is so Linux hack
            return loadFileAsString("/sys/class/net/" +interfaceName + "/address").toUpperCase().trim();
        } catch (IOException ex) {
            return null;
        }*/
    }

    public static String getLocalIp(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            Log.d(TAG, "Connected type:" + networkInfo.getType() + " Connected:" + networkInfo.isConnected() + " Available:" + networkInfo.isAvailable());
            if (networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
                try {
                    Enumeration<NetworkInterface> enumerationNi = NetworkInterface
                            .getNetworkInterfaces();
                    while (enumerationNi.hasMoreElements()) {
                        NetworkInterface networkInterface = enumerationNi.nextElement();
                        String interfaceName = networkInterface.getDisplayName();
                        if (interfaceName.equals("eth0")) {
                            Enumeration<InetAddress> enumIpAddr = networkInterface
                                    .getInetAddresses();
                            while (enumIpAddr.hasMoreElements()) {
                                InetAddress inetAddress = enumIpAddr.nextElement();
                                if (!inetAddress.isLoopbackAddress()
                                        && inetAddress instanceof Inet4Address) {
                                    Log.i(TAG, inetAddress.getHostAddress() + "   ");
                                    return inetAddress.getHostAddress();
                                }
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            } else {
                WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int ipAddress = wifiInfo.getIpAddress();
                if (ipAddress != 0) {
                    String ip = intToIp(ipAddress);
                    return ip;
                } else {
                    return "F";
                }
            }
        }
        return "F";
    }

    private static String intToIp(int i) {

        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                (i >> 24 & 0xFF);
    }



    public static InetAddress getLocalInetAddress() {
        InetAddress ip = null;
        try {
            Enumeration en_netInterface = NetworkInterface.getNetworkInterfaces();
            while (en_netInterface.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) en_netInterface.nextElement();
                Enumeration en_ip = ni.getInetAddresses();
                while (en_ip.hasMoreElements()) {
                    ip = (InetAddress) en_ip.nextElement();
                    if (!ip.isLoopbackAddress() && !ip.getHostAddress().contains(":")) {
                        break;
                    } else {
                        ip = null;
                    }
                }
                if (ip != null) {
                    break;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ip;
    }

    public static void killProcesses(Context context, List<String> packageNames){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo process:processes) {
            String processName = process.processName;
            int pid = process.pid;
            Log.d(TAG,"*** processName " + processName+"*** pid "+ pid);
            for (String packageName : packageNames) {
                if (TextUtils.equals(packageName,processName)) {
                    try {
                        Os.kill(pid, OsConstants.SIGKILL);
                    } catch (ErrnoException e) {
                        Log.d(TAG,"ErrnoException "+e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static View findViewByPredicate(View view, Predicate<View> predicate) {
        if (predicate == null) {
            return null;
        }
        if (predicate.apply(view)) {
            return view;
        }

        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            View child = null;
            for (int i = 0, count = viewGroup.getChildCount(); i < count; i++) {
                child = findViewByPredicate(viewGroup.getChildAt(i), predicate);
                if (child != null) {
                    return child;
                }
            }
        }
        return null;
    }

    public static String getUSBInternalPath(Context context){
        StorageManager mStorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            Class<?> volumeInfoClazz = Class.forName("android.os.storage.VolumeInfo");
            Method getVolumesMethod = mStorageManager.getClass().getMethod("getVolumes");
            List<?> volumeInfoList = (List<?>)getVolumesMethod.invoke(mStorageManager);
            for (int i = 0; i < volumeInfoList.size(); i++){
                Object volumeInfo = volumeInfoList.get(i);
                Method getDiskMethod = volumeInfoClazz.getMethod("getDisk");
                Object diskInfo = getDiskMethod.invoke(volumeInfo);
                if(diskInfo != null) {
                    Class<?> diskInfoClazz = Class.forName("android.os.storage.DiskInfo");
                    Method isUsbMethod = diskInfoClazz.getDeclaredMethod("isUsb");
                    Boolean isUsb = (Boolean) isUsbMethod.invoke(diskInfo);
                    Log.d("hxr", "getUSBPath isUsb = " + isUsb);
                    if(isUsb){
                        Method getPathMethod = volumeInfoClazz.getMethod("getPath");
//                        Method getInternalPathMethod = volumeInfoClazz.getMethod("getInternalPath");
                        File path = (File) getPathMethod.invoke(volumeInfo);
//                        File internalPath = (File) getInternalPathMethod.invoke(volumeInfo);
                        Log.d("hxr","getUSBPath path = "+(path==null?"":path.toString())); /* /storage/ECF3-E813 */
//                        Log.d("hxr","getUSBPath internalPath = "+(internalPath==null?"":internalPath.toString()));/* /mnt/media_rw/ECF3-E813 */
                        return path == null?null:path.toString();
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
