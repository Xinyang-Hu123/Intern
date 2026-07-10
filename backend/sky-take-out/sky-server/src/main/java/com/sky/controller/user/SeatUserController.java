package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.service.SeatService;
import com.sky.vo.SeatScanResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户端-座位扫码相关接口
 */
@RestController("userSeatController")
@RequestMapping("/user/seat")
@Slf4j
@Api(tags = "用户端-座位扫码接口")
public class SeatUserController {

    @Autowired
    private SeatService seatService;

    /**
     * 扫码解析座位（小程序扫码后调用）
     */
    @PostMapping("/scan")
    @ApiOperation("扫码解析座位")
    public Result<SeatScanResultVO> scanSeat(@RequestBody String scene) {
        log.info("扫码解析: {}", scene);
        SeatScanResultVO result = seatService.parseSeatByScene(scene);
        return Result.success(result);
    }

    /**
     * 创建或获取用餐会话
     */
    @PostMapping("/session/create")
    @ApiOperation("创建或获取用餐会话")
    public Result<Long> createSession(@RequestParam Long seatId) {
        Long sessionId = seatService.createOrGetSession(seatId);
        return Result.success(sessionId);
    }

    /**
     * 关闭用餐会话并释放座位
     */
    @PostMapping("/session/close")
    @ApiOperation("关闭用餐会话并释放座位")
    public Result closeSession(@RequestParam Long seatId) {
        seatService.closeSessionAndRelease(seatId);
        return Result.success();
    }

    /**
     * 占用座位（堂食下单后）
     */
    @PostMapping("/occupy")
    @ApiOperation("占用座位")
    public Result occupySeat(@RequestParam Long seatId) {
        seatService.occupySeat(seatId);
        return Result.success();
    }

    /**
     * 释放座位（订单取消/完成后）
     */
    @PostMapping("/release")
    @ApiOperation("释放座位")
    public Result releaseSeat(@RequestParam Long seatId) {
        seatService.releaseSeat(seatId);
        return Result.success();
    }
}
