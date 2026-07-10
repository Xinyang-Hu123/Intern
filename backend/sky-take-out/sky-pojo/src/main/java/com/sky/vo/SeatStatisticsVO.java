package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 座位统计VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatStatisticsVO implements Serializable {

    // 总座位数
    private Integer totalSeats;

    // 空闲座位数
    private Integer availableSeats;

    // 使用中座位数
    private Integer occupiedSeats;

    // 停用座位数
    private Integer disabledSeats;

    // 区域列表
    private List<AreaStatVO> areas;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AreaStatVO implements Serializable {
        private String areaName;
        private Integer total;
        private Integer available;
        private Integer occupied;
    }
}
