package com.sky.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class CouponPageQueryDTO implements Serializable {

    //页码
    private int page;

    //每页记录数
    private int pageSize;

    //优惠券名称
    private String name;

    //状态 0禁用 1启用
    private Integer status;

    //有效期查询开始
    private LocalDateTime startTime;

    //有效期查询结束
    private LocalDateTime endTime;
}
