package com.yhh.studysys.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yhh.studysys.common.CommonResult;
import com.yhh.studysys.common.MenuConfig.annotation.Menu;
import com.yhh.studysys.common.config.BusinessException;
import com.yhh.studysys.dto.RoleAddDto;
import com.yhh.studysys.dto.RoleQueryDto;
import com.yhh.studysys.dto.RoleUpdateDto;
import com.yhh.studysys.entity.SysRole;
import com.yhh.studysys.service.ISysRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 后台用户角色表 前端控制器
 * </p>
 *
 * @author yhh
 * @since 2021-02-25
 */
@RestController
@RequestMapping("/role")
@Menu(title = "角色管理",index = "Role",icon = "el-icon-lx-infofill",order = 2)
public class SysRoleController {

    @Autowired
    private ISysRoleService sysRoleService;

    @ApiOperation("新增角色")
    @PostMapping("/add")
    public CommonResult<Boolean> addRole(@RequestBody RoleAddDto dto) throws BusinessException {
        return CommonResult.success(sysRoleService.saveRole(dto));
    }

    @ApiOperation("列表")
    @GetMapping("/list")
    public CommonResult<IPage<SysRole>> roleList(@RequestParam(value = "name", required = false) String name,
                                                 @RequestParam(value = "status", required = false) int status,
                                                 @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                 @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum){
        return CommonResult.success(sysRoleService.list(new Page<>(pageNum,pageSize),new RoleQueryDto(name,status)));
    }

    @ApiOperation("更改")
    @PutMapping("/update")
    public CommonResult<Boolean> update(@RequestBody RoleUpdateDto dto){
        return CommonResult.success(sysRoleService.updateRole(dto));
    }

    @ApiOperation("删除")
    @DeleteMapping("/delete")
    public CommonResult<Boolean> delete(@RequestBody List<Long> ids){
        return CommonResult.success(sysRoleService.deleteRoles(ids));
    }

    @ApiOperation("查询所有角色")
    @GetMapping("/queryAll")
    public CommonResult<List<SysRole>> queryAllRole(){
        return CommonResult.success(sysRoleService.queryAllRole());
    }
}
