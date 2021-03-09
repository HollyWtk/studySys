package com.yhh.studysys.security.component;

import com.yhh.studysys.security.config.IgnoreUrlsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author howe
 * @Desc
 * @Date: 2021/3/5 11:23
 */

public class RoleBasedVoter implements AccessDecisionVoter<Object> {

    private final List<String> urls;

    public RoleBasedVoter(List<String> urls){
        this.urls = urls;
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
        if(authentication == null) {
            return ACCESS_DENIED;
        }
        //超级管理员权限全部放开
        String username = authentication.getName();
        if (Objects.equals("admin", username)) {
            return ACCESS_GRANTED;
        }
        FilterInvocation fi = (FilterInvocation) object;
        String url = fi.getRequestUrl();
        //白名单放开权限
        List<String> whiteUrl = urls.stream().filter(url::equals).collect(Collectors.toList());
        if(!whiteUrl.isEmpty()){
            return ACCESS_GRANTED;
        }
        if(url.contains("?"))
            url = url.split("\\?")[0];
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(url::equals)
                .findAny()
                .map(s -> ACCESS_GRANTED)
                .orElse(ACCESS_DENIED);
    }

    @Override
    public boolean supports(Class clazz) {
        return true;
    }
}
