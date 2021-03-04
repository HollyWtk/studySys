package com.yhh.studysys.entity;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author howe
 * @Desc
 * @Date: 2021/3/2 11:21
 */

public class PermissionNode extends SysPermission{
	private static final long serialVersionUID = -2536953996431879322L;
	
	@Getter
    @Setter
    private List<PermissionNode> children;
}
