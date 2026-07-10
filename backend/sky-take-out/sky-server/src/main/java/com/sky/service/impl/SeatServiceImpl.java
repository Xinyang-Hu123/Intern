package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.context.BaseContext;
import com.sky.dto.SeatDTO;
import com.sky.dto.SeatPageQueryDTO;
import com.sky.dto.SeatStatusDTO;
import com.sky.entity.DiningSession;
import com.sky.entity.Seat;
import com.sky.exception.SeatBusinessException;
import com.sky.mapper.SeatMapper;
import com.sky.result.PageResult;
import com.sky.service.SeatService;
import com.sky.vo.SeatScanResultVO;
import com.sky.vo.SeatStatisticsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class SeatServiceImpl implements SeatService {

    @Autowired
    private SeatMapper seatMapper;

    @Autowired
    private com.sky.websocket.WebSocketServer webSocketServer;

    @Value("${sky.seat.qr-secret-key}")
    private String qrSecretKey;

    @Override
    public void save(SeatDTO seatDTO) {
        Seat existSeat = seatMapper.getBySeatCode(seatDTO.getSeatCode());
        if (existSeat != null) {
            throw new SeatBusinessException("座位编码已存在: " + seatDTO.getSeatCode());
        }
        Seat seat = new Seat();
        BeanUtils.copyProperties(seatDTO, seat);
        seat.setStatus("AVAILABLE");
        seat.setQrVersion(1);
        seat.setCreateTime(LocalDateTime.now());
        seat.setUpdateTime(LocalDateTime.now());
        seat.setCreateUser(BaseContext.getCurrentId());
        seat.setUpdateUser(BaseContext.getCurrentId());
        seatMapper.insert(seat);
        generateAndSaveQrSign(seat.getId());
    }

    @Override
    public PageResult pageQuery(SeatPageQueryDTO seatPageQueryDTO) {
        PageHelper.startPage(seatPageQueryDTO.getPage(), seatPageQueryDTO.getPageSize());
        Page<Seat> page = seatMapper.pageQuery(seatPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void update(SeatDTO seatDTO) {
        Seat existSeat = seatMapper.getById(seatDTO.getId());
        if (existSeat == null) {
            throw new SeatBusinessException("座位不存在");
        }
        Seat codeSeat = seatMapper.getBySeatCode(seatDTO.getSeatCode());
        if (codeSeat != null && !codeSeat.getId().equals(seatDTO.getId())) {
            throw new SeatBusinessException("座位编码已被使用: " + seatDTO.getSeatCode());
        }
        Seat seat = new Seat();
        BeanUtils.copyProperties(seatDTO, seat);
        seat.setUpdateTime(LocalDateTime.now());
        seat.setUpdateUser(BaseContext.getCurrentId());
        seat.setStatus(existSeat.getStatus());
        seatMapper.update(seat);
    }

    @Override
    public void deleteById(Long id) {
        Seat seat = seatMapper.getById(id);
        if (seat == null) {
            throw new SeatBusinessException("座位不存在");
        }
        seat.setStatus("DISABLED");
        seat.setUpdateTime(LocalDateTime.now());
        seatMapper.update(seat);
        log.info("座位已停用: {}", seat.getSeatCode());
    }

    @Override
    public void changeStatus(SeatStatusDTO seatStatusDTO) {
        Seat seat = seatMapper.getById(seatStatusDTO.getId());
        if (seat == null) {
            throw new SeatBusinessException("座位不存在");
        }
        if ("OCCUPIED".equals(seat.getStatus()) && !"OCCUPIED".equals(seatStatusDTO.getStatus())) {
            throw new SeatBusinessException("使用中的座位需先关闭用餐会话才能停用");
        }
        seatMapper.updateStatus(seatStatusDTO.getId(), seatStatusDTO.getStatus());
    }

    @Override
    public Seat getById(Long id) { return seatMapper.getById(id); }

    @Override
    public List<Seat> listAll() { return seatMapper.listAll(); }

    @Override
    public SeatStatisticsVO getStatistics() { return seatMapper.getStatistics(); }

    @Override
    public SeatScanResultVO parseSeatByScene(String scene) {
        try {
            String[] parts = scene.split(":");
            if (parts.length != 3) {
                return SeatScanResultVO.builder().success(false).message("二维码格式无效，请联系店员").build();
            }
            String seatCode = parts[0];
            Integer qrVersion = Integer.parseInt(parts[1]);
            String sign = parts[2];
            Seat seat = seatMapper.getBySeatCode(seatCode);
            if (seat == null) {
                return SeatScanResultVO.builder().success(false).message("座位不存在，请联系店员").build();
            }
            if ("DISABLED".equals(seat.getStatus())) {
                return SeatScanResultVO.builder().success(false).message("该座位暂不可用").build();
            }
            if (!qrVersion.equals(seat.getQrVersion())) {
                return SeatScanResultVO.builder().success(false).message("二维码已失效，请重新扫码").build();
            }
            String expectedSign = calculateSign(seatCode, seat.getQrVersion());
            if (!expectedSign.equals(sign)) {
                return SeatScanResultVO.builder().success(false).message("二维码签名验证失败").build();
            }
            Long sessionId = createOrGetSession(seat.getId());
            return SeatScanResultVO.builder()
                    .success(true).message("扫码成功")
                    .seatId(seat.getId()).seatCode(seat.getSeatCode())
                    .seatName(seat.getSeatName()).areaName(seat.getAreaName())
                    .diningSessionId(sessionId).build();
        } catch (Exception e) {
            log.error("扫码解析失败", e);
            return SeatScanResultVO.builder().success(false).message("扫码解析异常").build();
        }
    }

    @Override
    public Long createOrGetSession(Long seatId) {
        DiningSession existingSession = seatMapper.getOpenSessionBySeat(seatId);
        if (existingSession != null) {
            return existingSession.getId();
        }
        Seat seat = seatMapper.getById(seatId);
        DiningSession session = DiningSession.builder()
                .seatId(seatId).status("OPEN")
                .startTime(LocalDateTime.now())
                .createTime(LocalDateTime.now()).updateTime(LocalDateTime.now()).build();
        seatMapper.insertSession(session);
        seatMapper.updateStatus(seatId, "OCCUPIED");
        return session.getId();
    }

    @Override
    public void closeSessionAndRelease(Long seatId) {
        DiningSession session = seatMapper.getOpenSessionBySeat(seatId);
        if (session != null) {
            seatMapper.closeSession(session.getId());
        }
        seatMapper.updateStatus(seatId, "AVAILABLE");
    }

    @Override
    public void regenerateQrCode(Long id) {
        Seat seat = seatMapper.getById(id);
        if (seat == null) throw new SeatBusinessException("座位不存在");
        Integer newVersion = seat.getQrVersion() + 1;
        String newSign = calculateSign(seat.getSeatCode(), newVersion);
        seatMapper.updateQrInfo(id, newVersion, newSign);
    }

    @Override
    public void occupySeat(Long seatId) {
        Seat seat = seatMapper.getById(seatId);
        if (seat == null || "DISABLED".equals(seat.getStatus())) {
            throw new SeatBusinessException("座位不可用");
        }
        if ("OCCUPIED".equals(seat.getStatus())) return;
        seatMapper.updateStatus(seatId, "OCCUPIED");
    }

    @Override
    public void releaseSeat(Long seatId) {
        Seat seat = seatMapper.getById(seatId);
        if (seat == null) return;
        if ("AVAILABLE".equals(seat.getStatus())) return;
        seatMapper.updateStatus(seatId, "AVAILABLE");
    }

    private String calculateSign(String seatCode, Integer qrVersion) {
        String data = seatCode + ":" + qrVersion + ":" + qrSecretKey;
        String hash = DigestUtils.md5DigestAsHex(data.getBytes(StandardCharsets.UTF_8));
        return hash.substring(0, Math.min(32, hash.length()));
    }

    private void generateAndSaveQrSign(Long seatId) {
        Seat seat = seatMapper.getById(seatId);
        if (seat == null) return;
        String sign = calculateSign(seat.getSeatCode(), seat.getQrVersion());
        seatMapper.updateQrInfo(seatId, seat.getQrVersion(), sign);
    }
}
