package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DishPageQueryDTO implements Serializable {

    private int page;

    private int pageSize;

    private String name;

    //分类id
    private Integer categoryId;

    //状态 0表示禁用 1表示启用
    private Integer status;

    //是否推荐首页 0否 1是
    private Integer recommend;

    //是否展示在栏目主页 0否 1是
    private Integer columnShow;

}
