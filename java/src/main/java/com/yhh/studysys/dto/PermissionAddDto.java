package com.yhh.studysys.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author howe
 * @Desc
 * @Date: 2021/3/4 15:20
 */
@ApiModel
@Data
public class PermissionAddDto {

    /**
     * 父级权限id
     */
    @ApiModelProperty(value = "父级权限id")
    private Long pid;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 图标
     */
    @ApiModelProperty(value = "图标")
    private String icon;

    /**
     * 权限类型：0->目录；1->菜单；2->按钮（接口绑定权限）
     */
    @ApiModelProperty(value = "类型")
    private Integer type;

    /**
     * 前端资源路径
     */
    @ApiModelProperty(value = "前端资源路径")
    private String uri;

    /**
     * 启用状态；0->禁用；1->启用
     */
    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "路由")
    private String pageIndex;

    @ApiModelProperty("接口地址")
    private String value;

}
