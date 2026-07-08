package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coupon implements Serializable {

    private static final long serialVersionUID = 1L;

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

    //创建时间
    private LocalDateTime createTime;

    //更新时间
    private LocalDateTime updateTime;

    //创建人
    private Long createUser;

    //修改人
    private Long updateUser;
}
