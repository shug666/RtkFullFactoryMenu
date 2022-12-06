package com.realtek.tvfactory.utils;

public class ByteTransformUtils {

    public static String byteToHex(byte b) {
        String hex = Integer.toHexString(b & 0xFF);
        if (hex.length() < 2) {
            hex = "0" + hex;
        }
        return hex;
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(aByte & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public static byte hexToByte(String inHex){
        return (byte)Integer.parseInt(inHex,16);
    }

    public static byte[] hexToByteArray(String inHex){
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1) {
            hexlen++;
            result = new byte[(hexlen/2)];
            inHex="0"+inHex;
        } else {
            result = new byte[(hexlen/2)];
        }
        int j=0;
        for (int i = 0; i < hexlen; i+=2){
            result[j]=hexToByte(inHex.substring(i,i+2));
            j++;
        }
        return result;
    }

    private static String toHexUtil(int n){
        String rt = "";
        switch(n) {
            case 10:rt+="A";break;
            case 11:rt+="B";break;
            case 12:rt+="C";break;
            case 13:rt+="D";break;
            case 14:rt+="E";break;
            case 15:rt+="F";break;
            default:
                rt+=n;
        }
        return rt;
    }

    public static String toHex(int n){
        StringBuilder sb = new StringBuilder();
        if(n/16 == 0){
            return toHexUtil(n);
        } else {
            String t = toHex(n/16);
            int nn = n%16;
            sb.append(t).append(toHexUtil(nn));
        }
        return sb.toString();
    }

    public static String parseAscii(String str){
        StringBuilder sb = new StringBuilder();
        byte[] bs=str.getBytes();
        for (byte b : bs) sb.append(toHex(b));
        return sb.toString();
    }

    /**
     * asciiToString
     * @param hexValue
     * @return
     */
    public static String asciiToString(String hexValue) {
        StringBuilder sbu = new StringBuilder();
        for (int i = 0; i < hexValue.length(); i += 2) {
            sbu.append((char) Integer.parseInt(hexValue.substring(i, i + 2), 16));
        }
        return sbu.toString();
    }
}
