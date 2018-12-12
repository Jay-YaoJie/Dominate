package com.telink.bluetooth.light.util;

import android.content.Context;
import android.os.Environment;

import com.telink.bluetooth.TelinkLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class FileSystem_h {

    public static boolean writeAsString(String fileName, String content) {

        File dir = Environment.getExternalStorageDirectory();
        File savePath = new File(dir.getAbsolutePath() + File.separator + "TelLog");
        if (!savePath.exists()) {
            savePath.mkdirs();
        }
        File file = new File(savePath, fileName);
//        FileWriter fw;
        FileOutputStream fos;
        try {

            if (!file.exists())
                file.createNewFile();
            fos = new FileOutputStream(file);
            fos.write(content.getBytes());
            fos.flush();
            fos.close();
            /*fw = new FileWriter(file, false);

            fw.write(content);

            fw.flush();
            fw.close();*/

            return true;
        } catch (IOException e) {
        }

        return false;
    }

    public static String readAsString(String fileName) {

        File dir = Environment.getExternalStorageDirectory();
        File file = new File(dir, fileName);

        if (!file.exists())
            return "";

        try {

            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = null;

            StringBuilder sb = new StringBuilder();

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            br.close();
            fr.close();

            return sb.toString();

        } catch (IOException e) {

        }

        return "";
    }

    public static boolean exists(String fileName) {
        File directory = Environment.getExternalStorageDirectory();
        File file = new File(directory, fileName);
        return file.exists();
    }

    public static boolean writeAsObject(Context context, String fileName, Object obj) {

//        File dir = Environment.getExternalStorageDirectory();
        File dir = Environment.getExternalStorageDirectory();
        File file = new File(dir, fileName);

        FileOutputStream fos = null;
        ObjectOutputStream ops = null;

        boolean success = false;
        try {

            if (!file.exists())
                file.createNewFile();

            fos = new FileOutputStream(file);
            ops = new ObjectOutputStream(fos);

            ops.writeObject(obj);
            ops.flush();

            success = true;

        } catch (IOException e) {

        } finally {
            try {
                if (ops != null)
                    ops.close();
                if (ops != null)
                    fos.close();
            } catch (Exception e) {
            }
        }

        return success;
    }

    public static Object readAsObject(String fileName) {

        File dir = Environment.getExternalStorageDirectory();
        File file = new File(dir, fileName);

        if (!file.exists())
            return null;

        FileInputStream fis = null;
        ObjectInputStream ois = null;

        Object result = null;
        try {

            fis = new FileInputStream(file);
            ois = new ObjectInputStream(fis);

            result = ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            TelinkLog.w("read object error : ", e);
        } finally {
            try {
                if (ois != null)
                    ois.close();
            } catch (Exception e) {
            }
        }

        return result;
    }
}
