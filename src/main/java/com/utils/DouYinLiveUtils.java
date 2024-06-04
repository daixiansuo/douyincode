package com.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtflys.forest.Forest;
import com.dtflys.forest.http.ForestCookie;
import com.dtflys.forest.http.ForestProxy;
import com.config.HttpConfig;
import com.entity.DouYinLoginEntity;
import org.apache.commons.codec.digest.DigestUtils;

import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author: ADMIN
 * @Date: 2022/6/9 15:24
 */
public class DouYinLiveUtils {




    /**
     * 创建抖音登陆二维码
     * @param cookieAtomic
     * @param aid
     * @return
     */
    public DouYinLoginEntity createLoginQR(AtomicReference<List<ForestCookie>> cookieAtomic, Integer aid, boolean head){


        String data0 =  Forest.get("https://ttwid.bytedance.com/ttwid/union/register/")
                .setUserAgent(HttpConfig.USER_AGENT)
                .setHost("sso.douyin.com")
                .setConnectTimeout(5000)
                .addHeader("Referer",HttpConfig.REFERER)//如果不提供就会导致登陆繁忙
                .onSaveCookie(((req, cookies) -> {
                    System.out.println(cookies.allCookies());
                    cookieAtomic.set(cookies.allCookies());
                }))
                .executeAsString();


        String host = "https://sso.douyin.com/get_qrcode/?aid="+ HttpConfig.getLiveAid(aid)+"&service=https:%2F%2Fwww.douyin.com%2Ffalcon%2Fwebcast_openpc%2Fpages%2Fdouyin_recharge%2Findex.html";
        host = "https://sso.douyin.com/get_qrcode/?next=https:%2F%2Fcreator.douyin.com%2Fcreator-micro%2Fhome&aid=2906&service=https:%2F%2Fcreator.douyin.com&is_vcd=1&f";
        String userAgent = HttpConfig.USER_AGENT;

        String data =  Forest.get(host)
                .setUserAgent(userAgent)
                .setHost("sso.douyin.com")
                .setConnectTimeout(5000)
                .addHeader("Referer",HttpConfig.REFERER)//如果不提供就会导致登陆繁忙
                .onSaveCookie(((req, cookies) -> {
                    System.out.println(cookies.allCookies());
                    cookieAtomic.set(cookies.allCookies());
                }))
                .executeAsString();

        JSONObject jsonObject = JSONObject.parseObject(data);

        if (0 == jsonObject.getInteger("error_code")){
            DouYinLoginEntity douYinLogin = new DouYinLoginEntity();
            String qr =  jsonObject.getJSONObject("data").getString("qrcode");
            if (head){
                douYinLogin.setQrCode("data:image/jpeg;base64,"+qr);
            }
            String token = jsonObject.getJSONObject("data").getString("token");
            douYinLogin.setQrCode(qr);
            douYinLogin.setToken(token);
            return douYinLogin;
        }else{
            System.out.println("获取二维码失败");
            return null;
        }

    }

