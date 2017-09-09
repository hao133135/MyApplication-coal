package model;

public class UserManager {

    private String LoginName;

    private String LoginPwd;

    public String getLoginName() {
        return LoginName;
    }

    public void setLoginName(String loginName) {
        LoginName = loginName;
    }

    public String getLoginPwd() {
        return LoginPwd;
    }

    public void setLoginPwd(String loginPwd) {
        LoginPwd = loginPwd;
    }

    @Override
    public String toString() {
        return "UserManager{" +
                "LoginName='" + LoginName + '\'' +
                ", LoginPwd='" + LoginPwd + '\'' +
                '}';
    }
}