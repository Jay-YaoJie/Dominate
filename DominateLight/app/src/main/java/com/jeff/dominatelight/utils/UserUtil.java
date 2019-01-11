package com.jeff.dominatelight.utils;

import android.text.TextUtils;
import com.jeff.dominatelight.MyApp;
import com.jeff.dominatelight.bean.greenDao.User;
import com.jeff.dominatelight.database.UserDao;

import java.util.List;


/**
 * Created by liucr on 2015/12/15.
 */
public class UserUtil {

    public static final String NoMaster = "no_master";

    private static UserDao userDao;

    public static User mUser = null;

    private static boolean isGetAccessToken = true;

    private static boolean isLogout = false;

    public static void setUser(User user) {
        mUser = user;
        saveUserData();
    }

    public static void updataUser() {
        saveUserData();
    }

    public static User getUser() {
        return mUser;
    }

    public static boolean isGetAccessToken() {
        return isGetAccessToken;
    }

    public static void setGetAccessToken(boolean getAccessToken) {
        isGetAccessToken = getAccessToken;
    }

    public static boolean isIsLogout(){
        return isLogout;
    }

    public static boolean isLogin(){
        if(mUser==null || TextUtils.isEmpty(mUser.getPassword())){
            return false;
        }else {
            return true;
        }
    }

    private static void saveUserData() {
        delUser();
        addUserToDb(mUser);
    }

    private static void delUser() {
        //删除原有
        List<User> users = userDao.queryBuilder().where(UserDao.Properties.Type.eq
                (TelinkCommon.CURUSER)).list();
        for (User user : users) {
            userDao.deleteInTx(user);
        }
    }

    private static void addUserToDb(User user) {
        User newuser = ConvertUtil.userToDao(0, TelinkCommon.CURUSER, user);
        userDao.insert(newuser);
    }

    public static void clearUser(boolean isLogOut) {
        if(mUser == null){
            return;
        }
        if(isLogOut){
            mUser.setPassword("");
            mUser.setName("");
            mUser.setAuthKey("");
            mUser.setAccessToken("");
            mUser.setRefreshToken("");
            saveUserData();
        }else {
            delUser();
            mUser = null;
        }
        isLogout = true;
    }

    public static void initUser() {
        userDao = MyApp.getApp().getDaoSession().getUserDao();
        mUser = new User();
        List<User> users = userDao.queryBuilder().where(UserDao.Properties.Type.eq (TelinkCommon.CURUSER)).list();
        if(users != null && users.size()>0 ){
            List<User> newUsers = ConvertUtil.daoToUser(users);
            mUser = newUsers.get(0);
        }else {
            mUser = null;
        }
    }

}
