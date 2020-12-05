package com.website.service.service.impl;

import com.website.service.constant.Constants;
import com.website.service.dto.WyyContent;
import com.website.service.dto.WyyUserContent;
import com.website.service.entity.ReplyComment;
import com.website.service.entity.SongComment;
import com.website.service.entity.SongInfo;
import com.website.service.mapper.ReplyCommentMapper;
import com.website.service.mapper.SongCommentMapper;
import com.website.service.mapper.SongListMapper;
import com.website.service.service.NetEaseCrawlerService;
import com.website.service.service.NetEaseMusicService;
import com.website.service.utils.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;


@Service
public class NetEaseMusicServiceImpl implements NetEaseMusicService {

    @Resource
    private IdGenerator idGenerator;
    @Resource
    private SongListMapper songListMapper;
    @Resource
    private SongCommentMapper songCommentMapper;
    @Resource
    private ReplyCommentMapper replyCommentMapper;
    @Resource
    private NetEaseCrawlerService netEaseCrawlerService;


    private static final Logger LOGGER = LoggerFactory.getLogger(NetEaseMusicServiceImpl.class);

    @Async
    @Override
    public void getSongCommentsFromNetEase(List<SongInfo> songInfoList) {

        for(SongInfo songInfo:songInfoList){

            LOGGER.info("开始爬取歌曲:[{}]",songInfo.toString());
            String songId = songInfo.getSongId();
            int totalComments = Integer.valueOf(songInfo.getSongCommentCount());
            int pages = totalComments/ Constants.PER_PAGE;
            int mod = totalComments % Constants.PER_PAGE;
            if (mod != 0) {
                pages = pages + 1;
            }

            if (pages <= 20) {
                for (int i = 0; i < pages; i++) {
                    int pageNo = i;
                    List<WyyContent> comments = netEaseCrawlerService.getSongInfoByLimitAndPageNo(songId,Constants.PER_PAGE, pageNo);
                    insertIntoDB(comments,songId);
                    }
            } else {
                // 前10页
                for (int i = 0; i < 10; i++) {
                    int pageNo = i;
                    List comments = netEaseCrawlerService.getSongInfoByLimitAndPageNo(songId,Constants.PER_PAGE, pageNo);
                    insertIntoDB(comments,songId);
                }
                //2201 条数据 23 页
                for (int i = pages - 10; i < pages; i++) {
                    int pageNo = i;
                    List comments = netEaseCrawlerService.getSongInfoByLimitAndPageNo(songId,Constants.PER_PAGE, pageNo);
                    insertIntoDB(comments,songId);
                }
            }
            songInfo.setSongFlag("1");
            songListMapper.updateByPrimaryKey(songInfo);

        }
    }


    @Async
    @Override
    public void getSongTotalCommentsFromNetEase(List<SongInfo> songInfoList) {
        for(SongInfo songInfo:songInfoList){
            String songId = songInfo.getSongId();
              String total = netEaseCrawlerService.getTotalComments(songId);
              if(total !=null){
                  songInfo.setNewCommentCount(total);
                  songListMapper.updateByPrimaryKey(songInfo);
              }
        }
    }

    public void insertIntoDB(List<WyyContent> comments,String songId){
        if (!CollectionUtils.isEmpty(comments)) {
            for (WyyContent wyyContent : comments) {
                SongComment songComment = new SongComment();
                songComment.setId(idGenerator.nextIdStr());
                songComment.setSongId(songId);
                songComment.setUserName(wyyContent.getUsername());
                songComment.setUserImg(wyyContent.getUserimg());
                songComment.setContent(wyyContent.getContent());
                songComment.setCreateTime(wyyContent.getTime());
                songComment.setLikeCount(wyyContent.getLikedCount());
                if("1".equals(songCommentMapper.selectByUserNameAndTime(wyyContent.getUsername(),wyyContent.getTime()))){
                    continue;
                }
                songCommentMapper.insert(songComment);
                List repliedCommtents = wyyContent.getBeReplied();
                if(!CollectionUtils.isEmpty(repliedCommtents)){
                    for(Object reply :repliedCommtents ){
                        WyyUserContent wyyUserContent = (WyyUserContent)reply;
                        ReplyComment replyComment = new ReplyComment();
                        replyComment.setId(idGenerator.nextIdStr());
                        replyComment.setCommentId(songComment.getId());
                        replyComment.setReplyContent(wyyUserContent.getContent());
                        replyComment.setUserName(wyyUserContent.getUsername());
                        replyComment.setUserImg(wyyUserContent.getUserimg());
                        replyCommentMapper.insert(replyComment);
                    }
                }
            }
        }
    }

    @Override
    public void getSongCommentsFromNetEaseWithEncrption(List<SongInfo> songInfoList) {

    }

    @Override
    public void getSongTotalCommentsFromNetEaseWithEncrption(List<SongInfo> songInfoList) {

    }
}
