package com.yhh.studysys.common.MenuConfig.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author howe
 * @Desc
 * @Date: 2021/3/1 18:05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuVo {

    private String title;

    private String icon;

    private String url;

    private String index;

}
