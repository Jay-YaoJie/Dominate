package com.outes.greendao;


import com.github.ichenkaihua.greendao.annotation.EntityInject;
import com.github.ichenkaihua.greendao.annotation.GenerateConfig;
import com.github.ichenkaihua.greendao.annotation.SchemaConfig;
import com.github.ichenkaihua.greendao.service.GenerateService;

import de.greenrobot.daogenerator.Entity;

/**
 *
 * @Title: Main
 * @Description: 类的描述 -
 * @date 2015-6-8
 *
 */
@GenerateConfig(outDir = "app\\src\\main\\java", schemaConfig = @SchemaConfig(defaultJavaPackage = "cn" +
        ".xlink.telinkoffical.bean.greenDao", defaultJavaPackageDao = "cn.xlink.telinkoffical.database", version = 2))
public class Main {

    void createUser(@EntityInject("User") Entity user){
        user.addIdProperty();
        user.addStringProperty("Account");
        user.addStringProperty("Name");
        user.addStringProperty("Password");
        user.addStringProperty("Uid");
        user.addStringProperty("AuthKey");
        user.addStringProperty("RefreshToken");
        user.addStringProperty("AccessToken");
        user.addBooleanProperty("isVaild");
        user.addStringProperty("Type");
    }

    void createPlace(@EntityInject("PlaceSort") Entity placeSort){
        placeSort.addIdProperty();

        placeSort.addStringProperty("name");
        placeSort.addStringProperty("meshAddress");
        placeSort.addStringProperty("meshKey");
        placeSort.addStringProperty("factoryName");
        placeSort.addStringProperty("factoryMeshKey");
        placeSort.addStringProperty("placeId");
        placeSort.addStringProperty("creatorAccount");
        placeSort.addStringProperty("creatorName");
        placeSort.addStringProperty("creatorId");
        placeSort.addStringProperty("createDate");
        placeSort.addLongProperty("placeVersion");
        placeSort.addStringProperty("lastUseDate");
        placeSort.addIntProperty("deviceIdRecord");
        placeSort.addIntProperty("role");

        placeSort.addStringProperty("Type");
    }

    void createLight(@EntityInject("LightSort") Entity lightSort){
        lightSort.addIdProperty();

        lightSort.addStringProperty("name");
        lightSort.addStringProperty("macAddress");
        lightSort.addStringProperty("firmwareRevision");
        lightSort.addIntProperty("meshAddress");
        lightSort.addIntProperty("lightType");
        lightSort.addBooleanProperty("isAlone");
        lightSort.addBooleanProperty("isShowOnHomeScreen");
        lightSort.addBooleanProperty("isAddToDefault");

        lightSort.addStringProperty("Type");
    }

    void createGroup(@EntityInject("GroupSort") Entity group) {
        group.addIdProperty();

        group.addStringProperty("name");
        group.addIntProperty("meshAddress");
        group.addIntProperty("brightness");
        group.addIntProperty("color");
        group.addIntProperty("temperature");
        group.addBooleanProperty("isShowOnHomeScreen");
        group.addStringProperty("members");

        group.addStringProperty("Type");
    }

    void createScene(@EntityInject("SceneSort") Entity sceneSort){
        sceneSort.addIdProperty();
        sceneSort.addStringProperty("name");
        sceneSort.addIntProperty("sceneId");
        sceneSort.addIntProperty("brightness");
        sceneSort.addIntProperty("temperature");
        sceneSort.addIntProperty("color");
        sceneSort.addIntProperty("sceneType");
        sceneSort.addStringProperty("actions");
        sceneSort.addBooleanProperty("isShowOnHomeScreen");
        sceneSort.addStringProperty("Type");
    }

    void createSceneActionSort(@EntityInject("SceneActionSort") Entity sceneAction){
        sceneAction.addIdProperty();
        sceneAction.addIntProperty("sceneId");
        sceneAction.addIntProperty("brightness");
        sceneAction.addIntProperty("temperature");
        sceneAction.addIntProperty("color");
        sceneAction.addIntProperty("deviceMesh");
        sceneAction.addStringProperty("Type");
    }

    void createSceneTimerSort(@EntityInject("SceneTimerSort") Entity sceneTimer){
        sceneTimer.addIdProperty();
        sceneTimer.addIntProperty("sceneId");
        sceneTimer.addIntProperty("sceneTimerId");
        sceneTimer.addIntProperty("timerId");
        sceneTimer.addIntProperty("timerType");
        sceneTimer.addIntProperty("deviceMesh");
        sceneTimer.addIntProperty("hour");
        sceneTimer.addIntProperty("minute");
        sceneTimer.addIntProperty("workDay");
        sceneTimer.addBooleanProperty("isEnable");
        sceneTimer.addStringProperty("Type");
    }

    // Main方法
    public static void main(String[] args) {
        // 传入带有类注解的Main.class类，GreenDao-Simple-Generate将会扫描类注解，配置输出路径，输出包名
        // 然后扫描这个类下的所有（public protected甚至private）方法
        // 如果方法参数有Schema、GenerateInfo、SchemaInfo、@EntityInject注解，则系统会注入相应对象
        // 最后调用这个方法
        new GenerateService(Main.class).generate();
    }
}