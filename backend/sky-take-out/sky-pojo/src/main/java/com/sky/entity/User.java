package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    //微信用户唯一标识
    private String openid;

    //姓名
    private String name;

    //手机号
    private String phone;

    //性别 0 女 1 男
    private String sex;

    //身份证号
    private String idNumber;

    //头像
    private String avatar;

    //状态 0禁用 1启用
    private Integer status;

    //累计订单数
    private Integer totalOrderCount;

    //累计消费金额
    private java.math.BigDecimal totalAmount;

    //最后登录时间
    private LocalDateTime lastLoginTime;

    //注册时间
    private LocalDateTime createTime;
}
