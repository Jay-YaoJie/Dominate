package com.jeff.dominatelight.model;

import android.util.Log;
import com.jeff.dominatelight.bean.greenDao.PlaceSort;
import com.jeff.dominatelight.utils.SharedPreferencesUtil;
import com.jeff.dominatelight.utils.TelinkCommon;
import com.jeff.dominatelight.utils.UserUtil;

/**
 * Created by liucr on 2016/1/11.
 */
public class Places extends DataStorageImpl<PlaceSort> {

    private static Places mThis;

    private PlaceSort curPlaceSort;

    private Places() {
        super();
    }

    public static Places getInstance() {

        if (mThis == null)
            mThis = new Places();

        return mThis;
    }

    public void setCurPlaceSortByMesh(String mesh) {
        if (getByMeshAddress(mesh) != null) {
            setCurPlaceSort(getByMeshAddress(mesh));
        }
    }

    public void setCurPlaceSort(PlaceSort curPlaceSort) {
        this.curPlaceSort = curPlaceSort;
        this.setByMeshAddress(curPlaceSort);
        SharedPreferencesUtil.keepShared(TelinkCommon.CURPLACEMESH, curPlaceSort.getMeshAddress());
    }

    public PlaceSort getCurPlaceSort() {
        return curPlaceSort;
    }

    public boolean curPlaceIsShare() {
        if (UserUtil.getUser() == null) {
            return false;
        }
        if (curPlaceSort == null || curPlaceSort.getCreatorAccount().equals(UserUtil.getUser().getAccount())) {
            return false;
        } else {
            return true;
        }
    }

    public void clearCur() {
        curPlaceSort = null;
    }

    public PlaceSort getByMeshAddress(String meshAddress) {
        PlaceSort curPlaceSort = null;
        for (PlaceSort placeSort : get()) {
            Log.i("liucr", "getByMeshAddress placeSort: " + placeSort.getMeshAddress());
            if (placeSort.getMeshAddress().equals(meshAddress)) {
                curPlaceSort = placeSort;
                break;
            }
        }
        return curPlaceSort;
    }

    public void setByMeshAddress(PlaceSort placeSort) {
        boolean isHad = false;
        for (int i = 0; i < get().size(); i++) {
            if (placeSort.getMeshAddress().equals(get().get(i).getMeshAddress())) {
                get().set(i, placeSort);
                isHad = true;
            }
        }
        if (!isHad) {
            add(placeSort);
        }
    }

    public void removeByMeshAddress(String MeshAddress) {
        for (int i = 0; i < get().size(); i++) {
            if (MeshAddress.equals(get().get(i).getMeshAddress())) {
                get().remove(i);
            }
        }
    }


}
