package com.sky.controller.user;

import com.sky.entity.Seat;
import com.sky.result.Result;
import com.sky.service.SeatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** C 端扫码点餐的座位查询接口。 */
@RestController("userSeatController")
@RequestMapping("/user/seat")
@Api(tags = "C端-扫码座位接口")
public class SeatController {

    @Autowired
    private SeatService seatService;

    @GetMapping("/layout")
    @ApiOperation("查询餐厅座位布局")
    public Result<List<Seat>> layout() {
        return Result.success(seatService.list(null));
    }

    @GetMapping("/scan/{seatNumber}")
    @ApiOperation("扫码校验桌号")
    public Result<Seat> scan(@PathVariable String seatNumber) {
        return Result.success(seatService.getAvailableBySeatNumber(seatNumber));
    }
}
