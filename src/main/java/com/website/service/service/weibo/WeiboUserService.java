package com.website.service.service.weibo;

import com.github.pagehelper.PageHelper;
import com.website.service.entity.weibo.WeiboUser;
import com.website.service.entity.weibo.WeiboUserExample;
import com.website.service.mapper.weibo.WeiboUserMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;


@Service
public class WeiboUserService {
    @Resource
    private WeiboUserMapper litemallWeiboUserMapper;

    public List<WeiboUser> querySelective(String userId, String userName, Integer page, Integer limit, String sort, String order) {
        WeiboUserExample example = new WeiboUserExample();

        WeiboUserExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(userName)) {
            criteria.andWeiboNameLike("%" + userName + "%");
        }

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, limit);
        return litemallWeiboUserMapper.selectByExample(example);
    }

    public int updateById(WeiboUser weiboUser) {
        return litemallWeiboUserMapper.updateByPrimaryKeySelective(weiboUser);
    }

    public void deleteById(String id) {
        litemallWeiboUserMapper.deleteByPrimaryKey(id);
    }

    public void add(WeiboUser weiboUser) {
        litemallWeiboUserMapper.insertSelective(weiboUser);
    }

    public WeiboUser findById(String id) {
        return litemallWeiboUserMapper.selectByPrimaryKey(id);
    }


}
