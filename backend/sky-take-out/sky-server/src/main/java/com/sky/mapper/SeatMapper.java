package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.enumeration.OperationType;
import com.sky.dto.SeatPageQueryDTO;
import com.sky.entity.DiningSessionParticipant;
import com.sky.entity.Seat;
import com.sky.vo.SeatStatisticsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SeatMapper {

    /**
     * 插入座位
     */
    @AutoFill(OperationType.INSERT)
    void insert(Seat seat);

    /**
     * 分页查询座位
     */
    Page<Seat> pageQuery(SeatPageQueryDTO seatPageQueryDTO);

    /**
     * 根据id查询座位
     */
    Seat getById(@Param("id") Long id);

    /**
     * 根据座位编码查询
     */
    Seat getBySeatCode(@Param("seatCode") String seatCode);

    /**
     * 更新座位
     */
    @AutoFill(OperationType.UPDATE)
    void update(Seat seat);

    /**
     * 根据id删除座位
     */
    void deleteById(@Param("id") Long id);

    /**
     * 更新座位状态
     */
    void updateStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * 更新座位二维码版本和签名
     */
    void updateQrInfo(@Param("id") Long id, @Param("qrVersion") Integer qrVersion, @Param("qrSign") String qrSign);

    /**
     * 查询所有座位（用于布局展示）
     */
    List<Seat> listAll();

    /**
     * 按区域查询座位
     */
    List<Seat> listByArea(@Param("areaName") String areaName);

    /**
     * 统计座位数量
     */
    SeatStatisticsVO getStatistics();

    /**
     * 插入用餐会话
     */
    @AutoFill(OperationType.INSERT)
    void insertSession(com.sky.entity.DiningSession session);

    /**
     * 关闭用餐会话
     */
    void closeSession(@Param("id") Long sessionId);

    /**
     * 根据座位id查询开放会话
     */
    com.sky.entity.DiningSession getOpenSessionBySeat(@Param("seatId") Long seatId);

    /**
     * 根据id锁定座位
     */
    Seat getByIdForUpdate(@Param("id") Long id);

    /**
     * 统计用餐会话参与人数
     */
    int countParticipants(@Param("diningSessionId") Long diningSessionId);

    /**
     * 查询用户是否已加入用餐会话
     */
    int countParticipantBySessionAndUser(@Param("diningSessionId") Long diningSessionId,
                                         @Param("userId") Long userId);

    /**
     * 插入用餐会话参与者
     */
    void insertParticipant(DiningSessionParticipant participant);
}
