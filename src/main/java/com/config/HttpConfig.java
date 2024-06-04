package com.config;

/**
 * @Author: admin
 * @Date: 2022/6/8 17:24
 */
public class HttpConfig {
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36";
    public static final String USER_API_AGENT = "okhttp/3.10.0.1";
    public static final String REFERER = "https://www.douyin.com/";
    public static final Integer AID_DOUYIN = 1128;
    public static final Integer AID_XIGUA = 32;
    public static final Integer AID_TIKTOK = 1233;


    public static String getRefererUrl(Integer aid){
        switch (aid) {
            case 1128:
                return "https://www.douyin.com/";
            case 32:
                return null;
            case 1233:
                return null;
            default:
                return null;
        }
    }
    public static String getLiveAid(Integer aid){
        switch (aid) {
            case 1128:
                return "10006";
            case 32:
                return "1768";
            case 1233:
                return null;
            default:
                return null;
        }
    }

    //LivePlatformType

}
