package cn.xlink.telinkoffical.model;

import android.util.Log;

import com.telink.bluetooth.light.ConnectionStatus;

import java.util.List;

import cn.xlink.telinkoffical.R;
import cn.xlink.telinkoffical.bean.greenDao.GroupSort;
import cn.xlink.telinkoffical.manage.DataToHostManage;
import cn.xlink.telinkoffical.utils.GroupsDbUtils;
import cn.xlink.telinkoffical.utils.XlinkUtils;

/**
 * Created by liucr on 2016/3/26.
 */
public class Group {

    private GroupSort groupSort;

    public ConnectionStatus status = ConnectionStatus.OFFLINE;

    public Group(GroupSort groupSort) {
        this.groupSort = groupSort;
    }

    public GroupSort getGroupSort() {
        return groupSort;
    }

    public List<String> getMembers() {
        return XlinkUtils.stringToList(groupSort.getMembers());
    }

    public void setMembers(List<String> members) {
        groupSort.setMembers(XlinkUtils.listToString(members));
    }

    public int getStatusIcon() {
        status();
        if (status == ConnectionStatus.ON) {
            return R.mipmap.icon_item_group_on;
        } else if (status == ConnectionStatus.OFF) {
            return R.mipmap.icon_item_group_off;
        } else {
            return R.mipmap.icon_item_group_off;
        }
    }

    public int getStatusBigIcon() {
        status();
        if (status == ConnectionStatus.ON) {
            return R.mipmap.icon_group_on;
        } else if (status == ConnectionStatus.OFF) {
            return R.mipmap.icon_group_off;
        } else {
            return R.mipmap.icon_group_off;
        }
    }

    public ConnectionStatus status() {
        int on = 0;
        int off = 0;
        int out = 0;
        for (String s : getMembers()) {
            Light light = Lights.getInstance().getByMeshAddress(Integer.parseInt(s));

            if (light != null) {
                if (light.status == ConnectionStatus.ON) {
                    on++;
                } else if (light.status == ConnectionStatus.OFF) {
                    off++;
                } else {
                    out++;
                }
            }
        }
        if (out == getMembers().size()) {
            status = ConnectionStatus.OFFLINE;
        } else if (on >= off) {
            status = ConnectionStatus.ON;
        } else {
            status = ConnectionStatus.OFF;
        }
        return status;
    }

    public void updataBrightness() {
        if (getMembers().size() == 0 || Lights.getInstance().get().size() == 0) {
            return;
        }
        boolean isAllSame = true;
        int lastBrigh = Lights.getInstance().get(0).brightness;
        for (int i = 1; i < getMembers().size(); i++) {
            int mesh = Integer.parseInt(getMembers().get(i));
            if(Lights.getInstance().getByMeshAddress(mesh) == null){
                break;
            }

            if (lastBrigh != Lights.getInstance().getByMeshAddress(mesh).brightness) {
                isAllSame = false;
                break;
            }
        }

        if (isAllSame) {
            groupSort.setBrightness(lastBrigh);
        }
    }

}
