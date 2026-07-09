package com.sky.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MemberVO implements Serializable {

    private Long id;
    private String openid;
    private String name;
    private String phone;
    private String sex;
    private String avatar;
    private Integer status;
    private Integer totalOrderCount;
    private BigDecimal totalAmount;
    private Integer commentCount;
    private Integer favoriteCount;
    private LocalDateTime lastLoginTime;
    private LocalDateTime createTime;
}
