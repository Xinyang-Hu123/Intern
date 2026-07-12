package com.sky.controller.admin;

import com.sky.dto.SeatDTO;
import com.sky.dto.SeatPageQueryDTO;
import com.sky.entity.Seat;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SeatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 座位管理
 */
@RestController
@RequestMapping("/admin/seat")
@Api(tags = "座位管理相关接口")
@Slf4j
public class SeatController {

    @Autowired
    private SeatService seatService;

    /**
     * 新增座位
     */
    @PostMapping
    @ApiOperation("新增座位")
    public Result<String> save(@RequestBody SeatDTO seatDTO) {
        log.info("新增座位：{}", seatDTO);
        seatService.save(seatDTO);
        return Result.success();
    }

    /**
     * 座位分页查询
     */
    @GetMapping("/page")
    @ApiOperation("座位分页查询")
    public Result<PageResult> page(SeatPageQueryDTO seatPageQueryDTO) {
        log.info("座位分页查询：{}", seatPageQueryDTO);
        PageResult pageResult = seatService.pageQuery(seatPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据ID查询座位
     */
    @GetMapping("/{id}")
    @ApiOperation("查询座位详情")
    public Result<Seat> getById(@PathVariable Long id) {
        log.info("查询座位：{}", id);
        Seat seat = seatService.getById(id);
        return Result.success(seat);
    }

    /**
     * 修改座位
     */
    @PutMapping
    @ApiOperation("修改座位")
    public Result<String> update(@RequestBody SeatDTO seatDTO) {
        log.info("修改座位：{}", seatDTO);
        seatService.update(seatDTO);
        return Result.success();
    }

    /**
     * 删除座位
     */
    @DeleteMapping
    @ApiOperation("删除座位")
    public Result<String> delete(Long id) {
        log.info("删除座位：{}", id);
        seatService.deleteById(id);
        return Result.success();
    }

    /**
     * 修改座位状态
     */
    @PutMapping("/status/{status}")
    @ApiOperation("修改座位状态")
    public Result<String> updateStatus(Long id, @PathVariable Integer status) {
        log.info("修改座位状态：id={}, status={}", id, status);
        seatService.updateStatus(id, status);
        return Result.success();
    }

    /**
     * 查询所有座位列表
     */
    @GetMapping("/list")
    @ApiOperation("查询座位列表")
    public Result<List<Seat>> list(Integer status) {
        log.info("查询座位列表，status={}", status);
        List<Seat> list = seatService.list(status);
        return Result.success(list);
    }

    /**
     * 座位状态统计
     */
    @GetMapping("/statusCount")
    @ApiOperation("座位状态统计")
    public Result<List<Map<String, Object>>> statusCount() {
        log.info("座位状态统计");
        List<Map<String, Object>> result = seatService.statusCount();
        return Result.success(result);
    }
}
