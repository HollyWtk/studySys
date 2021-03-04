package com.yhh.studysys.common.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yhh.studysys.common.MenuConfig.annotation.Menu;
import com.yhh.studysys.common.enums.PermissionType;
import com.yhh.studysys.entity.SysPermission;
import com.yhh.studysys.service.ISysPermissionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;

/**
 * @author howe
 * @Desc
 * @Date: 2021/3/1 17:22
 */
@Component
public class MenuConfig implements BeanPostProcessor {

    private ISysPermissionService sysPermissionService;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(bean.getClass().isAnnotationPresent(Menu.class)){
            Menu menu = bean.getClass().getAnnotation(Menu.class);
            handleMenu(menu);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof ISysPermissionService){
            sysPermissionService = (ISysPermissionService) bean;
        }
        return bean;
    }

    private void handleMenu(Menu menu){
        String icon = menu.icon();
        String title = menu.title();
        String index = menu.index();
        String parent = menu.parent();
        int order = menu.order();
        SysPermission permission = SysPermission.builder()
                .icon(icon)
                .pageIndex(index.toLowerCase(Locale.ROOT))
                .uri("../components/page/" + index + ".vue")
                .name(title)
                .createTime(LocalDateTime.now())
                .sort(order)
                .type(PermissionType.Menu.type())
                .build();
        if(menu.status()){
            permission.setStatus(1);
        }
        if(StringUtils.isNotBlank(parent)){
            SysPermission parentPermission = sysPermissionService.getOne(new LambdaQueryWrapper<SysPermission>().eq(SysPermission::getName, parent));
            permission.setPid(Objects.isNull(parentPermission) ? 0L : parentPermission.getId());
        }else{
            permission.setPid(0L);
        }
        SysPermission one = sysPermissionService.getOne(new LambdaQueryWrapper<SysPermission>().eq(SysPermission::getName, title));
        if(Objects.isNull(one)){
            sysPermissionService.save(permission);
        }
    }

}
