package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class MemberCommentPageQueryDTO implements Serializable {

    private int page;

    private int pageSize;

    private String memberName;

    private String goodsName;

    private Integer rating;

    private Integer status;
}
