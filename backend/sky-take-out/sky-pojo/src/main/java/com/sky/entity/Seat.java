package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 座位实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Seat implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 座位编码，全局唯一，如 A01
    private String seatCode;

    // 展示名称，如 A区 1号桌
    private String seatName;

    // 区域名称
    private String areaName;

    // 建议容纳人数
    private Integer capacity;

    // 布局横坐标，范围0-100
    private BigDecimal positionX;

    // 布局纵坐标，范围0-100
    private BigDecimal positionY;

    // 座位状态: AVAILABLE-空闲, OCCUPIED-使用中, DISABLED-停用
    private String status;

    // 二维码版本，重发时递增
    private Integer qrVersion;

    // 二维码签名
    private String qrSign;

    // 同区域排序值
    private Integer sort;

    // 创建时间
    private LocalDateTime createTime;

    // 更新时间
    private LocalDateTime updateTime;

    // 创建人
    private Long createUser;

    // 修改人
    private Long updateUser;
}
