package com.yhh.studysys.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author howe
 * @Desc
 * @Date: 2021/3/3 14:19
 */
@Data
@ApiModel
public class RoleUpdateDto {

    @NotNull
    @ApiModelProperty(required = true,value = "角色ID")
    private Long id;

    @ApiModelProperty(value = "角色名称")
    private String name;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "权限ID")
    private List<Long> permissionIds;
}
