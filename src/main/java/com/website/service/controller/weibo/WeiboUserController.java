package com.website.service.controller.weibo;

import com.website.service.annotation.RequiresPermissionsDesc;
import com.website.service.entity.weibo.WeiboUser;
import com.website.service.service.weibo.WeiboUserService;
import com.website.service.utils.LogHelper;
import com.website.service.utils.ResponseUtil;
import com.website.service.validator.Order;
import com.website.service.validator.Sort;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/weibo/user")
@Validated
public class WeiboUserController {
    @Resource
    private WeiboUserService weiboUserService;

    @Resource
    private LogHelper logHelper;

    private static final Logger LOGGER = LoggerFactory.getLogger(WeiboUserController.class);

    @RequiresPermissions("weibo:user:list")
    @RequiresPermissionsDesc(menu = {"系统管理", "管理员管理"}, button = "查询")
    @PostMapping("/list")
    public Object list(String username,
                       String userId,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "id") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        LOGGER.info("[{}] 查询微博用户操作",this.getClass().getSimpleName());
        List<WeiboUser> weiboUserList = weiboUserService.querySelective(userId,username, page, limit, sort, order);

        return ResponseUtil.okList(weiboUserList);
    }
}
