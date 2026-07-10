package com.sky.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 座位新增/编辑DTO
 */
@Data
public class SeatDTO implements Serializable {

    private Long id;

    private String seatCode;

    private String seatName;

    private String areaName;

    private Integer capacity;

    private BigDecimal positionX;

    private BigDecimal positionY;

    private Integer sort;
}