    /**
     * 获取登陆状态
     *
     *         ' status
     *         ' 1=未扫
     *         ' 2=已扫
     *         ' 3=登录
     *         ' 4=取消
     *         ' 5=过期
     * @param aid
     * @param token
     * @return
     */
    public DouYinLoginEntity liveCheckQRConnect(Integer aid,String token){


        StringBuilder stringBuilder = new StringBuilder();
        //stringBuilder.append("https://sso.toutiao.com/check_qrconnect/?service=");
        //stringBuilder.append("https:%2F%2Fwww.douyin.com%2Ffalcon%2Fwebcast_openpc%2Fpages%2Fdouyin_recharge%2Findex.html");
        stringBuilder.append("https://sso.douyin.com/check_qrconnect/?service=");
        stringBuilder.append("https%3A%2F%2Fwww.douyin.com");
        stringBuilder.append("need_logo=false&is_frontier=false&device_platform=web_app&account_sdk_source=sso&sdk_version=2.1.2-beta.3");
        stringBuilder.append("&token=");
        stringBuilder.append(token);
        stringBuilder.append("&aid=1349");
          //6383

      //  stringBuilder.append(HttpConfig.getLiveAid(aid));

        String data = Forest.get(stringBuilder.toString())
                .setUserAgent(HttpConfig.USER_AGENT)
                .setConnectTimeout(5000)
                .addHeader("Referer",HttpConfig.REFERER)
                .addHeader("host","sso.douyin.com").executeAsString();
        JSONObject jsonObject = JSONObject.parseObject(data);
        Integer errCode = jsonObject.getInteger("error_code");
        if (null != errCode && 0 == errCode){
            DouYinLoginEntity douYinLogin = new DouYinLoginEntity();

            douYinLogin.setRedirectUrl(jsonObject.getJSONObject("data").getString("redirect_url") );
            douYinLogin.setLiveStatus( jsonObject.getJSONObject("data").getInteger("status") );
            return douYinLogin;
        }else{
            DouYinLoginEntity douYinLogin = new DouYinLoginEntity();
            douYinLogin.setMsg(jsonObject.getJSONObject("data").getString("description") );
            douYinLogin.setLiveStatus( -1 );
            return douYinLogin;
        }

    }

    /**
     * 重定向登陆
     * @param cookieAtomic
     * @param redirectUrl
     */
    public void autoCallBack(AtomicReference<List<ForestCookie>> cookieAtomic,String redirectUrl){
        AtomicReference<String> location = new AtomicReference<>(null);
        Forest.get(redirectUrl)
                .autoRedirects(false)
                .setUserAgent(HttpConfig.USER_AGENT)
                .setConnectTimeout(5000)
                .addHeader("Referer",HttpConfig.REFERER)
                .onLoadCookie(((req, cookies) -> {
                    cookies.addAllCookies(cookieAtomic.get());
                }))
                .onSuccess(((data, req, res) -> {
                    location.set(res.getHeaderValue("location"));
                }))
                .executeAsString();

        if (null != location.get()){
            Forest.get(location.get())
                    .autoRedirects(true)
                    .setConnectTimeout(5000)
                    .setUserAgent(HttpConfig.USER_AGENT)
                    .addHeader("Referer",HttpConfig.REFERER)
                    .onSaveCookie(((req, cookies) -> {
                        cookieAtomic.set(cookies.allCookies());
                    }))
                    .executeAsString();
        }
    }

    /**
     * 生成账号信息
     * @param cookieAtomic
     * @param aid
     */
    public DouYinLoginEntity createAccountInfo(AtomicReference<List<ForestCookie>> cookieAtomic,Integer aid){

        String url = "https://webcast.amemv.com/webcast/room/create_info/?platform=2&aid=" + aid;
        String data =  Forest.get(url)
                .setConnectTimeout(5000)
                .setUserAgent(HttpConfig.USER_API_AGENT)
                .host("webcast.amemv.com")
                .onLoadCookie(((req, cookies) -> {
                    cookies.addAllCookies(cookieAtomic.get());
                })).executeAsString();
        JSONObject jsonRes = JSONObject.parseObject(data);
        JSONObject jsonData = jsonRes.getJSONObject("data");

        String title = jsonData.getString("title");
        String coverImgUrl = jsonData.getJSONObject("cover").getJSONArray("url_list").getString(0);
        String coverUrl = jsonData.getJSONObject("cover").getString("uri");

        if (!Objects.equals(coverUrl, "")){
            DouYinLoginEntity entity = new DouYinLoginEntity();
            entity.setLiveTitle(title);
            entity.setCoverImgUrl(coverImgUrl);
            entity.setCoverUrl(coverUrl);
            return entity;
        }else{
            if (!title.equals("")){// ' 这里是因为用户是第一次直播，无法获取到直播封面，需要用户手动开播一次
                String prompts = jsonData.getString("prompts");
                String message = jsonData.getString("message");
                return null;
            }
            return null;
        }
    }

