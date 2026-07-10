package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 座位VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatVO implements Serializable {

    private Long id;
    private String seatCode;
    private String seatName;
    private String areaName;
    private Integer capacity;
    private BigDecimal positionX;
    private BigDecimal positionY;
    private String status;
    private Integer qrVersion;
    private Integer sort;
    private String createTime;
}
