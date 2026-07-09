package com.sky.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class MemberCommentVO implements Serializable {

    private Long id;
    private Long userId;
    private String memberName;
    private String phone;
    private Long dishId;
    private Long setmealId;
    private String goodsName;
    private String content;
    private Integer rating;
    private String images;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
