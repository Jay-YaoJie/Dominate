package com.telink.bluetooth.light.model;

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
