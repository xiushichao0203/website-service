package com.website.service.constant;

public class Constants {


    //歌单url
    public static final String SOURCE_URL = "http://music.163.com/discover/playlist/?";

    public static final String FAVORATE_SONG_LIST_URL = "https://music.163.com/playlist?id=";

    //163主域名
    public static final String DOMAIN = "http://music.163.com";
    public static final String BASE_URL = "http://music.163.com/";

    //获取评论的API路径(没被加密)
    public static final String NET_EASE_COMMENT_API_URL_NO_ENCRYPT = "http://music.163.com/api/v1/resource/comments/R_SO_4_songId?limit=limits&offset=offsets";

    //获取评论的API路径(被加密)
    public static final String NET_EASE_COMMENT_API_URL_ENCRYPT = "http://music.163.com/weapi/v1/resource/comments/R_SO_4_";

    public static final String NET_EASE_ENCRYPT_KEY = "0CoJUm6Qyw8W8jud";

    //解密用的文本
    public static final String TEXT = "{\"username\": \"\", \"rememberLogin\": \"true\", \"password\": \"\"}";

    //存储歌曲信息文本路径
    public static final String COMMENT_MESSAGE_PATH = "/home/user/workspace/NetEaseMusicCrawler/log/comment_message.xls";

    //存储评论内容文本路径
    public static final String COMMENTS_PATH = "/home/user/workspace/NetEaseMusicCrawler/log/comments_";

    public static final String COMMENTS_SUFFIX = ".xls";

    //TOP歌曲文本路径
    public static final String TOP_MUSIC_PATH = "/home/user/workspace/NetEaseMusicCrawler/log/top_music.xls";

    //歌曲评论大于某个值文本路径
    public static final String TOP_COMMENT_MORE_MUSIC_PATH = "/home/user/workspace/NetEaseMusicCrawler/log/music_comment_gt.xls";

    //要爬取的歌单数
    public static final int MUSIC_LIST_COUNT = 100;

    //分页数
    public static final int PER_PAGE = 100;

    //偏移量
    public static final int OFFSET = 0;

    //要爬取的TOP歌曲数
    public static final int TOP_MUSIC_COUNT = 20;

    //获取评论数大于该值的歌曲
    public static final int COMMENTS_LIMIT = 100000;

    public static final String HER = "";

    public static final String YES_OR_NO_1 = "1";

    public static final String YES_OR_NO_0 = "0";
}
