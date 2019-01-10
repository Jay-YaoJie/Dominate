package cn.xlink.telinkoffical.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cn.xlink.telinkoffical.bean.greenDao.GroupSort;
import cn.xlink.telinkoffical.manage.CmdManage;
import cn.xlink.telinkoffical.manage.DataToHostManage;
import cn.xlink.telinkoffical.utils.GroupsDbUtils;

public class Groups extends DataStorageImpl<Group> {

    private static Groups mThis;

    private Groups() {
        super();
    }

    public static Groups getInstance() {

        if (mThis == null)
            mThis = new Groups();

        return mThis;
    }

    public boolean contains(int meshAddress) {
        for (Group group : get()) {
            if (group.getGroupSort().getMeshAddress() == meshAddress) {
                return true;
            }
        }
        return false;
    }

    public Group getByMeshAddress(int meshAddress) {
        for (Group group : get()) {
            if (group.getGroupSort().getMeshAddress() == meshAddress) {
                return group;
            }
        }
        return null;
    }

    @Override
    public void add(Group group) {
        super.add(group);
    }

    @Override
    public void add(List<Group> e) {
        for (int i = 0; i < e.size(); i++) {
            super.add(e.get(i));
            GroupsDbUtils.getInstance().updataOrInsert(e.get(i).getGroupSort());
        }
    }

    @Override
    public void remove(int location) {
        super.remove(location);
        GroupsDbUtils.getInstance().deleteGroupSort(get(location).getGroupSort());
    }

    @Override
    public void remove(Group group) {
        super.remove(group);
        GroupsDbUtils.getInstance().deleteGroupSort(group.getGroupSort());
    }

    @Override
    public void clear() {
        super.clear();
//		LightsDbUtils.getInstance().notifyDatabase(false);
    }

    /**
     * 将一个从所有分组中移除
     *
     * @param light
     */
    public void removeLight(Light light) {
        for (Group group : get()) {
            List<String> strings = group.getMembers();
            strings.remove(light.getLightSort().getMeshAddress() + "");
            group.setMembers(strings);
            GroupsDbUtils.getInstance().updataOrInsert(group.getGroupSort());
        }
    }

    /**
     * 将一个灯添加到mesh为groupMesh的组
     *
     * @param light
     * @param groupMesh
     */
    public void addLightToGroup(Light light, int groupMesh) {
        Group group = getByMeshAddress(groupMesh);
        if (group != null) {
            if (groupMesh != 0xffff) {
                CmdManage.allocDeviceGroup(group, light.getLightSort().getMeshAddress());
            }
            List<String> strings = group.getMembers();
            strings.remove(light.getLightSort().getMeshAddress() + "");
            strings.add(light.getLightSort().getMeshAddress() + "");
            group.setMembers(strings);
            GroupsDbUtils.getInstance().updataOrInsert(group.getGroupSort());
        }
    }

    public static boolean checkNameHad(String name, String oldName) {
        for (Group group : getInstance().get()) {
            if (group.getGroupSort().getName().equals(name) &&
                    !group.getGroupSort().getName().equals(oldName)) {
                return true;
            }
        }
        return false;
    }
}
