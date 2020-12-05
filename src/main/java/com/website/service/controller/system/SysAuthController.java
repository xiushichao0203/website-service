package com.website.service.controller.system;

import com.website.service.entity.system.SysAdmin;
import com.website.service.entity.system.SysUser;
import com.website.service.service.system.SysAdminService;
import com.website.service.service.system.SysPermissionService;
import com.website.service.service.system.SysRoleService;
import com.website.service.service.system.SysUserService;
import com.website.service.utils.LogHelper;
import com.website.service.utils.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.util.*;


@RestController
@RequestMapping("/admin/auth")
@Validated
public class SysAuthController {

    private final Log logger = LogFactory.getLog(SysAuthController.class);

    @Autowired
    private SysAdminService adminService;
    @Autowired
    private SysRoleService roleService;
    @Autowired
    private SysPermissionService permissionService;
    @Autowired
    private SysUserService userService;
    @Autowired
    private LogHelper logHelper;

    /*
     *  { username : value, password : value }
     */
    @PostMapping("/login")
    public Object login(@RequestBody String body, HttpServletRequest request) {
        String username = JacksonUtil.parseString(body, "username");
        String password = JacksonUtil.parseString(body, "password");

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return ResponseUtil.badArgument();
        }

        Subject currentUser = SecurityUtils.getSubject();
        try {
            currentUser.login(new UsernamePasswordToken(username, password));
        } catch (UnknownAccountException uae) {
            logHelper.logAuthFail("登录", "用户帐号或密码不正确");
            return ResponseUtil.fail(AdminResponseCode.ADMIN_INVALID_ACCOUNT, "用户帐号或密码不正确");
        } catch (LockedAccountException lae) {
            logHelper.logAuthFail("登录", "用户帐号已锁定不可用");
            return ResponseUtil.fail(AdminResponseCode.ADMIN_INVALID_ACCOUNT, "用户帐号已锁定不可用");

        } catch (AuthenticationException ae) {
            logHelper.logAuthFail("登录", "认证失败");
            return ResponseUtil.fail(AdminResponseCode.ADMIN_INVALID_ACCOUNT, "认证失败");
        }

        currentUser = SecurityUtils.getSubject();
        SysAdmin admin = (SysAdmin) currentUser.getPrincipal();
        admin.setLastLoginIp(IpUtil.getIpAddr(request));
        admin.setLastLoginTime(LocalDateTime.now());
        adminService.updateById(admin);

        logHelper.logAuthSucceed("登录");

        // userInfo
        Map<String, Object> adminInfo = new HashMap<String, Object>();
        adminInfo.put("nickName", admin.getUserName());
        adminInfo.put("avatar", admin.getAvatar());

        Map<Object, Object> result = new HashMap<Object, Object>();
        result.put("token", currentUser.getSession().getId());
        result.put("adminInfo", adminInfo);
        return ResponseUtil.ok(result);
    }


    @PostMapping("/register")
    public Object register(@RequestBody String body, HttpServletRequest request) {
        String username = JacksonUtil.parseString(body, "username");
        String password = JacksonUtil.parseString(body, "password");
        String userMail = JacksonUtil.parseString(body, "userMail");
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)|| StringUtils.isEmpty(userMail)) {
            return ResponseUtil.badArgument();
        }

        SysUser sysUser = userService.fingByUserNameOrUserMail(username,userMail);

        if(sysUser != null){
            String flag = sysUser.getId();
            if("1".equals(flag)){
                return ResponseUtil.fail(10002,"该用户名已被占用");
            }else{
                return ResponseUtil.fail(10002,"该邮箱已被占用");
            }
        }

        sysUser = userService.registByUserNameAndUserMail(username,userMail,password);
        if(sysUser == null){
            return ResponseUtil.fail();
        }
        return ResponseUtil.ok();

    }


    @PostMapping("/activate")
    public Object activate(@RequestBody String body, HttpServletRequest request) {
        String code = JacksonUtil.parseString(body, "checkCode");

        if (StringUtils.isEmpty(code)) {
            return ResponseUtil.badArgument();
        }

        code = URLDecoder.decode(code).replace(" ","+");

        try {
            code = DesUtil.decrypt(code);
        }catch (Exception e){
            return ResponseUtil.badArgument();
        }

        String[] param = code.split(";");

        if(!"checkCode".equals(param[0])){
            return ResponseUtil.badArgument();
        }

        if(userService.activateByUserNameOrUserMail(param)){
            return ResponseUtil.ok();
        }

        return ResponseUtil.fail();

    }



    /*
     *
     */
    @RequiresAuthentication
    @PostMapping("/logout")
    public Object logout() {
        Subject currentUser = SecurityUtils.getSubject();

        logHelper.logAuthSucceed("退出");
        currentUser.logout();
        return ResponseUtil.ok();
    }


    @RequiresAuthentication
    @GetMapping("/info")
    public Object info() {
        Subject currentUser = SecurityUtils.getSubject();
        SysAdmin admin = (SysAdmin) currentUser.getPrincipal();

        Map<String, Object> data = new HashMap<>();
        data.put("name", admin.getUserName());
        data.put("avatar", admin.getAvatar());

        String[] roleIds = admin.getRoleIds().split(";");
        Integer[] roleIdArr = new Integer[roleIds.length];
        for(int i=0;i<roleIds.length;i++){
            roleIdArr[i] = Integer.valueOf(roleIds[i]);
        }
        Set<String> roles = roleService.queryByIds(roleIdArr);
        Set<String> permissions = permissionService.queryByRoleIds(roleIdArr);
        data.put("roles", roles);
        // NOTE
        // 这里需要转换perms结构，因为对于前端而已API形式的权限更容易理解
        data.put("perms", toApi(permissions));
        return ResponseUtil.ok(data);
    }

    @Autowired
    private ApplicationContext context;

    private HashMap<String, String> systemPermissionsMap = null;

    private Collection<String> toApi(Set<String> permissions) {
        if (systemPermissionsMap == null) {
            systemPermissionsMap = new HashMap<>();
            final String basicPackage = "com.myself.crawler";
            List<Permission> systemPermissions = PermissionUtil.listPermission(context, basicPackage);
            for (Permission permission : systemPermissions) {
                String perm = permission.getRequiresPermissions().value()[0];
                String api = permission.getApi();
                systemPermissionsMap.put(perm, api);
            }
        }

        Collection<String> apis = new HashSet<>();
        for (String perm : permissions) {
            String api = systemPermissionsMap.get(perm);
            apis.add(api);

            if (perm.equals("*")) {
                apis.clear();
                apis.add("*");
                return apis;
                //                return systemPermissionsMap.values();

            }
        }
        return apis;
    }

    @GetMapping("/401")
    public Object page401() {
        return ResponseUtil.unlogin();
    }

    @GetMapping("/index")
    public Object pageIndex() {
        return ResponseUtil.ok();
    }

    @GetMapping("/403")
    public Object page403() {
        return ResponseUtil.unauthz();
    }
}
