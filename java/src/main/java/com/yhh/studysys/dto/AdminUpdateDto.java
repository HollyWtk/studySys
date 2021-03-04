package com.yhh.studysys.dto;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * 后台用户表
 * </p>
 *
 * @author yhh
 * @since 2021-02-25
 */
@Data
public class AdminUpdateDto {

    @ApiModelProperty(value = "id",required = true)
    @NotNull
    private Long id;

    /**
     * 头像
     */
    @ApiModelProperty(value = "头像")
    private String icon;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    private String email;

    /**
     * 昵称
     */
    @ApiModelProperty(value = "昵称")
    private String nickName;

    /**
     * 备注信息
     */
    @ApiModelProperty(value = "备注信息")
    private String note;

    /**
     * 帐号启用状态：0->禁用；1->启用
     */
    @ApiModelProperty(value = "帐号启用状态")
    private Integer status;

    @ApiModelProperty(value = "角色ID")
    private List<Long> roleIds;

}