    /**
     * 获取账号信息
     * @param cookieAtomic
     * @return
     */
    public DouYinLoginEntity getUserInfo(AtomicReference<List<ForestCookie>> cookieAtomic ){

        String url = "https://creator.douyin.com/web/api/media/user/info/";
        String data = Forest.get(url)
                .setUserAgent(HttpConfig.USER_API_AGENT)
                //接口有时候不可用,重试10次
                .maxRetryCount(10)
                .setConnectTimeout(5000)
                .onLoadCookie(((req, cookies) -> {
                    cookies.addAllCookies(cookieAtomic.get());
                })).executeAsString();

        if (!Objects.equals(data,"")){
            JSONObject json = JSON.parseObject(data);
            if ( 0 == json.getInteger("status_code")){
                JSONObject userJson = json.getJSONObject("user");
                String userId = userJson.getString("uid");
                String nickname = userJson.getString("nickname");
                String uniqueId = userJson.getString("unique_id");
                if (null == uniqueId || uniqueId.equals("")){
                    uniqueId = userJson.getString("short_id");
                }
                String avatarUrl = userJson.getJSONObject("avatar_thumb").getJSONArray("url_list").getString(0);

                DouYinLoginEntity entity = new DouYinLoginEntity();
                entity.setUserId(userId);
                entity.setNickname(nickname);
                entity.setUniqueId(uniqueId);
                entity.setAvatarUrl(avatarUrl);

                return entity;
            }else{
                return null;
            }
        }else{
            return null;
        }

    }

