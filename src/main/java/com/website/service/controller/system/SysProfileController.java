package com.website.service.controller.system;

import com.website.service.entity.system.SysAdmin;
import com.website.service.entity.system.SysNotice;
import com.website.service.entity.system.SysNoticeAdmin;
import com.website.service.service.system.SysAdminService;
import com.website.service.service.system.SysNoticeAdminService;
import com.website.service.service.system.SysNoticeService;
import com.website.service.utils.JacksonUtil;
import com.website.service.utils.ResponseUtil;
import com.website.service.utils.bcrypt.BCryptPasswordEncoder;
import com.website.service.validator.Order;
import com.website.service.validator.Sort;
import com.website.service.utils.AdminResponseCode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/profile")
@Validated
public class SysProfileController {
    private final Log logger = LogFactory.getLog(SysProfileController.class);

    @Autowired
    private SysAdminService adminService;
    @Autowired
    private SysNoticeService noticeService;
    @Autowired
    private SysNoticeAdminService noticeAdminService;

    @RequiresAuthentication
    @PostMapping("/password")
    public Object create(@RequestBody String body) {
        String oldPassword = JacksonUtil.parseString(body, "oldPassword");
        String newPassword = JacksonUtil.parseString(body, "newPassword");
        if (StringUtils.isEmpty(oldPassword)) {
            return ResponseUtil.badArgument();
        }
        if (StringUtils.isEmpty(newPassword)) {
            return ResponseUtil.badArgument();
        }

        Subject currentUser = SecurityUtils.getSubject();
        SysAdmin admin = (SysAdmin) currentUser.getPrincipal();

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(oldPassword, admin.getPassword())) {
            return ResponseUtil.fail(AdminResponseCode.ADMIN_INVALID_ACCOUNT, "账号密码不对");
        }

        String encodedNewPassword = encoder.encode(newPassword);
        admin.setPassword(encodedNewPassword);

        adminService.updateById(admin);
        return ResponseUtil.ok();
    }

    private Integer getAdminId(){
        Subject currentUser = SecurityUtils.getSubject();
        SysAdmin admin = (SysAdmin) currentUser.getPrincipal();
        return admin.getId();
    }

    @RequiresAuthentication
    @GetMapping("/nnotice")
    public Object nNotice() {
        int count = noticeAdminService.countUnread(getAdminId());
        return ResponseUtil.ok(count);
    }

    @RequiresAuthentication
    @GetMapping("/lsnotice")
    public Object lsNotice(String title, String type,
                            @RequestParam(defaultValue = "1") Integer page,
                            @RequestParam(defaultValue = "10") Integer limit,
                            @Sort @RequestParam(defaultValue = "add_time") String sort,
                            @Order @RequestParam(defaultValue = "desc") String order) {
        List<SysNoticeAdmin> noticeList = noticeAdminService.querySelective(title, type, getAdminId(), page, limit, sort, order);
        return ResponseUtil.okList(noticeList);
    }

    @RequiresAuthentication
    @PostMapping("/catnotice")
    public Object catNotice(@RequestBody String body) {
        Integer noticeId = JacksonUtil.parseInteger(body, "noticeId");
        if(noticeId == null){
            return ResponseUtil.badArgument();
        }

        SysNoticeAdmin noticeAdmin = noticeAdminService.find(noticeId, getAdminId());
        if(noticeAdmin == null){
           return ResponseUtil.badArgumentValue();
        }
        // 更新通知记录中的时间
        noticeAdmin.setReadTime(LocalDateTime.now());
        noticeAdminService.update(noticeAdmin);

        // 返回通知的相关信息
        Map<String, Object> data = new HashMap<>();
        SysNotice notice = noticeService.findById(noticeId);
        data.put("title", notice.getTitle());
        data.put("content", notice.getContent());
        data.put("time", notice.getUpdateTime());
        Integer adminId = notice.getAdminId();
        if(adminId.equals(0)){
            data.put("admin", "系统");
        }
        else{
            SysAdmin admin = adminService.findById(notice.getAdminId());
            data.put("admin", admin.getUserName());
            data.put("avatar", admin.getAvatar());
        }
        return ResponseUtil.ok(data);
    }

    @RequiresAuthentication
    @PostMapping("/bcatnotice")
    public Object bcatNotice(@RequestBody String body) {
        List<Integer> ids = JacksonUtil.parseIntegerList(body, "ids");
        noticeAdminService.markReadByIds(ids, getAdminId());
        return ResponseUtil.ok();
    }

    @RequiresAuthentication
    @PostMapping("/rmnotice")
    public Object rmNotice(@RequestBody String body) {
        Integer id = JacksonUtil.parseInteger(body, "id");
        if(id == null){
            return ResponseUtil.badArgument();
        }
        noticeAdminService.deleteById(id, getAdminId());
        return ResponseUtil.ok();
    }

    @RequiresAuthentication
    @PostMapping("/brmnotice")
    public Object brmNotice(@RequestBody String body) {
        List<Integer> ids = JacksonUtil.parseIntegerList(body, "ids");
        noticeAdminService.deleteByIds(ids, getAdminId());
        return ResponseUtil.ok();
    }

}
