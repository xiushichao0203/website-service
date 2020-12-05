package com.website.service.service.weibo;

import com.github.pagehelper.PageHelper;
import com.website.service.entity.weibo.WeiboSendList;
import com.website.service.entity.weibo.WeiboSendListExample;
import com.website.service.entity.weibo.WeiboUser;
import com.website.service.entity.weibo.WeiboUserExample;
import com.website.service.mapper.weibo.WeiboSendListMapper;
import com.website.service.mapper.weibo.WeiboUserMapper;
import com.website.service.service.WeiboService;
import com.website.service.utils.DateTimeUtil;
import com.website.service.utils.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Service
public class SendListService {

    @Resource
    private WeiboSendListMapper sendListMapper;
    @Resource
    private WeiboUserMapper weiboUserMapper;
    @Resource
    private WeiboService weiboService;
    @Resource
    private IdGenerator idGenerator;

    public List<WeiboSendList> querySelective(String userId, String weiboId, Integer page, Integer limit, String sort, String order) {
        WeiboSendListExample example = new WeiboSendListExample();

        WeiboSendListExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(userId)){
            if(!"super0".equals(userId)){
                criteria.andUserIdEqualTo(userId);
            }
        }
        if (!StringUtils.isEmpty(weiboId)) {
            criteria.andWeiboIdLike("%" + weiboId +"%");
        }

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, limit);

        List<WeiboSendList> sendList = sendListMapper.selectByExample(example);
        if(sendList != null){

            for(WeiboSendList sl:sendList){
                String userWeiboId = sl.getWeiboId();
                WeiboUserExample weiboUserExample = new WeiboUserExample();
                WeiboUserExample.Criteria criteria1 = weiboUserExample.createCriteria();
                criteria1.andWeiboIdEqualTo(userWeiboId);
                WeiboUser weiboUser = weiboUserMapper.selectOneByExample(weiboUserExample);
                sl.setWeiboName(weiboUser.getWeiboName());
            }
        }

        return sendList;
    }


    public WeiboSendList addSendList(Map<String,Object> requestMap){
        String weiboId = (String)requestMap.get("weiboId");

        WeiboUserExample example = new WeiboUserExample();
        example.createCriteria().andWeiboIdEqualTo(weiboId);
        WeiboUser weiboUser = weiboUserMapper.selectOneByExample(example);
        if(Objects.isNull(weiboUser)){
            weiboUser = new WeiboUser();
            weiboUser.setWeiboId(weiboId);
            weiboUser.setWeiboName((String)requestMap.get("weiboName"));
            weiboUser.setDescription((String)requestMap.get("description"));
            weiboUser.setNickName((String)requestMap.get("nickName"));
            weiboUser.setAvatarHd((String)requestMap.get("avatarHd"));
            weiboUser.setCreateTime(DateTimeUtil.timeNow());
            weiboUser.setId(idGenerator.nextIdStr());
            weiboUserMapper.insertSelective(weiboUser);
        }

        String userId = (String)requestMap.get("userId");
        String userMail = (String)requestMap.get("userMail");
        String sendSwitch = (String)requestMap.get("sendSwitch");
        WeiboSendListExample example1 = new WeiboSendListExample();
        WeiboSendListExample.Criteria criteria = example1.createCriteria();
        criteria.andWeiboIdEqualTo(weiboId);
        criteria.andUserIdEqualTo(userId);

        WeiboSendList weiboSendList  = sendListMapper.selectOneByExample(example1);
        if(!Objects.isNull(weiboSendList)){
            weiboSendList.setId("9999999999");
            return weiboSendList;
        }
        weiboSendList  = new WeiboSendList();
        weiboSendList.setId(idGenerator.nextIdStr());
        weiboSendList.setUserId(userId);
        weiboSendList.setUserMail(userMail);
        weiboSendList.setWeiboId(weiboId);
        weiboSendList.setSendSwitch(sendSwitch);
        weiboSendList.setCreateTime(DateTimeUtil.timeNow());

        sendListMapper.insertSelective(weiboSendList);

        return weiboSendList;
    }



    public WeiboUser queryWeiboUser(String weiboId){
        WeiboUserExample example = new WeiboUserExample();
        example.createCriteria().andWeiboIdEqualTo(weiboId);
        WeiboUser weiboUser = weiboUserMapper.selectOneByExample(example);
        if(!Objects.isNull(weiboUser)){
            return weiboUser;
        }


        Map<String, Object> weiboUserMap = weiboService.getUserWeiboContent(weiboId);
        WeiboUser weiboUser1 = (WeiboUser)weiboUserMap.get("userInfo");
        if(!Objects.isNull(weiboUser1)){
            weiboUser1.setWeiboId(weiboId);
            return weiboUser1;
        }
        weiboUser = new WeiboUser();
        weiboUser.setWeiboId("9999999999");

        return weiboUser;
    }

    public int updateSendSwitch(String id,String sendSwitch){

        WeiboSendList sendList = new WeiboSendList();
        sendList.setId(id);
        sendList.setSendSwitch(sendSwitch);
        sendList.setUpdateTime(DateTimeUtil.timeNow());

        return sendListMapper.updateByPrimaryKeySelective(sendList);
    }
}
