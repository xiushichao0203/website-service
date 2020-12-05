package com.website.service.service;

import java.util.List;
import java.util.Map;


public interface NetEaseCrawlerService {

    List getSongInfoByLimitAndPageNo(String songId, int limit,int pageNo);

    String getTotalComments(String songId);

    List getSongInfoByLimitAndPageNoWithEncrtption(String songId,int limit,int pageNo);

    String getTotalCommentsWithEncrtption(String songId);

    List<Map<String,Object>> getNewesttFavoriteSongInfo(String playListId);
}
