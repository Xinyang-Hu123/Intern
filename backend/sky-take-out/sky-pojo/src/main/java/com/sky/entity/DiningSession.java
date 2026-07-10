package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用餐会话实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiningSession implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 座位ID
    private Long seatId;

    // 会话状态: OPEN-开放, CLOSED-已关闭
    private String status;

    // 会话开始时间
    private LocalDateTime startTime;

    // 会话关闭时间
    private LocalDateTime closeTime;

    // 创建时间
    private LocalDateTime createTime;

    // 更新时间
    private LocalDateTime updateTime;
}
