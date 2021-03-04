package com.yhh.studysys.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yhh.studysys.common.config.BusinessException;
import com.yhh.studysys.dto.RoleAddDto;
import com.yhh.studysys.dto.RoleQueryDto;
import com.yhh.studysys.dto.RoleUpdateDto;
import com.yhh.studysys.entity.SysRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 后台用户角色表 服务类
 * </p>
 *
 * @author yhh
 * @since 2021-02-25
 */
public interface ISysRoleService extends IService<SysRole> {

    boolean saveRole(RoleAddDto dto) throws BusinessException;

    IPage<SysRole> list(Page<SysRole> page, RoleQueryDto dto);

    boolean updateRole(RoleUpdateDto dto);

    boolean deleteRoles(List<Long> roleIds);

    List<SysRole> queryAllRole();
}
