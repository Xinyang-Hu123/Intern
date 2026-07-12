package com.sky.mapper;

import com.sky.dto.SeatPageQueryDTO;
import com.sky.entity.Seat;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

/**
 * 座位 Mapper
 */
@Mapper
public interface SeatMapper {

    /**
     * 分页查询座位列表
     */
    List<Seat> pageQuery(SeatPageQueryDTO dto);

    /**
     * 根据ID查询
     */
    Seat getById(Long id);

    /**
     * 根据桌号查询
     */
    Seat getBySeatNumber(String seatNumber);

    /**
     * 新增座位
     */
    void insert(Seat seat);

    /**
     * 修改座位
     */
    void update(Seat seat);

    /**
     * 删除座位
     */
    void deleteById(Long id);

    /**
     * 修改座位状态
     */
    void updateStatus(Seat seat);

    /**
     * 查询所有座位（用于统计和小程序端）
     */
    List<Seat> list(Seat seat);

    /**
     * 座位状态统计
     */
    List<Map<String, Object>> countByStatus();
}
