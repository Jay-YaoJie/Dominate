package com.jeff.dominate.model;
/**
 * author : Jeff  5899859876@qq.com
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-11-17.
 * description ：
 */
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
        return this.contains("meshAddress", meshAddress);
    }

    public Group getByMeshAddress(int meshAddress) {
        return this.get("meshAddress", meshAddress);
    }

}
