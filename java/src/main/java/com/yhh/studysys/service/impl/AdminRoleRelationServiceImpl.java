package com.yhh.studysys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yhh.studysys.entity.AdminRoleRelation;
import com.yhh.studysys.mapper.AdminRoleRelationMapper;
import com.yhh.studysys.service.IAdminRoleRelationService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 后台用户和角色关系表 服务实现类
 * </p>
 *
 * @author yhh
 * @since 2021-02-25
 */
@Service
public class AdminRoleRelationServiceImpl extends ServiceImpl<AdminRoleRelationMapper, AdminRoleRelation> implements IAdminRoleRelationService {

    @Override
    public List<AdminRoleRelation> queryRoleByAdminId(Long adminId) {
        return this.list(new LambdaQueryWrapper<AdminRoleRelation>().eq(AdminRoleRelation::getAdminId, adminId));
    }
}
