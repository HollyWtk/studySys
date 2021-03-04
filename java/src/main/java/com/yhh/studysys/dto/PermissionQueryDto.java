package com.yhh.studysys.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author howe
 * @Desc
 * @Date: 2021/3/4 11:11
 */
@Data
@ApiModel
@AllArgsConstructor
@NoArgsConstructor
public class PermissionQueryDto {

    private String name;

    private int status;
}
