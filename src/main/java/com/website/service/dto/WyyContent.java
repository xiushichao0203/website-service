package com.website.service.dto;

import java.util.List;


public class WyyContent {
    private String content;
    private String likedCount;
    private String time;
    private String userimg;
    private String username;
    private List beReplied;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLikedCount() {
        return likedCount;
    }

    public void setLikedCount(String likedCount) {
        this.likedCount = likedCount;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserimg() {
        return userimg;
    }

    public void setUserimg(String userimg) {
        this.userimg = userimg;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List getBeReplied() {
        return beReplied;
    }

    public void setBeReplied(List beReplied) {
        this.beReplied = beReplied;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("WyyContent{");
        sb.append("content='").append(content).append('\'');
        sb.append(", likedCount='").append(likedCount).append('\'');
        sb.append(", time='").append(time).append('\'');
        sb.append(", userimg='").append(userimg).append('\'');
        sb.append(", username='").append(username).append('\'');
        sb.append(", beReplied=").append(beReplied);
        sb.append('}');
        return sb.toString();
    }
}
