package com.website.service.service;

import com.website.service.entity.SongInfo;

import java.util.List;


public interface NetEaseMusicService {
    /**
     * 根据歌曲ID获取歌曲评论信息
     * @param songInfoList
     */
    void getSongCommentsFromNetEase(List<SongInfo>  songInfoList);

    /**
     * 根据歌曲ID获取歌曲评论信息
     * @param songInfoList
     */
    void getSongCommentsFromNetEaseWithEncrption(List<SongInfo>  songInfoList);

    void getSongTotalCommentsFromNetEase(List<SongInfo>  songInfoList);

    void getSongTotalCommentsFromNetEaseWithEncrption(List<SongInfo>  songInfoList);
}
