package com.yhh.studysys.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author howe
 * @Desc
 * @Date: 2021/3/3 10:42
 */
@Data
@ApiModel
public class RoleAddDto {

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "状态")
    private int status;

    @ApiModelProperty(value = "权限ID")
    private List<Long> permissionId;
}
