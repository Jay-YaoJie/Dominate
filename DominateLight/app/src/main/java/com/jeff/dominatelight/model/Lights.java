package com.jeff.dominatelight.model;

import com.jeff.dominatelight.utils.LightsDbUtils;
import com.jeff.dominatelight.utils.PlacesDbUtils;

import java.util.ArrayList;
import java.util.List;


public class Lights extends DataStorageImpl<Light> {

    private static Lights mThis;

    private Lights() {
        super();
    }

    public static Lights getInstance() {

        if (mThis == null)
            mThis = new Lights();

        return mThis;
    }

    public boolean contains(int meshAddress) {
        for (Light light : get()) {
            if (light.getLightSort().getMeshAddress() == meshAddress) {
                return true;
            }
        }
        return false;
    }

    public Light getByMeshAddress(int meshAddress) {
        for (Light light : get()) {
            if (light.getLightSort().getMeshAddress() == meshAddress) {
                return light;
            }
        }
        return null;
    }

    public Light getByMac(String mac) {
        for (Light light : get()) {
            if (light.getLightSort().getMacAddress().equals(mac)) {
                return light;
            }
        }
        return null;
    }

    @Override
    public void add(Light light) {
        if (!contains(light.getLightSort().getMeshAddress())) {
            super.add(light);
            LightsDbUtils.getInstance().updataOrInsert(light.getLightSort());
        }
    }

    @Override
    public void add(Light light, int location) {
        if (!contains(light.getLightSort().getMeshAddress())) {
            super.add(light, location);
        }
        LightsDbUtils.getInstance().updataOrInsert(light.getLightSort());
    }

    public int getNextMeshAddress() {
        int max = 0;

        max = Places.getInstance().getCurPlaceSort().getDeviceIdRecord();

        if (max > 254) {
            for (int i = 0; i < 255; i++) {
                boolean isHad = false;
                for (Light light : get()) {
                    if (light.getLightSort().getMeshAddress() == i) {
                        isHad = true;
                        break;
                    }
                }
                if (!isHad) {
                    max = i;
                    break;
                }
            }
        }
        return max + 1;
    }

    public void setCurMesh(int mesh){
        Places.getInstance().getCurPlaceSort().setDeviceIdRecord(mesh);
        PlacesDbUtils.getInstance().updataOrInsert(Places.getInstance().getCurPlaceSort());
    }

    /**
     * 通过收数据解析为灯的组mesh
     *
     * @param params
     */
    public static List<Integer> getLightGroups(byte[] params) {
        List<Integer> list = new ArrayList<>();
        for (byte b : params) {
            if (b != -1) {
                int group = 0x8000 + b;
                list.add(group);
            }
        }
        return list;
    }

    @Override
    public void add(List<Light> e) {

        for (int i = 0; i < e.size(); i++) {
            if (!contains(e.get(i).getLightSort().getMeshAddress())) {
                super.add(e.get(i));
                LightsDbUtils.getInstance().updataOrInsert(e.get(i).getLightSort());
            }
        }

    }

    @Override
    public void remove(int location) {
        super.remove(location);
        LightsDbUtils.getInstance().deleteLight(get(location).getLightSort());
    }

    @Override
    public void remove(Light light) {
        super.remove(light);
        LightsDbUtils.getInstance().deleteLight(light.getLightSort());
    }

    @Override
    public void clear() {
        super.clear();
//		LightsDbUtils.getInstance().notifyDatabase(false);
    }

    public int getOTACount() {
        int i = 0;
        for (Light light : get()) {
            if (light.isCanUpdata() == 1) {
                i++;
            }
        }
        return i;
    }

    public static boolean checkNameHad(String name, String oldName){
        for(Light light : getInstance().get()){
            if(light.getLightSort().getName().equals(name)
                     && !light.getLightSort().getName().equals(oldName)){
                return true;
            }
        }
        return false;
    }

}
