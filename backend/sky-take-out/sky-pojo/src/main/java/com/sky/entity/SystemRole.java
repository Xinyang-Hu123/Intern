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
public class SystemRole implements Serializable {

    private Long id;

    private String roleCode;

    private String name;

    private Integer status;

    private List<Long> menuIds;

    private LocalDateTime updateTime;
}
