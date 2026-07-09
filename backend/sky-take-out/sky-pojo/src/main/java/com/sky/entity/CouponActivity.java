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
public class CouponActivity implements Serializable {

    private Long id;

    private String name;

    private String couponType;

    private BigDecimal thresholdAmount;

    private BigDecimal discountAmount;

    private String scopeType;

    private Integer status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
