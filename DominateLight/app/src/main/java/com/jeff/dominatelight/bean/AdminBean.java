package com.jeff.dominatelight.bean;

import java.io.Serializable;

/**
 * Created by liucr on 2016/1/14.
 */
public class AdminBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String createDate;

    private String emailAddress;

    private String lastUseDate;

    private String username;

    private long userID;

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setLastUseDate(String lastUseDate) {
        this.lastUseDate = lastUseDate;
    }

    public String getLastUseDate() {
        return lastUseDate;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public long getUserID() {
        return userID;
    }
}
