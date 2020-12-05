package com.website.service.service.system;

import com.github.pagehelper.PageHelper;
import com.website.service.entity.system.SysAdmin;
import com.website.service.entity.system.SysAdmin.Column;
import com.website.service.entity.system.SysAdminExample;
import com.website.service.mapper.system.SysAdminMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SysAdminService {

    private final Column[] result = new Column[]{SysAdmin.Column.id, SysAdmin.Column.userName, SysAdmin.Column.avatar, SysAdmin.Column.roleIds};
    @Resource
    private SysAdminMapper adminMapper;

    public List<SysAdmin> findAdmin(String username) {
        SysAdminExample example = new SysAdminExample();
        example.or().andUserNameEqualTo(username).andDeletedEqualTo(false);
        return adminMapper.selectByExample(example);
    }

    public SysAdmin findAdmin(Integer id) {
        return adminMapper.selectByPrimaryKey(id);
    }

    public List<SysAdmin> querySelective(String username, Integer page, Integer limit, String sort, String order) {
        SysAdminExample example = new SysAdminExample();
        SysAdminExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(username)) {
            criteria.andUserNameLike("%" + username + "%");
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, limit);
        return adminMapper.selectByExampleSelective(example, result);
    }

    public int updateById(SysAdmin admin) {
        admin.setUpdateTime(LocalDateTime.now());
        return adminMapper.updateByPrimaryKeySelective(admin);
    }

    public void deleteById(Integer id) {
        adminMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(SysAdmin admin) {
        admin.setCreateTime(LocalDateTime.now());
        admin.setUpdateTime(LocalDateTime.now());
        adminMapper.insertSelective(admin);
    }

    public SysAdmin findById(Integer id) {
        return adminMapper.selectByPrimaryKeySelective(id, result);
    }

    public List<SysAdmin> all() {
        SysAdminExample example = new SysAdminExample();
        example.or().andDeletedEqualTo(false);
        return adminMapper.selectByExample(example);
    }
}
