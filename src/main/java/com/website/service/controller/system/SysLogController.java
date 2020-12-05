package com.website.service.controller.system;

import com.website.service.annotation.RequiresPermissionsDesc;
import com.website.service.entity.system.SysLog;
import com.website.service.service.system.SysLogService;
import com.website.service.utils.ResponseUtil;
import com.website.service.validator.Order;
import com.website.service.validator.Sort;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/log")
@Validated
public class SysLogController {

    private final Log logger = LogFactory.getLog(SysLogController.class);

    @Autowired
    private SysLogService logService;

    @RequiresPermissions("admin:log:list")
    @RequiresPermissionsDesc(menu = {"系统管理", "操作日志"}, button = "查询")
    @GetMapping("/list")
    public Object list(String name,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        List<SysLog> logList = logService.querySelective(name, page, limit, sort, order);
        return ResponseUtil.okList(logList);
    }
}
