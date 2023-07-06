package pers.ervinse.ddmall.utils;

public class TokenContextUtils {
    private static String token;

    static {
        token = "null";
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        TokenContextUtils.token = token;
    }
}
