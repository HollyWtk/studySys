package com.yhh.studysys.common.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yhh.studysys.common.MenuConfig.annotation.Menu;
import com.yhh.studysys.common.enums.PermissionType;
import com.yhh.studysys.entity.SysPermission;
import com.yhh.studysys.service.ISysPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

/**
 * @author howe
 * @Desc
 * @Date: 2021/3/9 09:41
 */
@Configuration
@Slf4j
public class ControllerConfig implements BeanPostProcessor {

    private ISysPermissionService sysPermissionService;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(bean.getClass().isAnnotationPresent(RestController.class) && bean.getClass().isAnnotationPresent(Menu.class)){
            Menu menu = bean.getClass().getAnnotation(Menu.class);
            RequestMapping requestMapping = bean.getClass().getAnnotation(RequestMapping.class);
            SysPermission sysPermission = handleMenu(menu);
            String[] mappings = requestMapping.value();
            if(mappings.length != 1){
                throw new RuntimeException("请求地址只允许存在一个!");
            }
            String urlPrefix = Arrays.stream(mappings).findFirst().orElse("");
            sysPermission.setValue(StringUtils.isNotBlank(menu.url()) ? urlPrefix + menu.url() : "");
            sysPermission.setType(StringUtils.isBlank(menu.url()) ? PermissionType.CONTENT.type() : PermissionType.Menu.type());
            //插入目录/菜单 权限
            doInsertOrUpdate(sysPermission);
            Long pid = sysPermission.getId();
            Method[] declaredMethods = bean.getClass().getDeclaredMethods();
            Arrays.stream(declaredMethods).forEach(k ->{
                String value = "";
                String name = "";
                Integer type = PermissionType.BUTTON.type();
                if(k.isAnnotationPresent(GetMapping.class)){
                    GetMapping getMapping = k.getAnnotation(GetMapping.class);
                    String url = Arrays.stream(getMapping.value()).findFirst().orElse("");
                    value =  urlPrefix + url;
                    name = getMapping.name();
                }else if(k.isAnnotationPresent(PostMapping.class)){
                    PostMapping postMapping = k.getAnnotation(PostMapping.class);
                    String url = Arrays.stream(postMapping.value()).findFirst().orElse("");
                    value =  urlPrefix + url;
                    name = postMapping.name();
                }else if(k.isAnnotationPresent(PutMapping.class)){
                    PutMapping putMapping = k.getAnnotation(PutMapping.class);
                    String url = Arrays.stream(putMapping.value()).findFirst().orElse("");
                    value =  urlPrefix + url;
                    name = putMapping.name();
                }else if(k.isAnnotationPresent(DeleteMapping.class)){
                    DeleteMapping deleteMapping = k.getAnnotation(DeleteMapping.class);
                    String url = Arrays.stream(deleteMapping.value()).findFirst().orElse("");
                    value =  urlPrefix + url;
                    name = deleteMapping.name();
                }
                SysPermission buttonPermission = SysPermission.builder()
                        .value(value)
                        .pid(pid)
                        .status(1)
                        .name(name)
                        .type(type)
                        .createTime(LocalDateTime.now())
                        .build();
                if(StringUtils.isNotBlank(buttonPermission.getName()))
                    doInsertOrUpdate(buttonPermission);
            });
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if(bean instanceof ISysPermissionService){
            sysPermissionService = (ISysPermissionService) bean;
        }
        return bean;
    }

    private SysPermission handleMenu(Menu menu){
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
                .build();
        if(menu.status()){
            permission.setStatus(1);
        }
        if(StringUtils.isNotBlank(parent)){
            SysPermission parentPermission = sysPermissionService.getOne(new LambdaQueryWrapper<SysPermission>().eq(SysPermission::getName, parent));
            permission.setPid(Objects.isNull(parentPermission) ? null : parentPermission.getId());
        }else{
            permission.setPid(null);
        }
        return permission;
    }

    private void doInsertOrUpdate(SysPermission permission){
        SysPermission one = sysPermissionService.getOne(new LambdaQueryWrapper<SysPermission>().eq(SysPermission::getName, permission.getName()));
        if(Objects.isNull(one)){
            sysPermissionService.save(permission);
        }else{
            permission.setId(one.getId());
            log.warn("权限:{}已存在,覆盖之前权限",permission.getName());
            BeanUtils.copyProperties(permission,one);
            sysPermissionService.saveOrUpdate(one);
        }
    }
}
