package com.yhh.studysys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yhh.studysys.common.config.BusinessException;
import com.yhh.studysys.dto.RoleAddDto;
import com.yhh.studysys.dto.RoleQueryDto;
import com.yhh.studysys.dto.RoleUpdateDto;
import com.yhh.studysys.entity.RolePermissionRelation;
import com.yhh.studysys.entity.SysRole;
import com.yhh.studysys.mapper.SysRoleMapper;
import com.yhh.studysys.service.IRolePermissionRelationService;
import com.yhh.studysys.service.ISysPermissionService;
import com.yhh.studysys.service.ISysRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 后台用户角色表 服务实现类
 * </p>
 *
 * @author yhh
 * @since 2021-02-25
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

    @Autowired
    private IRolePermissionRelationService rolePermissionRelationService;

    @Autowired
    private ISysPermissionService sysPermissionService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveRole(RoleAddDto dto) throws BusinessException {
        String name = dto.getName();
        int count = this.count(new LambdaQueryWrapper<SysRole>().eq(SysRole::getName, name));
        if(count > 0){
            throw new BusinessException("角色名称已存在!");
        }
        SysRole role = new SysRole();
        BeanUtils.copyProperties(dto,role);
        role.setCreateTime(LocalDateTime.now());
        this.save(role);
        List<Long> permissionId = dto.getPermissionId();
        insertIntoRelation(permissionId,role.getId());
        return true;
    }

    @Override
    public IPage<SysRole> list(Page<SysRole> page, RoleQueryDto dto) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotBlank(dto.getName())){
            wrapper.like(SysRole::getName, dto.getName());
        }
        wrapper.eq(SysRole::getStatus,dto.getStatus());
        return this.page(page,wrapper).convert(k ->{
            k.setPermissionIds(sysPermissionService.getPermissionByRoleId(k.getId()));
            return k;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRole(RoleUpdateDto dto) {
        SysRole role = this.getById(dto.getId());
        BeanUtils.copyProperties(dto,role);
        this.updateById(role);
        List<Long> permissionIds = dto.getPermissionIds();
        //删除之前角色对应权限ID
        rolePermissionRelationService.remove(new LambdaQueryWrapper<RolePermissionRelation>().eq(RolePermissionRelation::getRoleId,dto.getId()));
        //重新赋予角色权限
        insertIntoRelation(permissionIds,dto.getId());
        return true;
    }

    @Override
    public boolean deleteRoles(List<Long> roleIds) {
        return this.removeByIds(roleIds);
    }

    @Override
    public List<SysRole> queryAllRole() {
        return this.list();
    }

    private void insertIntoRelation(List<Long> permissionId,Long roleId){
        List<RolePermissionRelation> relations = new ArrayList<>();
        permissionId.forEach(id ->{
            RolePermissionRelation rolePermissionRelation = new RolePermissionRelation();
            rolePermissionRelation.setRoleId(roleId);
            rolePermissionRelation.setPermissionId(id);
            relations.add(rolePermissionRelation);
        });
        rolePermissionRelationService.saveBatch(relations);
    }
}
