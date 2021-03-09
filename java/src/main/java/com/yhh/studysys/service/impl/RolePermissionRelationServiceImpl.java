package com.yhh.studysys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yhh.studysys.entity.RolePermissionRelation;
import com.yhh.studysys.entity.SysPermission;
import com.yhh.studysys.mapper.RolePermissionRelationMapper;
import com.yhh.studysys.service.IRolePermissionRelationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yhh.studysys.service.ISysPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 后台用户角色和权限关系表 服务实现类
 * </p>
 *
 * @author yhh
 * @since 2021-03-02
 */
@Service
public class RolePermissionRelationServiceImpl extends ServiceImpl<RolePermissionRelationMapper, RolePermissionRelation> implements IRolePermissionRelationService {

    @Autowired
    private ISysPermissionService sysPermissionService;

    @Override
    public List<SysPermission> queryPermissionByRole(Long roleId) {
        List<SysPermission> list;
        if(roleId == 1){
            list = sysPermissionService.list();
        }else{
            List<Long> permissionIds = this.list(new LambdaQueryWrapper<RolePermissionRelation>()
                    .eq(RolePermissionRelation::getRoleId, roleId))
                    .stream().map(RolePermissionRelation::getPermissionId).collect(Collectors.toList());
            list = permissionIds.isEmpty()
                    ? new ArrayList<>()
                    : sysPermissionService.list(new LambdaQueryWrapper<SysPermission>().in(SysPermission::getId,permissionIds));
        }
        return list;
    }
}
