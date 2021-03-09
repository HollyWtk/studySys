package com.yhh.studysys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yhh.studysys.common.AdminUserDetails;
import com.yhh.studysys.common.config.BusinessException;
import com.yhh.studysys.dto.AdminQueryDto;
import com.yhh.studysys.dto.AdminRegisterDto;
import com.yhh.studysys.dto.AdminUpdateDto;
import com.yhh.studysys.entity.AdminRoleRelation;
import com.yhh.studysys.entity.SysAdmin;
import com.yhh.studysys.entity.SysPermission;
import com.yhh.studysys.entity.SysRole;
import com.yhh.studysys.mapper.SysAdminMapper;
import com.yhh.studysys.security.util.JwtTokenUtil;
import com.yhh.studysys.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 后台用户表 服务实现类
 * </p>
 *
 * @author yhh
 * @since 2021-02-25
 */
@Service
@Slf4j
public class SysAdminServiceImpl extends ServiceImpl<SysAdminMapper, SysAdmin> implements ISysAdminService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private IAdminRoleRelationService adminRoleRelationService;

    @Autowired
    private ISysRoleService sysRoleService;

    @Autowired
    private IRolePermissionRelationService rolePermissionRelationService;


    @Override
    public UserDetails loadUserByUsername(String username) {
        //获取用户信息
        SysAdmin sysAdmin = this.getOne(new LambdaQueryWrapper<SysAdmin>().eq(SysAdmin::getUsername, username));
        if (sysAdmin != null) {
            List<AdminRoleRelation> adminRoleRelations = adminRoleRelationService.queryRoleByAdminId(sysAdmin.getId());
            List<SysPermission> permissionList = rolePermissionRelationService.queryPermissionByRole(
                    Objects.requireNonNull(adminRoleRelations.stream().findFirst().orElse(null)).getRoleId());
            return new AdminUserDetails(sysAdmin,permissionList);
        }
        throw new UsernameNotFoundException("用户名或密码错误");
    }

    @Override
    public SysAdmin getAdminByCondition(AdminQueryDto dto) {
        LambdaQueryWrapper<SysAdmin> wrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotBlank(dto.getUsername())){
            wrapper.like(SysAdmin::getUsername,dto.getUsername());
        }
        if(Objects.nonNull(dto.getId())){
            wrapper.eq(SysAdmin::getId,dto.getId());
        }
        return this.getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysAdmin register(AdminRegisterDto adminRegisterDto) throws BusinessException {
        SysAdmin admin = new SysAdmin();
        BeanUtils.copyProperties(adminRegisterDto,admin);
        int count = this.count(new LambdaQueryWrapper<SysAdmin>().eq(SysAdmin::getUsername, admin.getUsername()));
        if(count > 0){
            throw new BusinessException("用户已存在");
        }
        String encodePassword = passwordEncoder.encode(admin.getPassword());
        admin.setPassword(encodePassword);
        admin.setCreateTime(LocalDateTime.now());
        this.save(admin);
        adminRoleRelationService.save(AdminRoleRelation.builder().adminId(admin.getId()).roleId(2L).build());
        return admin;
    }

    @Override
    public String login(String username, String password) throws BusinessException {
        String token = null;
        //密码需要客户端加密后传递
        try {
            UserDetails userDetails = loadUserByUsername(username);
            if(!passwordEncoder.matches(password,userDetails.getPassword())){
                throw new BusinessException("密码不正确");
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            token = jwtTokenUtil.generateToken(userDetails);
        } catch (AuthenticationException e) {
            log.warn("登录异常:{}", e.getMessage());
        }
        return token;
    }

    @Override
    public String refreshToken(String oldToken) {
        return jwtTokenUtil.refreshHeadToken(oldToken);
    }

    @Override
    public IPage<SysAdmin> list(AdminQueryDto dto, Page<SysAdmin> page) {
        LambdaQueryWrapper<SysAdmin> wrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotBlank(dto.getUsername())){
            wrapper.like(SysAdmin::getUsername,dto.getUsername());
        }
        if(StringUtils.isNotBlank(dto.getNickName())){
            wrapper.like(SysAdmin::getId,dto.getNickName());
        }
        wrapper.eq(SysAdmin::getStatus,dto.getStatus());
        return this.page(page,wrapper).convert(k ->{
            List<AdminRoleRelation> list = adminRoleRelationService.queryRoleByAdminId(k.getId());
            if(!list.isEmpty()){
                List<SysRole> roles = sysRoleService.list(new LambdaQueryWrapper<SysRole>().in(SysRole::getId,list.stream().map(AdminRoleRelation::getRoleId).collect(Collectors.toList())));
                k.setRoleName(roles.stream().map(SysRole::getName).collect(Collectors.joining(",")));
                k.setRoleIds(roles.stream().map(SysRole::getId).collect(Collectors.toList()));
            }
            return k;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(AdminUpdateDto dto) {
        SysAdmin admin = this.getById(dto.getId());
        BeanUtils.copyProperties(dto,admin);
        //删除之前角色ID
        adminRoleRelationService.remove(new LambdaQueryWrapper<AdminRoleRelation>().eq(AdminRoleRelation::getAdminId,dto.getId()));
        List<Long> roleIds = dto.getRoleIds();
        for (Long roleId : roleIds) {
            AdminRoleRelation adminRoleRelation = AdminRoleRelation.builder()
                    .roleId(roleId)
                    .adminId(dto.getId())
                    .build();
            adminRoleRelationService.save(adminRoleRelation);
        }
        return this.updateById(admin);
    }

    @Override
    public boolean delete(List<Long> ids) {
        return this.removeByIds(ids);
    }

    @Override
    public int updateRole(Long adminId, List<Long> roleIds) {
        return 0;
    }

    @Override
    public int updatePermission(Long adminId, List<Long> permissionIds) {
        return 0;
    }
}
