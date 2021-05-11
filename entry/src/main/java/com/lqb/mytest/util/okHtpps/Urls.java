package com.lqb.mytest.util.okHtpps;

public class Urls {

    /**
     * BaseUrl 还可以根据 build.gradle 的配置来取
     * 打出不同环境的包，自动使用不同的 BaseUrl，这里便不再示例
     */
    public static final String BASE_URL = "https://8c5f239280ce.ngrok.io";

    /**
     * 当 Token 快过期时，调用该接口来刷新 Token
     */
    public static final String TOKEN_REFRESH = "/oauth/access-token";
}
