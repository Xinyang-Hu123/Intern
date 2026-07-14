package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.SeatDTO;
import com.sky.dto.SeatPageQueryDTO;
import com.sky.entity.Seat;
import com.sky.exception.BaseException;
import com.sky.mapper.SeatMapper;
import com.sky.result.PageResult;
import com.sky.service.SeatService;
import com.sky.websocket.WebSocketServer;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 座位管理 Service 实现
 */
@Service
public class SeatServiceImpl implements SeatService {

    @Autowired
    private SeatMapper seatMapper;

    @Autowired
    private WebSocketServer webSocketServer;

    @Override
    public void save(SeatDTO seatDTO) {
        Seat existing = seatMapper.getBySeatNumber(seatDTO.getSeatNumber());
        if (existing != null) {
            throw new BaseException("桌号已存在：" + seatDTO.getSeatNumber());
        }
        Seat seat = new Seat();
        BeanUtils.copyProperties(seatDTO, seat);
        seat.setCreateTime(LocalDateTime.now());
        seat.setUpdateTime(LocalDateTime.now());
        // 默认空闲状态
        if (seat.getStatus() == null) {
            seat.setStatus(0);
        }
        seatMapper.insert(seat);
        publishStatusChanged(seat.getId(), seat.getStatus());
    }

    @Override
    public PageResult pageQuery(SeatPageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(), dto.getPageSize());
        Page<Seat> page = (Page<Seat>) seatMapper.pageQuery(dto);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public Seat getById(Long id) {
        Seat seat = seatMapper.getById(id);
        if (seat == null) {
            throw new BaseException("座位不存在");
        }
        return seat;
    }

    @Override
    public void update(SeatDTO seatDTO) {
        Seat seat = seatMapper.getById(seatDTO.getId());
        if (seat == null) {
            throw new BaseException("座位不存在");
        }
        BeanUtils.copyProperties(seatDTO, seat);
        seat.setUpdateTime(LocalDateTime.now());
        seatMapper.update(seat);
        publishStatusChanged(seat.getId(), seat.getStatus());
    }

    @Override
    public void deleteById(Long id) {
        Seat seat = seatMapper.getById(id);
        if (seat == null) {
            throw new BaseException("座位不存在");
        }
        // 使用中的座位不能删除
        if (seat.getStatus() != null && seat.getStatus() == 1) {
            throw new BaseException("使用中的座位不能删除");
        }
        seatMapper.deleteById(id);
        publishStatusChanged(id, null);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Seat seat = seatMapper.getById(id);
        if (seat == null) {
            throw new BaseException("座位不存在");
        }
        if (status == null || status < 0 || status > 3) {
            throw new BaseException("座位状态不合法");
        }
        seat.setStatus(status);
        seat.setUpdateTime(LocalDateTime.now());
        seatMapper.updateStatus(seat);
        publishStatusChanged(id, status);
    }

    @Override
    public List<Seat> list(Integer status) {
        Seat query = new Seat();
        query.setStatus(status);
        return seatMapper.list(query);
    }

    @Override
    public List<Map<String, Object>> statusCount() {
        return seatMapper.countByStatus();
    }

    @Override
    public Seat getAvailableBySeatNumber(String seatNumber) {
        if (seatNumber == null || seatNumber.trim().isEmpty()) {
            throw new BaseException("桌号不能为空");
        }
        Seat seat = seatMapper.getBySeatNumber(seatNumber.trim());
        if (seat == null) {
            throw new BaseException("桌号不存在");
        }
        if (seat.getStatus() == null || seat.getStatus() != 0) {
            throw new BaseException("该座位当前不可用");
        }
        return seat;
    }

    @Override
    public void occupy(Long id) {
        if (id == null || seatMapper.occupyIfAvailable(id) != 1) {
            throw new BaseException("该座位已被占用或当前不可用");
        }
        publishStatusChanged(id, 1);
    }

    @Override
    public void release(Long id) {
        if (id == null) {
            return;
        }
        Seat seat = seatMapper.getById(id);
        if (seat != null && Integer.valueOf(1).equals(seat.getStatus())) {
            seat.setStatus(0);
            seat.setUpdateTime(LocalDateTime.now());
            seatMapper.updateStatus(seat);
            publishStatusChanged(id, 0);
        }
    }

    private void publishStatusChanged(Long seatId, Integer status) {
        Map<String, Object> event = new java.util.HashMap<>();
        event.put("type", "seat-status-changed");
        event.put("seatId", seatId);
        event.put("status", status);
        webSocketServer.sendToAllClient(JSON.toJSONString(event));
    }
}
