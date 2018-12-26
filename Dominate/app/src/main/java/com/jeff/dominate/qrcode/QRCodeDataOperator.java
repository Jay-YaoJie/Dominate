package com.jeff.dominate.qrcode;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.jeff.dominate.TelinkLightApplication;
import com.jeff.dominate.model.Light;
import com.jeff.dominate.model.Mesh;
import com.jeff.dominate.model.SharedPreferencesHelper;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kee on 2017/12/27.
 */

public class QRCodeDataOperator {

    public String provideStr() {
        Mesh mesh = TelinkLightApplication.getApp().getMesh();
        if (mesh == null) {
            return "{}";
        }
        TmpMesh tmpMesh = new TmpMesh();
        tmpMesh.n = mesh.name;
        tmpMesh.p = mesh.password;
        if (mesh.devices != null) {
            List<TmpDeviceInfo> deviceInfoList = new ArrayList<>();

            TmpDeviceInfo tmpDeviceInfo;
            for (Light deviceInfo : mesh.devices) {
                tmpDeviceInfo = new TmpDeviceInfo();
                tmpDeviceInfo.m = deviceInfo.macAddress;
                tmpDeviceInfo.a = deviceInfo.meshAddress;
                tmpDeviceInfo.v = deviceInfo.firmwareRevision;
                tmpDeviceInfo.pu = deviceInfo.productUUID;

                deviceInfoList.add(tmpDeviceInfo);
            }
            tmpMesh.d = deviceInfoList;
        }

        Gson gson = new Gson();
        return gson.toJson(tmpMesh);
        /*JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("n", tmpMesh.n);
            jsonObject.put("p", tmpMesh.p);
//            jsonObject.put("d", tmpMesh.d);
            jsonObject.putOpt("d", tmpMesh.d);
            return jsonObject.toString();
        }catch (Exception e){
            return null;
        }*/
    }

    public boolean parseData(String data) {
        Gson gson = new Gson();
        TmpMesh tmpMesh = gson.fromJson(data, TmpMesh.class);
        if (tmpMesh != null && !TextUtils.isEmpty(tmpMesh.n) && !TextUtils.isEmpty(tmpMesh.p)) {
            Mesh newMesh = new Mesh();
            Mesh oldMesh = TelinkLightApplication.getApp().getMesh();

            newMesh.name = tmpMesh.n;
            newMesh.password = tmpMesh.p;
            newMesh.factoryName = oldMesh.factoryName;
            newMesh.factoryPassword = oldMesh.factoryPassword;

            newMesh.devices = new ArrayList<>();
            if (tmpMesh.d != null) {
                Light deviceInfo;
                for (TmpDeviceInfo tmpDeviceInfo : tmpMesh.d) {
                    deviceInfo = new Light();
                    deviceInfo.meshName = newMesh.name;
                    deviceInfo.deviceName = newMesh.name;
                    deviceInfo.macAddress = tmpDeviceInfo.m;
                    deviceInfo.meshAddress = tmpDeviceInfo.a;
                    deviceInfo.firmwareRevision = tmpDeviceInfo.v;
                    deviceInfo.productUUID = tmpDeviceInfo.pu;
                    newMesh.devices.add(deviceInfo);
                }
            }
            newMesh.saveOrUpdate(TelinkLightApplication.getApp());
            SharedPreferencesHelper.saveMeshName(TelinkLightApplication.getApp(), newMesh.name);
            SharedPreferencesHelper.saveMeshPassword(TelinkLightApplication.getApp(), newMesh.password);
            TelinkLightApplication.getApp().setupMesh(newMesh);
            return true;
        }

        return false;
    }

    class TmpMesh {
        String n;
        String p;
        List<TmpDeviceInfo> d;
    }

    class TmpDeviceInfo {
        String m;
        int a;
        String v;
        int pu;
    }
}
