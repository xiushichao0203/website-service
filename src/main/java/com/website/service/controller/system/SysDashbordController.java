package com.website.service.controller.system;

import com.website.service.service.system.SysAdminService;
import com.website.service.service.system.SysUserService;
import com.website.service.utils.ResponseUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@RequestMapping("/admin/dashboard")
@Validated
public class SysDashbordController {
    private final Log logger = LogFactory.getLog(SysDashbordController.class);

    @Autowired
    private SysUserService userService;
    @Resource
    private SysAdminService adminService;


    @GetMapping("")
    public Object info(String userId) {
        //todo 记得改
        return ResponseUtil.ok(adminService.findAdmin(userId).get(0));
    }

}
