package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemUser implements Serializable {

    private Long id;

    private String username;

    private String name;

    private String phone;

    private Integer status;

    private List<String> roles;

    private LocalDateTime updateTime;
}