    /**
     * 创建直播,通过API创建直播间但是可能没有流量，
     * @param cookieAtomic
     * @param aid  字节跳动的平台aid
     * @param coverUri 封面地址
     * @param title 直播间标题
     * @param other 其他参数，有的平台有特有的参数，这里传过来
     * @return
     */
    public DouYinLoginEntity createLive(AtomicReference<List<ForestCookie>> cookieAtomic,Integer aid,String coverUri,String title,String other){
        try{
            String url = "https://webcast.amemv.com/webcast/room/create/?aid=" + aid;
            String coverUrlEn = URLEncoder.encode(coverUri,"UTF-8");
            if (!Objects.equals(title,"")){
                coverUrlEn = coverUrlEn + "&title=" + URLEncoder.encode(title,"UTF-8");
            }
            //位置权限，1为禁用，0为开启
            String disableLocationPermission = "1";
            //礼物权限，2禁止，1开启
            String giftAuth = "1";
            coverUrlEn = coverUrlEn + "&disable_location_permission=" + disableLocationPermission + "&gift_auth=" + giftAuth + other;
            // 其他参数说明
            // challenge_id=话题id
            // record_screen=录制回放和高光，1禁止，0开启
            // gen_replay=是否允许观众录制，1禁止，0开启
            String stub = DigestUtils.md5Hex(coverUrlEn.getBytes("utf-8")).toUpperCase();

            String data = Forest.post(url)
                    .setConnectTimeout(5000)
                    .setUserAgent(HttpConfig.USER_API_AGENT)
                    .addHeader("Host","webcast.amemv.com")
                    .addHeader("X-SS-STUB",stub)
                    .onLoadCookie(((req, cookies) -> {
                        cookies.addAllCookies(cookieAtomic.get());
                    })).executeAsString();
            JSONObject jsonObject = JSONObject.parseObject(data);
            JSONObject data1 = jsonObject.getJSONObject("data");
            if(0 == jsonObject.getInteger("status_code")){

                //流id 用来做直播间操作使用
                String streamId = data1.getString("stream_id_str");
                //直播间id
                String roomId = data1.getString("id_str");
                //推流地址
                String pushUrl = data1.getJSONObject("stream_url").getString("rtmp_push_url");
                String msg = data1.getString("prompts") +":"+ data1.getString("message");
                DouYinLoginEntity entity = new DouYinLoginEntity();
                entity.setStreamId(streamId);
                entity.setRoomId(roomId);
                entity.setPushUrl(pushUrl);
                entity.setMsg(msg);
                return entity;
            }else{
                String msg = data1.getString("prompts") +":"+ data1.getString("message");
                DouYinLoginEntity entity = new DouYinLoginEntity();
                entity.setMsg(msg);
                return entity;

            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 直播间操作，创建完直播间需要调用一次 status传2
     * @param cookieAtomic cookies
     * @param aid 字节跳动的平台aid
     * @param roomId
     * @param streamId
     * @param status 1=直播间刚创建的状态 2=恢复/保持直播 3=暂停 4=关闭
     * @return
     */
    public boolean livePing(AtomicReference<List<ForestCookie>> cookieAtomic,Integer aid,String roomId,String streamId,int status){
        String url  = "https://webcast.amemv.com/webcast/room/ping/anchor/?room_id=" + roomId +"&status=" + status + "&stream_id=" + streamId + "&reason_no=0&aid=" + aid;
        String data = Forest.get(url)
                .setConnectTimeout(5000)
                .setUserAgent(HttpConfig.USER_API_AGENT)
                .addHeader("Host","webcast.amemv.com")
                .onLoadCookie(((req, cookies) -> {
                    cookies.addAllCookies(cookieAtomic.get());
                })).executeAsString();
        JSONObject jsonObject = JSONObject.parseObject(data);
        return 0 == jsonObject.getInteger("status_code");
    }

    /**
     * 真机获取推流
     * @param cookieAtomic
     * @param aid
     * @param deviceId PC填1  手机填devId
     * @return
     */
    public DouYinLoginEntity liveContinue(AtomicReference<List<ForestCookie>> cookieAtomic,Integer aid,String deviceId ){


        String url = "https://webcast.amemv.com/webcast/room/continue/?device_id="+deviceId+"&aid="+aid;
        String data = Forest.get(url)
                .setConnectTimeout(5000)
                .setUserAgent(HttpConfig.USER_API_AGENT)
                .addHeader("Host","webcast.amemv.com")
                .onLoadCookie(((req, cookies) -> {
                    cookies.addAllCookies(cookieAtomic.get());
                })).executeAsString();
        JSONObject jsonObject = JSONObject.parseObject(data);
        JSONObject dataJson = jsonObject.getJSONObject("data");
        Integer code = jsonObject.getInteger("status_code");
        if (0 == code){
            String liveStatus = dataJson.getString("status");
            String title = dataJson.getString("title");
            String roomId = dataJson.getString("id_str");
            String streamId = dataJson.getString("stream_id_str");
            String pushUrl = dataJson.getJSONObject("stream_url").getString("rtmp_push_url");
            DouYinLoginEntity entity = new DouYinLoginEntity();
            entity.setLiveTitle(liveStatus);
            entity.setLiveTitle(title);
            entity.setRoomId(roomId);
            entity.setStreamId(streamId);
            entity.setPushUrl(pushUrl);
            entity.setLiveStatus(0);
            return entity;
        }
        if (30003 == code){
            DouYinLoginEntity entity = new DouYinLoginEntity();
            entity.setLiveStatus(4);
            entity.setMsg("还未开播");
            return entity;
        }
        if (30005 == code){
            DouYinLoginEntity entity = new DouYinLoginEntity();
            entity.setLiveStatus(2);
            entity.setMsg("已检测到直播，但填写的DeviceId有误(与开播设备无法匹配)，请重试。");
            return entity;
        }
        DouYinLoginEntity entity = new DouYinLoginEntity();
        entity.setLiveStatus(-1);
        entity.setMsg(dataJson.getString("prompts"));
        return entity;
    }



}
