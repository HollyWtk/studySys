package com.yhh.studysys.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yhh.studysys.common.enums.PermissionType;
import com.yhh.studysys.dto.PermissionAddDto;
import com.yhh.studysys.dto.PermissionQueryDto;
import com.yhh.studysys.dto.PermissionUpdateDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yhh.studysys.dto.PermissionTreeQueryDto;
import com.yhh.studysys.entity.AdminRoleRelation;
import com.yhh.studysys.entity.PermissionNode;
import com.yhh.studysys.entity.RolePermissionRelation;
import com.yhh.studysys.entity.SysAdmin;
import com.yhh.studysys.entity.SysPermission;
import com.yhh.studysys.mapper.SysPermissionMapper;
import com.yhh.studysys.service.IAdminRoleRelationService;
import com.yhh.studysys.service.IRolePermissionRelationService;
import com.yhh.studysys.service.ISysAdminService;
import com.yhh.studysys.service.ISysPermissionService;

/**
 * <p>
 * 后台用户权限表 服务实现类
 * </p>
 *
 * @author yhh
 * @since 2021-02-25
 */
@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements ISysPermissionService {

    @Autowired
    private ISysAdminService sysAdminService;

    @Autowired
    private IAdminRoleRelationService adminRoleRelationService;

    @Autowired
    private IRolePermissionRelationService rolePermissionRelationService;

    @Override
    public List<PermissionNode> queryListTree(PermissionTreeQueryDto dto) {
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysPermission::getSort);
        LambdaQueryWrapper<RolePermissionRelation> rpWrapper = new LambdaQueryWrapper<>();
        if(!Objects.equals("admin",dto.getUsername())){
            Long userId = sysAdminService.getOne(new LambdaQueryWrapper<SysAdmin>().eq(SysAdmin::getUsername,dto.getUsername())).getId();
            List<Long> roleIds = adminRoleRelationService
                    .list(new LambdaQueryWrapper<AdminRoleRelation>().eq(AdminRoleRelation::getAdminId,userId))
                    .stream().map(AdminRoleRelation::getRoleId)
                    .collect(Collectors.toList());
            rpWrapper.in(RolePermissionRelation::getRoleId,roleIds);
        }else if(Objects.nonNull(dto.getRoleId())){
            rpWrapper.eq(RolePermissionRelation::getRoleId,dto.getRoleId());
        }
        List<Long> permissionIds = rolePermissionRelationService.list(rpWrapper).stream().map(RolePermissionRelation::getPermissionId).collect(Collectors.toList());
        List<SysPermission> list;
        if(Objects.equals("admin",dto.getUsername())){
            list = this.list();
        }else{
            list = permissionIds.isEmpty() ? new ArrayList<>() : this.list(wrapper.in(SysPermission::getId,permissionIds));
        }
        return list.stream()
                .filter(permission -> permission.getPid() == null)
                .map(permission -> covert(permission, list)).collect(Collectors.toList());
    }

    @Override
    public List<Long> getPermissionByRoleId(Long roleId) {
        List<Long> permissionIds = rolePermissionRelationService.list(new LambdaQueryWrapper<RolePermissionRelation>()
                .eq(RolePermissionRelation::getRoleId, roleId))
                .stream().map(RolePermissionRelation::getPermissionId).collect(Collectors.toList());
        if(permissionIds.isEmpty()){
            return new ArrayList<>();
        }
        return this.list(new LambdaQueryWrapper<SysPermission>()
                .in(SysPermission::getId,permissionIds).eq(SysPermission::getType, PermissionType.BUTTON.type()))
                .stream().map(SysPermission::getId)
                .collect(Collectors.toList());

    }

    @Override
    public IPage<SysPermission> pageList(Page<SysPermission> page, PermissionQueryDto dto) {
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(dto.getName())) {
            wrapper.like(SysPermission::getName,dto.getName());
        }
        wrapper.eq(SysPermission::getStatus,dto.getStatus());
        List<SysPermission> list = this.list();
        return this.page(page,wrapper).convert(k ->{
            SysPermission sysPermission = list.stream().filter(j -> k.getPid().equals(j.getId())).findFirst().orElse(null);
            k.setParentName(sysPermission == null ? null : sysPermission.getName());
            return k;
        });
    }

    @Override
    public boolean addPermission(PermissionAddDto dto) {
        SysPermission sysPermission = new SysPermission();
        BeanUtils.copyProperties(dto,sysPermission);
        sysPermission.setPid(Objects.isNull(dto.getPid()) ? 0L : dto.getPid());
        sysPermission.setCreateTime(LocalDateTime.now());
        return this.save(sysPermission);
    }

    @Override
    public List<SysPermission> listContents() {
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(SysPermission::getType,2);
        return this.list(wrapper);
    }

    @Override
    public boolean updatePermission(PermissionUpdateDto dto) {
        SysPermission sysPermission = this.getById(dto.getId());
        BeanUtils.copyProperties(dto,sysPermission);
        return this.updateById(sysPermission);
    }

    @Override
    public boolean deletePermission(List<Long> ids) {
        return this.removeByIds(ids);
    }

    /**
     * 将权限转换为带有子级的权限对象
     * 当找不到子级权限的时候map操作不会再递归调用covert
     */
    private PermissionNode covert(SysPermission permission, List<SysPermission> permissionList){
        PermissionNode node = new PermissionNode();
        BeanUtils.copyProperties(permission,node);
        List<PermissionNode> children = permissionList.stream()
                .filter(subPermission -> subPermission.getPid() != null && subPermission.getPid().equals(permission.getId()))
                .map(subPermission -> covert(subPermission,permissionList)).collect(Collectors.toList());
        node.setChildren(children);
        return node;
    }
}
