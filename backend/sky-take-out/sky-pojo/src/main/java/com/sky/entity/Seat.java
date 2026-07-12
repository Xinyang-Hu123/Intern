package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 座位（桌台）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Seat implements Serializable {

    private static final long serialVersionUID = 1L;

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

    /** 状态文本 */
    private String statusText;

    /** 备注 */
    private String remark;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createUser;
    private Long updateUser;
}
