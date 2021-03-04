package com.yhh.studysys.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yhh.studysys.common.config.BusinessException;
import com.yhh.studysys.dto.AdminQueryDto;
import com.yhh.studysys.dto.AdminRegisterDto;
import com.yhh.studysys.dto.AdminUpdateDto;
import com.yhh.studysys.entity.SysAdmin;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 后台用户表 服务类
 * </p>
 *
 * @author yhh
 * @since 2021-02-25
 */
public interface ISysAdminService extends IService<SysAdmin> {

    UserDetails loadUserByUsername(String username);

    /**
     * 根据用户名获取后台管理员
     */
    SysAdmin getAdminByCondition(AdminQueryDto dto);

    /**
     * 注册功能
     */
    SysAdmin register(AdminRegisterDto adminRegisterDto) throws BusinessException;

    /**
     * 登录功能
     * @param username 用户名
     * @param password 密码
     * @return 生成的JWT的token
     */
    String login(String username,String password) throws BusinessException;

    /**
     * 刷新token的功能
     * @param oldToken 旧的token
     */
    String refreshToken(String oldToken);

    /**
     * 根据用户名或昵称分页查询用户
     */
    IPage<SysAdmin> list(AdminQueryDto dto, Page<SysAdmin> page);

    /**
     * 修改指定用户信息
     * @return
     */
    boolean update(AdminUpdateDto dto);

    /**
     * 删除指定用户
     * @return
     */
    boolean delete(List<Long> id);

    /**
     * 修改用户角色关系
     */
    @Transactional
    int updateRole(Long adminId, List<Long> roleIds);

    /**
     * 修改用户的+-权限
     */
    @Transactional
    int updatePermission(Long adminId, List<Long> permissionIds);



}
