package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 扫码解析结果VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatScanResultVO implements Serializable {

    private Boolean success;
    private String message;
    private Long seatId;
    private String seatCode;
    private String seatName;
    private String areaName;
    private Long diningSessionId;
    private Integer capacity;
    private Integer participantCount;
    private Boolean joined;
    private Boolean full;
}
