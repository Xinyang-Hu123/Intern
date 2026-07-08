package com.sky.mapper;

import com.sky.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminMapper {

    /**
     * 根据用户名查询管理员
     * @param username
     * @return
     */
    Admin getByUsername(@Param("username") String username);
}
