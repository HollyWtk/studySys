package com.yhh.studysys.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yhh.studysys.dto.PermissionAddDto;
import com.yhh.studysys.dto.PermissionQueryDto;
import com.yhh.studysys.dto.PermissionTreeQueryDto;
import com.yhh.studysys.dto.PermissionUpdateDto;
import com.yhh.studysys.entity.PermissionNode;
import com.yhh.studysys.entity.SysPermission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 后台用户权限表 服务类
 * </p>
 *
 * @author yhh
 * @since 2021-02-25
 */
public interface ISysPermissionService extends IService<SysPermission> {

    /**
     * 查询菜单
     * @param dto
     * @return
     */
    List<PermissionNode> queryListTree(PermissionTreeQueryDto dto);

    List<Long> getPermissionByRoleId(Long roleId);

    IPage<SysPermission> pageList(Page<SysPermission> page, PermissionQueryDto dto);

    boolean addPermission(PermissionAddDto dto);

    List<SysPermission> listContents();

    boolean updatePermission(PermissionUpdateDto dto);

    boolean deletePermission(List<Long> ids);
}
