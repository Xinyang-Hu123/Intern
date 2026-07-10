package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 座位状态更新DTO
 */
@Data
public class SeatStatusDTO implements Serializable {

    private Long id;
    private String status;
}
