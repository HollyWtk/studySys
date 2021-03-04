package com.yhh.studysys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yhh.studysys.entity.AdminPermissionRelation;
import com.yhh.studysys.entity.SysPermission;
import com.yhh.studysys.mapper.AdminPermissionRelationMapper;
import com.yhh.studysys.service.IAdminPermissionRelationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yhh.studysys.service.ISysPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 后台用户和权限关系表(除角色中定义的权限以外的加减权限) 服务实现类
 * </p>
 *
 * @author yhh
 * @since 2021-02-25
 */
@Service
public class AdminPermissionRelationServiceImpl extends ServiceImpl<AdminPermissionRelationMapper, AdminPermissionRelation> implements IAdminPermissionRelationService {

    @Autowired
    private ISysPermissionService sysPermissionService;

    @Override
    public List<SysPermission> queryPermissionsByAdminId(Long adminId) {
        List<Long> permissionIds = this.list(new LambdaQueryWrapper<AdminPermissionRelation>()
                .eq(AdminPermissionRelation::getAdminId, adminId))
                .stream().map(AdminPermissionRelation::getPermissionId)
                .collect(Collectors.toList());
        return permissionIds.isEmpty() ? new ArrayList<>() : sysPermissionService.list(new LambdaQueryWrapper<SysPermission>().in(SysPermission::getId,permissionIds));
    }
}
