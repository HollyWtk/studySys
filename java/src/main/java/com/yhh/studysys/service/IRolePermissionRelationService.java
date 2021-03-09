package com.yhh.studysys.service;

import com.yhh.studysys.entity.RolePermissionRelation;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yhh.studysys.entity.SysPermission;

import java.util.List;

/**
 * <p>
 * 后台用户角色和权限关系表 服务类
 * </p>
 *
 * @author yhh
 * @since 2021-03-02
 */
public interface IRolePermissionRelationService extends IService<RolePermissionRelation> {

    List<SysPermission> queryPermissionByRole(Long roleId);
}
