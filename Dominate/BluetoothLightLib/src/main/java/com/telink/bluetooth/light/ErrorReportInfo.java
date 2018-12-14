package com.telink.bluetooth.light;

import android.os.Parcel;
import android.os.Parcelable;
/**
 * author : Jeff  5899859876@qq.com
 * Csdn :https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-13.
 * description ：
 */


public class ErrorReportInfo implements Parcelable {


    /**
     * state code
     */
    public int stateCode;

    /**
     * error code
     */
    public int errorCode;

    public int deviceId;

    public ErrorReportInfo() {

    }

    public static final Creator<ErrorReportInfo> CREATOR = new Creator<ErrorReportInfo>() {
        @Override
        public ErrorReportInfo createFromParcel(Parcel in) {
            return new ErrorReportInfo(in);
        }

        @Override
        public ErrorReportInfo[] newArray(int size) {
            return new ErrorReportInfo[size];
        }
    };

    public ErrorReportInfo(Parcel in) {
        this.stateCode = in.readInt();
        this.errorCode = in.readInt();
        this.deviceId = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.stateCode);
        dest.writeInt(this.errorCode);
        dest.writeInt(this.deviceId);
    }

}
