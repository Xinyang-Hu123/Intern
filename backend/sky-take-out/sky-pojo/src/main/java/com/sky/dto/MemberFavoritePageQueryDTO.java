package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class MemberFavoritePageQueryDTO implements Serializable {

    private int page;

    private int pageSize;

    private String memberName;

    private String goodsName;
}
