package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 扫码解析DTO - 小程序扫码后传入的scene参数
 */
@Data
public class SeatScanDTO implements Serializable {

    // 座位编码
    private String seatCode;

    // 二维码版本
    private Integer qrVersion;

    // 签名
    private String sign;
}
