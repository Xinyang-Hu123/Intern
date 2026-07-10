package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 座位分页查询DTO
 */
@Data
public class SeatPageQueryDTO implements Serializable {

    // 区域名称
    private String areaName;

    // 座位状态
    private String status;

    // 页码
    private int page;

    // 每页条数
    private int pageSize;

    // 座位编码模糊查询
    private String seatCode;
}
