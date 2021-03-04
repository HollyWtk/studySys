package com.yhh.studysys.service;

import com.yhh.studysys.entity.AdminPermissionRelation;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yhh.studysys.entity.SysPermission;

import java.util.List;

/**
 * <p>
 * 后台用户和权限关系表(除角色中定义的权限以外的加减权限) 服务类
 * </p>
 *
 * @author yhh
 * @since 2021-02-25
 */
public interface IAdminPermissionRelationService extends IService<AdminPermissionRelation> {

    List<SysPermission> queryPermissionsByAdminId(Long adminId);
}
