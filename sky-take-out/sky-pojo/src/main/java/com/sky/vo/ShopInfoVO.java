package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopInfoVO implements Serializable {

    private String name;

    private String address;

    private String slogan;

    private String phone;

    private BigDecimal deliveryFee;

    private Integer estimatedTime;
}
