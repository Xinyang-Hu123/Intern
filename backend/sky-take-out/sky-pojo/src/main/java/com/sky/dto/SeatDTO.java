package com.sky.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 座位 DTO
 */
@Data
public class SeatDTO implements Serializable {

    private Long id;

    /** 桌号/座位编号 */
    private String seatNumber;

    /** 座位名称 */
    private String name;

    /** 容纳人数 */
    private Integer capacity;

    /** 区域 */
    private String area;

    /** 二维码路径 */
    private String qrCode;

    /** 状态: 0空闲 1使用中 2已预订 3清洁中 */
    private Integer status;

    /** 备注 */
    private String remark;
}
