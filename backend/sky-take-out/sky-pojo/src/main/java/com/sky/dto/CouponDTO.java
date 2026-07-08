package com.sky.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CouponDTO implements Serializable {

    private Long id;

    //优惠券名称
    private String name;

    //折扣，8.5表示8.5折
    private BigDecimal discount;

    //开始时间
    private LocalDateTime startTime;

    //截止时间
    private LocalDateTime endTime;

    //状态 0禁用 1启用
    private Integer status;
}
