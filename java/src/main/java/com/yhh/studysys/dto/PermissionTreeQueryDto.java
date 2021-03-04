package com.yhh.studysys.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author howe
 * @Desc
 * @Date: 2021/3/2 16:52
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionTreeQueryDto {

    private String username;

    private Long roleId;
}
