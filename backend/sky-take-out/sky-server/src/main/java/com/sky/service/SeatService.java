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
}
