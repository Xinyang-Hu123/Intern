package com.sky.controller.admin;

import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SeatService;
import com.sky.dto.SeatDTO;
import com.sky.dto.SeatPageQueryDTO;
import com.sky.dto.SeatStatusDTO;
import com.sky.entity.Seat;
import com.sky.vo.SeatStatisticsVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端-座位管理
 */
@RestController
@RequestMapping("/admin/seat")
@Slf4j
@Api(tags = "座位管理接口")
public class SeatController {

    @Autowired
    private SeatService seatService;

    @PostMapping
    @ApiOperation("新增座位")
    public Result save(@RequestBody SeatDTO seatDTO) {
        log.info("新增座位: {}", seatDTO);
        seatService.save(seatDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("座位分页查询")
    public Result<PageResult> page(SeatPageQueryDTO seatPageQueryDTO) {
        log.info("座位分页查询: {}", seatPageQueryDTO);
        PageResult pageResult = seatService.pageQuery(seatPageQueryDTO);
        return Result.success(pageResult);
    }

    @PutMapping
    @ApiOperation("编辑座位")
    public Result update(@RequestBody SeatDTO seatDTO) {
        log.info("编辑座位: {}", seatDTO);
        seatService.update(seatDTO);
        return Result.success();
    }

    @DeleteMapping
    @ApiOperation("删除座位（停用）")
    public Result delete(@RequestParam Long id) {
        log.info("删除座位ID: {}", id);
        seatService.deleteById(id);
        return Result.success();
    }

    @PutMapping("/status")
    @ApiOperation("启用/停用座位")
    public Result changeStatus(@RequestBody SeatStatusDTO seatStatusDTO) {
        log.info("修改座位状态: {}", seatStatusDTO);
        seatService.changeStatus(seatStatusDTO);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据ID查询座位")
    public Result<Seat> getById(@PathVariable Long id) {
        return Result.success(seatService.getById(id));
    }

    @GetMapping("/list")
    @ApiOperation("查询所有座位（用于布局展示）")
    public Result<List<Seat>> listAll() {
        return Result.success(seatService.listAll());
    }

    @GetMapping("/statistics")
    @ApiOperation("座位统计信息")
    public Result<SeatStatisticsVO> statistics() {
        return Result.success(seatService.getStatistics());
    }

    @PostMapping("/regenerate-qr/{id}")
    @ApiOperation("重新生成二维码")
    public Result regenerateQr(@PathVariable Long id) {
        seatService.regenerateQrCode(id);
        return Result.success();
    }
}
