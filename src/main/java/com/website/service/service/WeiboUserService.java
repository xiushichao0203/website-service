package com.website.service.service;

import com.website.service.entity.system.SysUser;

import java.util.List;


public interface WeiboUserService {

     List<SysUser> querySelective(String username, String mobile, Integer page, Integer size, String sort, String order);

}
