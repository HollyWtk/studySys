package com.yhh.studysys.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author howe
 * @Desc
 * @Date: 2021/3/1 09:29
 */
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminQueryDto {

    @ApiModelProperty(value = "用户Id")
    private Long id;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "昵称")
    private String nickName;

    @ApiModelProperty(value = "状态")
    private int status;
}
