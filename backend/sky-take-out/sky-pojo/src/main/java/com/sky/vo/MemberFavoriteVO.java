package com.sky.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class MemberFavoriteVO implements Serializable {

    private Long id;
    private Long userId;
    private String memberName;
    private String phone;
    private Long dishId;
    private Long setmealId;
    private String goodsName;
    private String goodsImage;
    private LocalDateTime createTime;
}
