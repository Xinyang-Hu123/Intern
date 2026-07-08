package com.sky.controller.admin;

import com.sky.dto.CouponDTO;
import com.sky.dto.CouponPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CouponService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/coupon")
@Api(tags = "优惠券相关接口")
@Slf4j
public class CouponController {

    @Autowired
    private CouponService couponService;

    @PostMapping
    @ApiOperation("新增优惠券")
    public Result<String> save(@RequestBody CouponDTO couponDTO) {
        log.info("新增优惠券：{}", couponDTO);
        couponService.save(couponDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("优惠券分页查询")
    public Result<PageResult> page(CouponPageQueryDTO couponPageQueryDTO) {
        log.info("优惠券分页查询：{}", couponPageQueryDTO);
        PageResult pageResult = couponService.pageQuery(couponPageQueryDTO);
        return Result.success(pageResult);
    }

    @PutMapping
    @ApiOperation("修改优惠券")
    public Result<String> update(@RequestBody CouponDTO couponDTO) {
        log.info("修改优惠券：{}", couponDTO);
        couponService.update(couponDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("启用禁用优惠券")
    public Result<String> startOrStop(@PathVariable("status") Integer status, Long id) {
        couponService.startOrStop(status, id);
        return Result.success();
    }

    @DeleteMapping
    @ApiOperation("删除优惠券")
    public Result<String> deleteById(Long id) {
        couponService.deleteById(id);
        return Result.success();
    }
}
