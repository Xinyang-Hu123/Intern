package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 菜品
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dish implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    //菜品名称
    private String name;

    //菜品分类id
    private Long categoryId;

    //菜品价格
    private BigDecimal price;

    //图片
    private String image;

    //详情描述图
    private String detailImage;

    //商品多图，逗号分隔
    private String images;

    //描述信息
    private String description;

    //排序，数值越小越靠前
    private Integer sort;

    //是否推荐首页 0否 1是
    private Integer recommend;

    //是否展示在栏目主页 0否 1是
    private Integer columnShow;

    //0 停售 1 起售
    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long createUser;

    private Long updateUser;

}
