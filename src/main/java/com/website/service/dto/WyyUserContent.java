package com.website.service.dto;


public class WyyUserContent {
    private String content;
    private String userimg;
    private String username;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("WyyUserContent{");
        sb.append("content='").append(content).append('\'');
        sb.append(", userimg='").append(userimg).append('\'');
        sb.append(", username='").append(username).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
