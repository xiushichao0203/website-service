package com.website.service.controller.weibo;

import com.website.service.annotation.RequiresPermissionsDesc;
import com.website.service.entity.weibo.WeiboSendList;
import com.website.service.entity.weibo.WeiboUser;
import com.website.service.utils.LogHelper;
import com.website.service.service.weibo.SendListService;
import com.website.service.utils.ResponseUtil;
import com.website.service.validator.Order;
import com.website.service.validator.Sort;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/weibo/sendList")
@Validated
public class WeiboSendListController {

    @Resource
    private SendListService sendListService;

    @Resource
    private LogHelper logHelper;

    private static final Logger LOGGER = LoggerFactory.getLogger(WeiboUserController.class);

    @RequiresPermissions("weibo:sendList:list")
    @RequiresPermissionsDesc(menu = {"系统管理", "管理员管理"}, button = "查询")
    @PostMapping("/list")
    public Object list(String userId,
                       String weiboId,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "id") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        LOGGER.info("[{}] 查询发送列表操作",this.getClass().getSimpleName());

        List<WeiboSendList> sendList = sendListService.querySelective(userId,weiboId, page, limit, sort, order);

        return ResponseUtil.okList(sendList);
    }

    @RequiresPermissions("weibo:sendList:add")
    @RequiresPermissionsDesc(menu = {"系统管理", "管理员管理"}, button = "添加")
    @PostMapping("/add")
    public Object add(@RequestBody Map<String,Object> requestMap) {
        LOGGER.info("[{}] 增加关注的用户",this.getClass().getSimpleName());
        String userId = (String)requestMap.get("userId");
        if(StringUtils.isEmpty(userId)){
            return ResponseUtil.fail(100000,"用户ID不能为空");
        }
        String sendSwitch = (String)requestMap.get("sendSwitch");
        if(StringUtils.isEmpty(sendSwitch)){
            return ResponseUtil.fail(100000,"是否发送邮件不能为空");
        }
        WeiboSendList weiboSendList = sendListService.addSendList(requestMap);
        return ResponseUtil.ok(weiboSendList);
    }


    @RequiresPermissions("weibo:sendList:queryWeiboUser")
    @RequiresPermissionsDesc(menu = {"系统管理", "管理员管理"}, button = "查询")
    @PostMapping("/queryWeiboUser")
    public Object queryWeiboUser(@RequestBody Map<String,Object> requestMap) {
        LOGGER.info("[{}] 查询微博用户",this.getClass().getSimpleName());
        String weiboId = (String)requestMap.get("weiboId");
        if(StringUtils.isEmpty(weiboId)){
            return ResponseUtil.fail(100000,"微博ID不能为空");
        }
       WeiboUser weiboUser = sendListService.queryWeiboUser(weiboId);
        if("9999999999".equals(weiboUser.getWeiboId())){
            return ResponseUtil.fail(100001,"查询不到对应的微博用户");
        }
        return ResponseUtil.ok(weiboUser);
    }


    @RequiresPermissions("weibo:sendList:updateSendSwitch")
    @RequiresPermissionsDesc(menu = {"系统管理", "管理员管理"}, button = "查询")
    @PostMapping("/updateSendSwitch")
    public Object updateSendSwitch(@RequestBody Map<String,Object> requestMap) {
        LOGGER.info("[{}] 更新通知状态",this.getClass().getSimpleName());
        String id = (String)requestMap.get("id");
        if(StringUtils.isEmpty(id)){
            return ResponseUtil.fail(100000,"ID不能为空");
        }
        String sendSwitch = (String)requestMap.get("sendSwitch");
        if(StringUtils.isEmpty(sendSwitch)){
            return ResponseUtil.fail(100000,"是否发送邮件不能为空");
        }

        return ResponseUtil.ok(sendListService.updateSendSwitch(id,sendSwitch));
    }

}


