package com.website.service.service.system;

import com.github.pagehelper.PageHelper;
import com.website.service.entity.system.SysAdmin;
import com.website.service.entity.system.SysAdminExample;
import com.website.service.entity.system.SysUser;
import com.website.service.entity.system.SysUserExample;
import com.website.service.mapper.system.SysAdminMapper;
import com.website.service.mapper.system.SysUserMapper;
import com.website.service.service.MailService;
import com.website.service.utils.DateTimeUtil;
import com.website.service.utils.DesUtil;
import com.website.service.utils.IdGenerator;
import com.website.service.utils.bcrypt.BCryptPasswordEncoder;
import com.website.service.vo.UserVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SysUserService {

    @Resource
    private SysUserMapper userMapper;
    @Resource
    private SysAdminMapper adminMapper;
    @Resource
    private IdGenerator idGenerator;
    @Resource
    private MailService mailService;
    @Resource
    private TemplateEngine templateEngine;

    public SysUser findById(String userId) {
        return userMapper.selectByPrimaryKey(userId);
    }


    public SysUser fingByUserNameOrUserMail(String userName,String userMail){

        SysUserExample userExample = new SysUserExample();
        SysUserExample.Criteria criteria = userExample.createCriteria();
        criteria.andUserNameEqualTo(userName);
        SysUser sysUserName = userMapper.selectOneByExample(userExample);
        if(sysUserName != null){
            sysUserName.setId("1");
            return sysUserName;
        }

        SysUserExample userExample2 = new SysUserExample();
        SysUserExample.Criteria criteria2 = userExample2.createCriteria();
        criteria2.andMailEqualTo(userMail);
        SysUser sysUserMail = userMapper.selectOneByExample(userExample2);
        if(sysUserMail != null){
            sysUserMail.setId("2");
            return sysUserMail;
        }

        return null;
    }

    public boolean activateByUserNameOrUserMail(String[] param){

        String userName = param[1];
        String mail = param[2];

        SysUserExample sysUserExample = new SysUserExample();
        SysUserExample.Criteria criteria =  sysUserExample.createCriteria();
        criteria.andMailEqualTo(mail);
        criteria.andUserNameEqualTo(userName);

        SysUser sysUser = userMapper.selectOneByExample(sysUserExample);
        if(sysUser == null){
            return false;
        }
        sysUser.setStatus((byte)0);
        sysUser.setUpdateTime(DateTimeUtil.timeNow());
        userMapper.updateByPrimaryKeySelective(sysUser);

        SysAdminExample sysAdminExample = new SysAdminExample();
        SysAdminExample.Criteria criteria2 = sysAdminExample.createCriteria();
        criteria2.andUserNameEqualTo(userName);

        SysAdmin sysAdmin = adminMapper.selectOneByExample(sysAdminExample);
        sysAdmin.setDeleted(false);
        sysAdmin.setUpdateTime(LocalDateTime.now());

        adminMapper.updateByPrimaryKey(sysAdmin);


        return true;

    }

    public SysUser registByUserNameAndUserMail(String userName,String userMail,String password){

        String data = "checkCode;".concat(userName).concat(";").concat(userMail);

        String checkCode;
        try {
            checkCode = DesUtil.encrypt(data);
        }catch (Exception e){
            return null;
        }

        SysUser sysUser = new SysUser();
        sysUser.setId(idGenerator.nextIdStr());
        sysUser.setUserName(userName);
        sysUser.setMail(userMail);
        sysUser.setPassword(new BCryptPasswordEncoder().encode(password));
        sysUser.setStatus((byte)4);
        sysUser.setCreateTime(DateTimeUtil.timeNow());
        sysUser.setUpdateTime(DateTimeUtil.timeNow());
        sysUser.setDeleted(false);
        userMapper.insertSelective(sysUser);

        SysAdmin sysAdmin = new SysAdmin();
        sysAdmin.setUserName(userName);
        sysAdmin.setPassword(new BCryptPasswordEncoder().encode(password));
        sysAdmin.setRoleIds("1");
        sysAdmin.setDeleted(true);
        sysAdmin.setAvatar("https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        sysAdmin.setCreateTime(LocalDateTime.now());
        sysAdmin.setUpdateTime(LocalDateTime.now());
        adminMapper.insertSelective(sysAdmin);


        String subject = "【没有感情的机器人】请您激活您的账号【userName】"
                .replace("userName", sysUser.getUserName());
        String checkUrl = "http://www.mylife4myself.cn/#/activate?code=".concat(checkCode);

        Map<String,Object> dataMap =new HashMap<>(3);
        dataMap.put("email",userMail);
        dataMap.put("checkUrl",checkUrl);
        dataMap.put("createTime",DateTimeUtil.timeNow());

        Context context = new Context();
        for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
            context.setVariable(entry.getKey(), entry.getValue());
        }

        String templateContent = templateEngine.process("activateTemplate", context);


        mailService.sendMime(userName,userMail, subject, templateContent, null, false);


        return sysUser;
    }

    public UserVo findUserVoById(String userId) {
        SysUser user = findById(userId);
        UserVo userVo = new UserVo();
        userVo.setNickname(user.getNickName());
        userVo.setAvatar(user.getAvatar());
        return userVo;
    }

    public SysUser queryByOid(String openId) {
        SysUserExample example = new SysUserExample();
        example.or().andWeixinOpenidEqualTo(openId).andDeletedEqualTo(false);
        return userMapper.selectOneByExample(example);
    }

    public void add(SysUser user) {
        user.setCreateTime(DateTimeUtil.timeNow());
        user.setUpdateTime(DateTimeUtil.timeNow());
        userMapper.insertSelective(user);
    }

    public int updateById(SysUser user) {
        user.setUpdateTime(DateTimeUtil.timeNow());
        return userMapper.updateByPrimaryKeySelective(user);
    }

    public List<SysUser> querySelective(String username, String mobile, Integer page, Integer size, String sort, String order) {
        SysUserExample example = new SysUserExample();
        SysUserExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(username)) {
            criteria.andUserNameLike("%" + username + "%");
        }
        if (!StringUtils.isEmpty(mobile)) {
            criteria.andMobileEqualTo(mobile);
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, size);
        return userMapper.selectByExample(example);
    }

    public int count() {
        SysUserExample example = new SysUserExample();
        example.or().andDeletedEqualTo(false);

        return (int) userMapper.countByExample(example);
    }

    public List<SysUser> queryByUsername(String username) {
        SysUserExample example = new SysUserExample();
        example.or().andUserNameEqualTo(username).andDeletedEqualTo(false);
        return userMapper.selectByExample(example);
    }

    public boolean checkByUsername(String username) {
        SysUserExample example = new SysUserExample();
        example.or().andUserNameEqualTo(username).andDeletedEqualTo(false);
        return userMapper.countByExample(example) != 0;
    }

    public List<SysUser> queryByMobile(String mobile) {
        SysUserExample example = new SysUserExample();
        example.or().andMobileEqualTo(mobile).andDeletedEqualTo(false);
        return userMapper.selectByExample(example);
    }

    public List<SysUser> queryByOpenid(String openid) {
        SysUserExample example = new SysUserExample();
        example.or().andWeixinOpenidEqualTo(openid).andDeletedEqualTo(false);
        return userMapper.selectByExample(example);
    }

    public void deleteById(String id) {
        userMapper.logicalDeleteByPrimaryKey(id);
    }
}
