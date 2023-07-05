package pers.ervinse.ddmall.domain;


import java.io.Serializable;

public class User implements Serializable {

    private String UserName, UserPassword, UserExtendInfo, UserAccount, UserSex;
    private Integer UserAge;
    private Integer UserID;

    public User() {

    }

    public User(String UserAccount, String UserName, String UserPassword) {
        this.UserAccount = UserAccount;
        this.UserName = UserName;
        this.UserPassword = UserPassword;
    }

    public User(String UserAccount, String UserPassword) {
        this.UserAccount = UserAccount;
        this.UserName = UserName;
        this.UserPassword = UserPassword;
    }

    public User(String UserAccount, String UserName, String UserPassword, String UserExtendInfo, String UserSex, Integer UserAge) {
        this.UserName = UserName;
        this.UserPassword = UserPassword;
        this.UserExtendInfo = UserExtendInfo;
        this.UserAccount = UserAccount;
        this.UserSex = UserSex;
        this.UserAge = UserAge;
    }

    public Integer getUserID() {
        return UserID;
    }

    public void setUserID(Integer userID) {
        UserID = userID;
    }

    public String getUserAccount() {
        return UserAccount;
    }

    public void setUserAccount(String UserAccount) {
        this.UserAccount = UserAccount;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public String getUserSex() {
        return UserSex;
    }

    public Integer getUserAge() {
        return UserAge;
    }

    public String getUserPassword() {
        return UserPassword;
    }

    public void setUserPassword(String UserPassword) {
        this.UserPassword = UserPassword;
    }

    public String getUserExtendInfo() {
        return UserExtendInfo;
    }

    public void setUserExtendInfo(String UserExtendInfo) {
        this.UserExtendInfo = UserExtendInfo;
    }

    @Override
    public String toString() {
        return "User{" +
                "UserAccount='" + UserAccount + '\'' +
                ", UserName='" + UserName + '\'' +
                ", UserPassword='" + UserPassword + '\'' +
                ", UserExtendInfo='" + UserExtendInfo + '\'' +
                '}';
    }
}
