package cn.xlink.telinkoffical.utils;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.xlink.telinkoffical.MyApp;


public class XlinkUtils {
    /**
     * Map 转换为json
     *
     * @param map
     * @return
     */
    public static JSONObject getJsonObject(Map<String, Object> map) {
        JSONObject jo = new JSONObject();
        Iterator<Entry<String, Object>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, Object> entry = iter.next();
            try {
                jo.put(entry.getKey(), entry.getValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jo;

    }

    public static void cleanDatabases(Context context) {
        System.out.println(context.getFilesDir().getPath() + "");
        deleteFilesByDirectory(new File("/data/data/"
                + context.getPackageName() + "/databases"));
    }

    public static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }

    public static byte[] stringTobyte(String text) {

        return text.getBytes(Charset.forName(XlinkUtils.ENCODING));
    }

    /**
     * 动态设置listview的高度 item必须是LinearLayout
     *
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        BaseAdapter listAdapter = (BaseAdapter) listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) listView
                .getLayoutParams();

        if (listAdapter.getCount() == 0) {
            totalHeight = 0;
            listView.setVisibility(View.GONE);
        } else if (listAdapter.getCount() == 1) {
            listView.setVisibility(View.VISIBLE);
            listView.setPadding(0, 0, 0, 0);
        } else {
            totalHeight += listView.getPaddingTop()
                    + listView.getPaddingBottom();
            totalHeight += (listView.getDividerHeight() * (listAdapter
                    .getCount() + 1));

            listView.setVisibility(View.VISIBLE);

        }

        params.height = totalHeight;
        listView.setLayoutParams(params);
    }

    /**
     * 验证邮箱
     *
     * @param email
     * @return
     */
    public static boolean checkEmail(String email) {
        if (email.length() > 30) {
            return false;
        }
        boolean flag = false;
        try {
            String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+[a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * 字符串 16进制转bytes
     *
     * @param hexString
     * @return
     */
    public static byte[] stringToByteArray(String hexString) {
        if (hexString.isEmpty() || hexString.length() % 2 != 0)
            return null;
        hexString = hexString.replace(" ", "");
        hexString = hexString.replaceAll(":", "");
//        hexString = hexString.trim();
        hexString = hexString.toLowerCase();
        final byte[] byteArray = new byte[hexString.length() / 2];
        int k = 0;
        for (int i = 0; i < byteArray.length; i++) {
            // 因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
            byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
            byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
            byteArray[i] = (byte) (high << 4 | low);
            k += 2;
        }
        return byteArray;
    }

    public static String weekToString(byte week) {
        StringBuilder sb = new StringBuilder();
        if (week == 0) {
            sb.append("单次");
        } else if ((byte) week == (byte) 0x3E) {
            sb.append("工作日");
        } else if (week == (byte) 0x7f) {
            sb.append("每天");
        } else {
            sb.append("周");
            if (readFlagsBit(week, 0)) {
                sb.append("日");
            }
            if (readFlagsBit(week, 1)) {
                sb.append("一");
            }
            if (readFlagsBit(week, 2)) {
                sb.append("二");
            }
            if (readFlagsBit(week, 3)) {
                sb.append("三");
            }
            if (readFlagsBit(week, 4)) {
                sb.append("四");
            }
            if (readFlagsBit(week, 5)) {
                sb.append("五");
            }
            if (readFlagsBit(week, 6)) {
                sb.append("六");
            }
        }

        return sb.toString();
    }


    public byte[] stringToByte(String src) {
        try {
            return src.getBytes(XlinkUtils.ENCODING);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param bytes
     * @param offset
     * @param len
     * @return
     */
    public static byte[] subBytes(byte[] bytes, int offset, int len) {
        byte[] subBs = new byte[len - offset];
        System.arraycopy(bytes, offset, subBs, 0, len);

        return subBs;
    }

    // 数据编码类型(固定)
//    public static final String ENCODING = "UTF-8";
    public static final String ENCODING = "GBK";

    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * 将32位整数转换成长度为4的byte数组
     *
     * @param i int
     * @return byte[]
     */
    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        result[3] = (byte) ((i >> 24) & 0xFF);
        result[2] = (byte) ((i >> 16) & 0xFF);
        result[1] = (byte) ((i >> 8) & 0xFF);
        result[0] = (byte) (i & 0xFF);
        return result;
    }


    //
    // /**
    // * 将32位整数转换成长度为4的byte数组
    // *
    // * @param s
    // * int
    // * @return byte[]
    // * */
    // public static byte[] intToByteArray2(int i) {
    // byte[] result = new byte[4];
    // result[0] = (byte) ((i >> 24) & 0xFF);
    // result[1] = (byte) ((i >> 16) & 0xFF);
    // result[2] = (byte) ((i >> 8) & 0xFF);
    // result[3] = (byte) (i & 0xFF);
    // return result;
    // }

    /**
     * byte[] 转int 高位在前，低位在后
     *
     * @param src
     * @return
     */
    public static int bytesToInt(byte[] src) {
        int value;
        value = (int) (((src[3] & 0xFF) << 24) | ((src[2] & 0xFF) << 16)
                | ((src[1] & 0xFF) << 8) | (src[0] & 0xFF));
        return value;
    }

    // /**
    // *
    // * @param src
    // * @return
    // */
    // public static int bytesToInt2(byte[] src) {
    // int value;
    // value = (int) (((src[0] & 0xFF) << 24) | ((src[0 + 1] & 0xFF) << 16)
    // | ((src[0 + 2] & 0xFF) << 8) | (src[0 + 3] & 0xFF));
    // return value;
    // }

    public static byte[] concat(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    /**
     * BASE64解密
     *
     * @param key
     * @return
     * @throws IOException
     */
    public static byte[] base64Decrypt(String key) {
        byte[] bs = Base64.decode(key, Base64.DEFAULT);
        if (bs == null || bs.length == 0) {
            bs = key.getBytes();
        }
        return bs;
    }

    /**
     * 判断网络是否连接
     *
     * @return
     */

    public static boolean isConnected() {

        ConnectivityManager connectivity = (ConnectivityManager) MyApp.getApp()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (null != connectivity) {

            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isAvailable()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否是wifi连接
     */
    public static boolean isWifi() {
        ConnectivityManager cm = (ConnectivityManager) MyApp.getApp()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null || cm.getActiveNetworkInfo() == null) {
            return false;
        }

        return cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;

    }

    public static String getHexBinString(byte[] bs) {
        StringBuffer log = new StringBuffer();
        for (int i = 0; i < bs.length; i++) {
            log.append(String.format("%02X", (byte) bs[i]) + " ");
        }
        return log.toString();
    }

    public static String getHexBinString2(byte[] bs) {
        StringBuffer log = new StringBuffer();
        for (int i = 0; i < bs.length; i++) {
            log.append(String.format("%02X", (byte) bs[i]));
        }
        return log.toString();
    }

    /**
     * 把byte转化成 二进制.
     *
     * @param aByte
     * @return
     */
    public static String getBinString(byte aByte) {
        String out = "";
        int i = 0;
        for (i = 0; i < 8; i++) {
            int v = (aByte << i) & 0x80;
            v = (v >> 7) & 1;
            out += v;
        }
        return out;
    }

    static private final int bitValue0 = 0x01; // 0000 0001
    static private final int bitValue1 = 0x02; // 0000 0010
    static private final int bitValue2 = 0x04; // 0000 0100
    static private final int bitValue3 = 0x08; // 0000 1000
    static private final int bitValue4 = 0x10; // 0001 0000
    static private final int bitValue5 = 0x20; // 0010 0000
    static private final int bitValue6 = 0x40; // 0100 0000
    static private final int bitValue7 = 0x80; // 1000 0000

    /**
     * 设置flags
     *
     * @param index 第几个bit，从零开始排
     * @param value byte值
     * @return
     */
    public static byte setByteBit(int index, byte value, boolean b) {
        if (index > 7) {
            throw new IllegalAccessError("setByteBit error index>7!!! ");
        }
        byte ret = value;
        if (index == 0) {
            if (b) {
                ret |= bitValue0;
            } else {
                ret &= 0xfe;
            }
        } else if (index == 1) {
            if (b) {
                ret |= bitValue1;
            } else {
                ret &= 0xfd;
            }
        } else if (index == 2) {
            if (b) {
                ret |= bitValue2;
            } else {
                ret &= 0xfb;
            }
        } else if (index == 3) {
            if (b) {
                ret |= bitValue3;
            } else {
                ret &= 0xf7;
            }
        } else if (index == 4) {
            if (b) {
                ret |= bitValue4;
            } else {
                ret &= 0xEF;
            }
        } else if (index == 5) {
            if (b) {
                ret |= bitValue5;
            } else {
                ret &= 0xdF;
            }
        } else if (index == 6) {
            if (b) {
                ret |= bitValue6;
            } else {
                ret &= 0xbF;
            }
        } else if (index == 7) {
            if (b) {
                ret |= bitValue7;
            } else {
                ret &= 0x7F;
            }
        }
        return ret;
    }

    /**
     * 设置flags
     *
     * @param index 第几个bit，从零开始排
     * @param value byte值
     * @return
     */
    public static byte setByteBit2(int index, byte value) {
        if (index > 7) {
            throw new IllegalAccessError("setByteBit error index>7!!! ");
        }
        byte ret = value;
        if (index == 0) {
            ret |= bitValue0;
        } else if (index == 1) {
            ret |= bitValue1;
        } else if (index == 2) {
            ret |= bitValue2;
        } else if (index == 3) {
            ret |= bitValue3;
        } else if (index == 4) {
            ret |= bitValue4;
        } else if (index == 5) {
            ret |= bitValue5;
        } else if (index == 6) {
            ret |= bitValue6;
        } else if (index == 7) {
            ret |= bitValue7;
        }
        return ret;
    }

    /**
     * 读取 flags 里的小bit
     *
     * @param anByte
     * @param index
     * @return
     */
    public static boolean readFlagsBit(byte anByte, int index) {
        if (index > 7) {
            throw new IllegalAccessError("readFlagsBit error index>7!!! ");
        }
        int temp = anByte << (7 - index);
        temp = temp >> 7;
        temp &= 0x01;
        if (temp == 1) {
            return true;
        }
        // if((anByte & (01<<index)) !=0){
        // return true;
        // }
        return false;
    }

    /**
     * 将16位的short转换成byte数组
     *
     * @param s short
     * @return byte[] 长度为2
     */
    public static byte[] shortToByteArray(short s) {
//        byte[] targets = new byte[2];
//        targets[0] = (byte) (s & 0xff); // 获得低位字节
//        targets[1] = (byte) (s >>> 8);// 获得高位字节

        // for (int i = 0; i < 2; i++) {
        // int offset = (targets.length - 1 - i) * 8;
        // targets[i] = (byte) ((s >>> offset) & 0xff);
        // }

        byte[] targets = new byte[2];
        for (int i = 0; i < 2; i++) {
            int offset = (targets.length - 1 - i) * 8;
            targets[i] = (byte) ((s >>> offset) & 0xff);
        }
        return targets;
    }

    public static short byteToShort(byte b1, byte b2) {
        short s = 0;
        short s0 = (short) (b1 & 0xff);// 最低位
        short s1 = (short) (b2 & 0xff);
        s1 <<= 8;
        s = (short) (s0 | s1);
        return s;
    }

    public static String byteToString(byte[] bs) {
        try {
            String str = new String(bs, ENCODING).trim();
            // str = str.replace("  ", "");
            return str;
        } catch (UnsupportedEncodingException e) {
            String srt = new String(bs);
            // srt = srt.replace("  ", "");
            return srt;
        }
    }

    /**
     * byte[] 转int 高位在前，低位在后
     *
     * @param src
     * @return
     */
    public static int bytesToInt2(byte[] src) {
        int value;
        value = (int) (((src[0] & 0xFF) << 24) | ((src[0 + 1] & 0xFF) << 16) | ((src[0 + 2] & 0xFF) << 8)
                | (src[0 + 3] & 0xFF));
        return value;
    }

    /**
     * 将byte[2]转换成short
     *
     * @param b
     * @param offset
     * @return
     */
    public static int byte2Short(byte[] b, int offset) {
        return (int) ((((b[offset] & 0xff) << 8) & 0xff00) | (b[offset + 1] & 0xff));
    }


    @SuppressWarnings("unchecked")
    public static <T extends View> T getAdapterView(View convertView, int id) {
        SparseArray<View> viewHolder = (SparseArray<View>) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<View>();
            convertView.setTag(viewHolder);
        }
        View childView = viewHolder.get(id);
        if (childView == null) {
            childView = convertView.findViewById(id);
            viewHolder.put(id, childView);
        }
        return (T) childView;
    }

    public final static String MD5(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * BASE64加密
     *
     * @param key
     * @return
     * @throws UnsupportedEncodingException
     * @throws Exception
     */
    public static String base64EncryptUTF(byte[] key)
            throws UnsupportedEncodingException {
        return new String(Base64.encode(key, Base64.DEFAULT), "UTF-8");
    }

    public static String base64Encrypt(byte[] key) {
        return new String(Base64.encode(key, Base64.DEFAULT));
    }


    private static byte msgId = 0;

    /**
     * 获取随机 message id
     *
     * @return
     */
    public static byte getMsgId() {
        if (msgId == 0 || msgId > 255) {
            msgId = (byte) getRandomPid();
        }
        return msgId++;
    }

    /**
     * 获取随机数(message id用)ֵ
     *
     * @return
     */
    private static int getRandomPid() {
        int max = 250;
        int min = 10;
        Random random = new Random();
        int s = random.nextInt(max) % (max - min + 1) + min;
        return s;
    }

    /**
     * 打开网络设置界面
     */
    public static void openSetting(Activity activity) {
        Intent intent = null;
        if (android.os.Build.VERSION.SDK_INT > 10) {
            intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
        } else {
            intent = new Intent("/");
            ComponentName cm = new ComponentName("com.android.settings",
                    "com.android.settings.WirelessSettings");
            intent.setComponent(cm);
            intent.setAction("android.intent.action.VIEW");
        }
        activity.startActivityForResult(intent, 0);
    }

    public static void shortTips(final String tip) {
        Log.e("Tips", tip);
        // handler.obtainMessage(TIPS, tip);
        MyApp.postToMainThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(MyApp.getApp(), tip, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private final static int TIPS = 1;


    public static void longTips(String tip) {
        Toast.makeText(MyApp.getApp(), tip, Toast.LENGTH_LONG).show();
    }

    // public static void showTopTips(String tips, Activity activity) {
    // Toast toast = new Toast(activity);
    // toast.setDuration(2000);
    // toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
    // LayoutInflater layoutInflater = LayoutInflater.from(activity);
    // View view = layoutInflater.inflate(R.layout.toast_layout, null);
    // ((TextView) view.findViewById(R.id.toast_text)).setText(tips);
    //
    // WindowManager windowManager = activity.getWindowManager();
    // Display display = windowManager.getDefaultDisplay();
    // @SuppressWarnings("deprecation")
    // int screenWidth = display.getWidth();
    // @SuppressWarnings("deprecation")
    // int screenHeight = display.getHeight();
    // view.getLayoutParams().width=screenWidth;
    // view.getLayoutParams().height=screenHeight;
    // // FrameLayout layout2 = (FrameLayout) view.findViewById(R.id.toast_fr);
    // // layout2.getLayoutParams().width = screenWidth;
    // // layout2.getLayoutParams().height = 30;
    // toast.setView(view);
    // toast.show();
    // }

    /**
     * 对小数格式化
     *
     * @param format
     * @param source
     * @return
     */
    public static String getDecimalFormatString(String format, double source) {
        try {
            DecimalFormat df = new DecimalFormat(format);
            return df.format(source);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static float applyDimension(int unit, float value) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        switch (unit) {
            case TypedValue.COMPLEX_UNIT_PX:
                return value;
            case TypedValue.COMPLEX_UNIT_DIP:
                return value * metrics.density;
            case TypedValue.COMPLEX_UNIT_SP:
                return value * metrics.scaledDensity;
            case TypedValue.COMPLEX_UNIT_PT:
                return value * metrics.xdpi * (1.0f / 72);
            case TypedValue.COMPLEX_UNIT_IN:
                return value * metrics.xdpi;
            case TypedValue.COMPLEX_UNIT_MM:
                return value * metrics.xdpi * (1.0f / 25.4f);
        }
        return 0;
    }

    public static String appendString(Object...objects){
        if (objects == null){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < objects.length; i++) {
            sb.append(objects[i]);
        }
        return sb.toString();
    }

    public static String getRandomString(int length) { //length表示生成字符串的长度
        String base = "ABCDEF0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public static String getVirtualMac() {
        UUID uuid = UUID.randomUUID();
        String uniqueId = uuid.toString();
        String md5String = MD5(uniqueId);
        Log.e("getMyUUID", "----->md5String :  " + md5String);
        String mac = md5String.substring(md5String.length()-12,md5String.length());
        Log.e("getMyUUID", "----->mac :  " + mac);
        return mac;
    }

    public static String getRandomInt(int length) { //length表示生成字符串的长度
        String base = "0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public static String listToString(List<String> listString) {
        if (listString == null) {
            return "";
        }
        String s = listString.toString().replace("[", "");
        s = s.replace("]", "");
        s = s.replace(" ", "");
        return s;
    }

    public static List<String> stringToList(String string) {
        List<String> stringList = new ArrayList<>();
        if (TextUtils.isEmpty(string)) {
            return stringList;
        }
        String[] arr = string.split(",");
        List<String> list = java.util.Arrays.asList(arr);
        for (String s : list) {
            stringList.add(s);
        }
        return stringList;
    }

    public static String reverse(String s) {
        return new StringBuffer(s).reverse().toString();
    }
}
