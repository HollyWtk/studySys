package com.yhh.studysys.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yhh.studysys.common.MenuConfig.annotation.Menu;
import com.yhh.studysys.common.CommonResult;
import com.yhh.studysys.common.config.BusinessException;
import com.yhh.studysys.dto.AdminLoginDto;
import com.yhh.studysys.dto.AdminQueryDto;
import com.yhh.studysys.dto.AdminRegisterDto;
import com.yhh.studysys.dto.AdminUpdateDto;
import com.yhh.studysys.entity.SysAdmin;
import com.yhh.studysys.service.ISysAdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 后台用户表 前端控制器
 * </p>
 *
 * @author yhh
 * @since 2021-02-25
 */
@RestController
@RequestMapping("/admin")
@Api(tags = "用户管理模块")
@Menu(icon = "el-icon-lx-people",index = "Admin",title = "用户管理",order = 1)
public class SysAdminController {

    @Autowired
    private ISysAdminService sysAdminService;

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @PostMapping(value = "/register")
    @ApiOperation("用户注册")
    public CommonResult<SysAdmin> register(@RequestBody AdminRegisterDto adminRegisterDto) throws BusinessException {
        SysAdmin admin = sysAdminService.register(adminRegisterDto);
        return Objects.isNull(admin) ? CommonResult.failed() : CommonResult.success(admin);
    }

    @ApiOperation(value = "登录以后返回token")
    @PostMapping(value = "/login")
    public CommonResult<Map<String,String>> login(@RequestBody AdminLoginDto dto) throws BusinessException {
        String token = sysAdminService.login(dto.getUsername(), dto.getPassword());
        if (token == null) {
            return CommonResult.validateFailed("用户名或密码错误");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        return CommonResult.success(tokenMap);
    }

    @ApiOperation(value = "刷新token")
    @GetMapping(value = "/refreshToken")
    public CommonResult<Map<String,String>> refreshToken(HttpServletRequest request) {
        String token = request.getHeader(tokenHeader);
        String refreshToken = sysAdminService.refreshToken(token);
        if (refreshToken == null) {
            return CommonResult.failed("token已经过期！");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", refreshToken);
        tokenMap.put("tokenHead", tokenHead);
        return CommonResult.success(tokenMap);
    }

    @ApiOperation(value = "获取当前登录用户信息")
    @GetMapping(value = "/info")
    public CommonResult<Map<String,Object>> getAdminInfo(Principal principal) {
        String username = principal.getName();
        SysAdmin umsAdmin = sysAdminService.getAdminByCondition(AdminQueryDto.builder().username(username).build());
        Map<String, Object> data = new HashMap<>();
        data.put("username", umsAdmin.getUsername());
        data.put("roles", new String[]{"TEST"});
        data.put("icon", umsAdmin.getIcon());
        return CommonResult.success(data);
    }

    @ApiOperation(value = "登出功能")
    @PostMapping(value = "/logout")
    public CommonResult<Object> logout() {
        return CommonResult.success(null);
    }

    @ApiOperation("根据用户名或姓名分页获取用户列表")
    @GetMapping(value = "/list")
    public CommonResult<IPage<SysAdmin>> list(@RequestParam(value = "userName", required = false) String userName,
                                              @RequestParam(value = "nickName", required = false) String nickName,
                                              @RequestParam(value = "status",required = false) int status,
                                              @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                              @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        IPage<SysAdmin> list = sysAdminService.list(AdminQueryDto.builder().username(userName).nickName(nickName).status(status).build(), new Page<>(pageNum, pageSize));
        return CommonResult.success(list);
    }

    @ApiOperation("修改指定用户信息")
    @PostMapping(value = "/update")
    public CommonResult<Object> update(@RequestBody AdminUpdateDto dto) {
        return sysAdminService.update(dto) ? CommonResult.success(null) : CommonResult.failed();
    }

    @ApiOperation("删除用户")
    @DeleteMapping(value = "/delete")
    public CommonResult<Object> delete(@RequestBody List<Long> ids) {
        return sysAdminService.delete(ids) ? CommonResult.success(null) : CommonResult.failed();
    }
}
