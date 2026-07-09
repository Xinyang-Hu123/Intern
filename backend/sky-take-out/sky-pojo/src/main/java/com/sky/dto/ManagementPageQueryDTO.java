package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ManagementPageQueryDTO implements Serializable {

    private int page = 1;

    private int pageSize = 10;

    private String name;

    private Integer status;
}
