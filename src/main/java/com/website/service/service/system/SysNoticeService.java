package com.website.service.service.system;

import com.github.pagehelper.PageHelper;
import com.website.service.entity.system.SysNotice;
import com.website.service.entity.system.SysNoticeExample;
import com.website.service.mapper.system.SysNoticeMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SysNoticeService {

    @Resource
    private SysNoticeMapper noticeMapper;


    public List<SysNotice> querySelective(String title, String content, Integer page, Integer limit, String sort, String order) {
        SysNoticeExample example = new SysNoticeExample();
        SysNoticeExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(title)) {
            criteria.andTitleLike("%" + title + "%");
        }
        if (!StringUtils.isEmpty(content)) {
            criteria.andContentLike("%" + content + "%");
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, limit);
        return noticeMapper.selectByExample(example);
    }

    public int updateById(SysNotice notice) {
        notice.setUpdateTime(LocalDateTime.now());
        return noticeMapper.updateByPrimaryKeySelective(notice);
    }

    public void deleteById(Integer id) {
        noticeMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(SysNotice notice) {
        notice.setCreateTime(LocalDateTime.now());
        notice.setUpdateTime(LocalDateTime.now());
        noticeMapper.insertSelective(notice);
    }

    public SysNotice findById(Integer id) {
        return noticeMapper.selectByPrimaryKey(id);
    }

    public void deleteByIds(List<Integer> ids) {
        SysNoticeExample example = new SysNoticeExample();
        example.or().andIdIn(ids).andDeletedEqualTo(false);
        SysNotice notice = new SysNotice();
        notice.setUpdateTime(LocalDateTime.now());
        notice.setDeleted(true);
        noticeMapper.updateByExampleSelective(notice, example);
    }
}
