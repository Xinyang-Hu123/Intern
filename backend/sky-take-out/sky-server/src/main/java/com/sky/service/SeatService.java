package com.sky.service;

import com.sky.dto.SeatDTO;
import com.sky.dto.SeatPageQueryDTO;
import com.sky.entity.Seat;
import com.sky.result.PageResult;
import java.util.List;
import java.util.Map;

/**
 * 座位管理 Service
 */
public interface SeatService {

    /**
     * 新增座位
     */
    void save(SeatDTO seatDTO);

    /**
     * 分页查询
     */
    PageResult pageQuery(SeatPageQueryDTO dto);

    /**
     * 根据ID查询
     */
    Seat getById(Long id);

    /**
     * 修改座位
     */
    void update(SeatDTO seatDTO);

    /**
     * 删除座位
     */
    void deleteById(Long id);

    /**
     * 修改座位状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 查询所有座位
     */
    List<Seat> list(Integer status);

    /**
     * 座位状态统计
     */
    List<Map<String, Object>> statusCount();

    /** 校验并返回可用于扫码点餐的空闲座位。 */
    Seat getAvailableBySeatNumber(String seatNumber);

    /** 原子占用座位，防止多个顾客同时提交同一桌订单。 */
    void occupy(Long id);

    /** 释放订单占用的座位。 */
    void release(Long id);
}
