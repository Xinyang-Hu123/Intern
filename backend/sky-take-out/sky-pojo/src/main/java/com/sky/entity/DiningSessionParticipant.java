package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用餐会话参与者实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiningSessionParticipant implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 用餐会话ID
    private Long diningSessionId;

    // 小程序用户ID
    private Long userId;

    // 确认加入时间
    private LocalDateTime createTime;
}
