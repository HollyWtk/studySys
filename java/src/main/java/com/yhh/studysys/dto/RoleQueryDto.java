package com.yhh.studysys.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

/**
 * @author howe
 * @Desc
 * @Date: 2021/3/3 10:58
 */
@Data
@ApiModel
@AllArgsConstructor
public class RoleQueryDto {

    @ApiModelProperty(value = "角色名称")
    private String name;

    @ApiModelProperty(value = "状态")
    private int status;
}
