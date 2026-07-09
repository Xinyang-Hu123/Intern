package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class MemberPageQueryDTO implements Serializable {

    private int page;

    private int pageSize;

    private String name;

    private String phone;

    private Integer status;
}
