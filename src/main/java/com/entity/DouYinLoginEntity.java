package com.entity;

import java.io.Serializable;

/**
 * @Author: 七龙
 * @Date: 2022/6/8 17:44
 */
public class DouYinLoginEntity implements Serializable {

    private String token;
    private String qrCode;
    private String redirectUrl;
    private Integer liveStatus;

    private String liveTitle;
    private String coverImgUrl;
    private String coverUrl;

    private String userId;
    private String nickname;
    private String uniqueId;
    private String avatarUrl;

    private String streamId;
    private String roomId;
    private String pushUrl;
    private String msg;

    @Override
    public String toString() {
        return "DouYinLoginEntity{" +
                "token='" + token + '\'' +
                ", qrCode='" + qrCode + '\'' +
                ", redirectUrl='" + redirectUrl + '\'' +
                ", liveStatus=" + liveStatus +
                ", liveTitle='" + liveTitle + '\'' +
                ", coverImgUrl='" + coverImgUrl + '\'' +
                ", coverUrl='" + coverUrl + '\'' +
                ", userId='" + userId + '\'' +
                ", nickname='" + nickname + '\'' +
                ", uniqueId='" + uniqueId + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", streamId='" + streamId + '\'' +
                ", roomId='" + roomId + '\'' +
                ", pushUrl='" + pushUrl + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getPushUrl() {
        return pushUrl;
    }

    public void setPushUrl(String pushUrl) {
        this.pushUrl = pushUrl;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getLiveTitle() {
        return liveTitle;
    }

    public void setLiveTitle(String liveTitle) {
        this.liveTitle = liveTitle;
    }

    public String getCoverImgUrl() {
        return coverImgUrl;
    }

    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public Integer getLiveStatus() {
        return liveStatus;
    }

    public void setLiveStatus(Integer liveStatus) {
        this.liveStatus = liveStatus;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
}
