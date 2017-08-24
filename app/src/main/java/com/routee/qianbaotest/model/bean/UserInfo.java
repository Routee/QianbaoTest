package com.routee.qianbaotest.model.bean;

/**
 * Created by hdb on 2017/8/7.
 * description: ${TODO}
 */

public class UserInfo {
    private String userName;
    private static UserInfo sUserInfo = null;

    private UserInfo() {
    }

    public static UserInfo getInstance() {
        synchronized (UserInfo.class) {
            if (sUserInfo == null) {
                sUserInfo = new UserInfo();
            }
        }
        return sUserInfo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
