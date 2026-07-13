package com.sky.service;

import com.sky.dto.SeatDTO;
import com.sky.dto.SeatPageQueryDTO;
import com.sky.dto.SeatStatusDTO;
import com.sky.entity.Seat;
import com.sky.result.PageResult;
import com.sky.vo.SeatScanResultVO;
import com.sky.vo.SeatStatisticsVO;

import java.util.List;

public interface SeatService {

    /**
     * 新增座位
     */
    Seat save(SeatDTO seatDTO);

    /**
     * 分页查询座位
     */
    PageResult pageQuery(SeatPageQueryDTO seatPageQueryDTO);

    /**
     * 编辑座位
     */
    void update(SeatDTO seatDTO);

    /**
     * 删除座位
     */
    void deleteById(Long id);

    /**
     * 启用/停用座位
     */
    void changeStatus(SeatStatusDTO seatStatusDTO);

    /**
     * 根据id查询座位
     */
    Seat getById(Long id);

    /**
     * 查询所有座位（用于布局展示）
     */
    List<Seat> listAll();

    /**
     * 获取座位统计信息
     */
    SeatStatisticsVO getStatistics();

    /**
     * 扫码解析座位（校验签名+版本）
     */
    SeatScanResultVO parseSeatByScene(String scene);

    /**
     * 确认加入座位用餐会话
     */
    SeatScanResultVO confirmSession(Long seatId);

    /**
     * 关闭用餐会话并释放座位
     */
    void closeSessionAndRelease(Long seatId);

    /**
     * 重新生成二维码
     */
    void regenerateQrCode(Long id);

    /**
     * 堂食下单后占用座位
     */
    void occupySeat(Long seatId);

    /**
     * 释放座位（订单取消/完成）
     */
    void releaseSeat(Long seatId);
}
