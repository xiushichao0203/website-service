package com.website.service.controller.weibo;

import com.website.service.entity.weibo.WeiboUser;
import com.website.service.service.weibo.WeiboUserService;
import com.website.service.utils.ResponseUtil;
import com.website.service.validator.Order;
import com.website.service.validator.Sort;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/admin/weibo/user")
//@Validated
public class AdminWeiboUserController {

    private final Log logger = LogFactory.getLog(WeiboUserController.class);

    @Resource
    private WeiboUserService weiboUserService;

//    @RequiresPermissions("admin:user:list")
//    @RequiresPermissionsDesc(menu = {"用户管理", "会员管理"}, button = "查询")
    @GetMapping("/list")
    public Object list(String username, String mobile,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "id") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        List<WeiboUser> userList = weiboUserService.querySelective(username, mobile, page, limit, sort, order);
        return ResponseUtil.okList(userList);
    }
}
