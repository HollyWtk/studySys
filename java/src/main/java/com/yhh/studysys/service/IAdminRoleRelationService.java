package com.yhh.studysys.service;

import com.yhh.studysys.entity.AdminRoleRelation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 后台用户和角色关系表 服务类
 * </p>
 *
 * @author yhh
 * @since 2021-02-25
 */
public interface IAdminRoleRelationService extends IService<AdminRoleRelation> {

    List<AdminRoleRelation> queryRoleByAdminId(Long adminId);
}
