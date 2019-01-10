package cn.xlink.telinkoffical.utils;

import java.util.ArrayList;
import java.util.List;

import cn.xlink.telinkoffical.MyApp;
import cn.xlink.telinkoffical.bean.greenDao.GroupSort;
import cn.xlink.telinkoffical.database.GroupSortDao;
import cn.xlink.telinkoffical.model.Group;

/**
 * Created by liucr on 2016/3/24.
 */
public class GroupsDbUtils {

    private static GroupsDbUtils groupsDbUtils;

    private static GroupSortDao groupDao;

    public GroupsDbUtils() {
        groupDao = MyApp.getApp().getDaoSession().getGroupSortDao();
    }

    public static void init() {
        groupsDbUtils = new GroupsDbUtils();
    }

    public static GroupsDbUtils getInstance() {
        return groupsDbUtils;
    }

    /**
     * 当前设备插入或更新设备
     * @param group
     */
    public void updataOrInsert(GroupSort group) {
        updataOrInsert(getCurType(), group);
    }

    /**
     * 插入或更新分组
     * @param type
     * @param group
     */
    public void updataOrInsert(String type, GroupSort group) {
        group.setType(type);
        GroupSort groupSort = getGroupByMesh(type, group.getMeshAddress());
        if(groupSort == null){
            LogUtil.e("insert group : "+ groupDao.insert(group));
        }else {
            group.setId(groupSort.getId());
            groupDao.update(group);
            LogUtil.e("update groupDao : "+ group.getName());
        }
    }

    /**
     * 删除本地记录
     * @param group
     */
    public void deleteGroupSort(GroupSort group){
        groupDao.delete(group);
    }

    public void deleteGroupSort(String account, String placeMesh){
        for(Group group : getAllGroupsByType(getType(account, placeMesh))){
            groupDao.delete(group.getGroupSort());
        }
    }

    /**
     * 通过前缀及mac查询设备
     * @param type
     * @param mesh
     * @return
     */
    public GroupSort getGroupByMesh(String type, int mesh){
        List<GroupSort> groups = groupDao.queryBuilder().where(
                GroupSortDao.Properties.Type.eq(type), GroupSortDao.Properties.MeshAddress.eq(mesh)).list();
        if(groups.size() == 0){
            return null;
        }else {
            return groups.get(0);
        }
    }

    /**
     * 通过前缀及id查询设备
     * @param id
     * @param type
     * @param id
     * @return
     */
    public GroupSort getGroupById(String type, Long id){
        List<GroupSort> groups = groupDao.queryBuilder().where(
                GroupSortDao.Properties.Type.eq(type), GroupSortDao.Properties.Id.eq(id)).list();
        if(groups.size() == 0){
            return null;
        }else {
            return groups.get(0);
        }
    }

    /**
     * 获取当前账号的所有分组
     * @return
     */
    public List<Group> getCurAccountGroups() {
        //从数据库读出所有组
        return getAllGroupsByType(getCurType());
    }

    /**
     * 根据前缀获取所有分组
     * @param type
     * @return
     */
    public List<Group> getAllGroupsByType(String type) {
        //从数据库读出所有组
        List<GroupSort> groupSorts = groupDao.queryBuilder().where(GroupSortDao.Properties.Type.eq
                (type)).list();
        List<Group> groups = new ArrayList<>();
        for(GroupSort groupSort : groupSorts){
            Group group = new Group(groupSort);
            groups.add(group);
        }
        return groups;
    }

    /**
     * 获取当前前缀
     *
     * @return
     */
    private String getCurType() {
        String type = getType(TelinkCommon.getCurDbUidType(),  TelinkCommon.getCurPlaceType());
        return type;
    }

    public static String getType(String account, String place) {
        return account + place + TelinkCommon.GROUPLIST;
    }
}
