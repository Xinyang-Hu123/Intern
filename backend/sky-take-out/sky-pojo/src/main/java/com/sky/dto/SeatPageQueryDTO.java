package com.sky.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 座位分页查询 DTO
 */
@Data
public class SeatPageQueryDTO implements Serializable {

    private int page;
    private int pageSize;

    /** 桌号搜索 */
    private String seatNumber;

    /** 区域筛选 */
    private String area;

    /** 状态筛选 */
    private Integer status;
}
