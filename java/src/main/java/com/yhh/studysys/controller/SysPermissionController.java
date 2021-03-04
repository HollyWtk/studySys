package com.yhh.studysys.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yhh.studysys.common.CommonResult;
import com.yhh.studysys.common.MenuConfig.annotation.Menu;
import com.yhh.studysys.dto.PermissionAddDto;
import com.yhh.studysys.dto.PermissionQueryDto;
import com.yhh.studysys.dto.PermissionTreeQueryDto;
import com.yhh.studysys.dto.PermissionUpdateDto;
import com.yhh.studysys.entity.PermissionNode;
import com.yhh.studysys.entity.SysPermission;
import com.yhh.studysys.service.ISysPermissionService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * <p>
 * 后台用户权限表 前端控制器
 * </p>
 *
 * @author yhh
 * @since 2021-02-25
 */
@RestController
@RequestMapping("/permission")
@Menu(icon = "el-icon-lx-lock",index = "Permission",title = "权限管理",order = 3)
public class SysPermissionController {

    @Autowired
    private ISysPermissionService sysPermissionService;

    @GetMapping("/menuList")
    @ApiOperation("查询菜单")
    public CommonResult<List<PermissionNode>> getPermission(Principal principal, @RequestParam(required = false) Long roleId){
        PermissionTreeQueryDto dto = new PermissionTreeQueryDto(principal.getName(),roleId);
        return CommonResult.success(sysPermissionService.queryListTree(dto));
    }

    @GetMapping("/list")
    @ApiOperation("权限列表")
    public CommonResult<IPage<SysPermission>> roleList(@RequestParam(value = "name", required = false) String name,
                                                       @RequestParam(value = "status",required = false) int status,
                                                       @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                       @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum){
        return CommonResult.success(sysPermissionService.pageList(new Page<>(pageNum,pageSize),new PermissionQueryDto(name,status)));
    }

    @GetMapping("/listContents")
    @ApiOperation("目录下拉框")
    public CommonResult<List<SysPermission>> listContents(){
        return CommonResult.success(sysPermissionService.listContents());
    }

    @PostMapping("/add")
    @ApiOperation("添加")
    public CommonResult<Boolean> add(@RequestBody PermissionAddDto dto){
        return CommonResult.success(sysPermissionService.addPermission(dto));
    }

    @PutMapping("/update")
    @ApiOperation("编辑")
    public CommonResult<Boolean> update(@RequestBody PermissionUpdateDto dto){
        return CommonResult.success(sysPermissionService.updatePermission(dto));
    }

    @ApiOperation("删除")
    @DeleteMapping("/delete")
    public CommonResult<Boolean> delete(@RequestBody List<Long> ids){
        return CommonResult.success(sysPermissionService.deletePermission(ids));
    }

}
