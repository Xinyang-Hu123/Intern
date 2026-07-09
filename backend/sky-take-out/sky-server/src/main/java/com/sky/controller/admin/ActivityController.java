package com.sky.controller.admin;

import com.sky.dto.ManagementPageQueryDTO;
import com.sky.entity.CouponActivity;
import com.sky.entity.MarketingActivity;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.ManagementCatalogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/activity")
@Api(tags = "活动管理接口")
public class ActivityController {

    @Autowired
    private ManagementCatalogService managementCatalogService;

    @GetMapping("/marketing/page")
    @ApiOperation("营销活动分页查询")
    public Result<PageResult> pageMarketing(ManagementPageQueryDTO queryDTO) {
        return Result.success(managementCatalogService.pageMarketing(queryDTO));
    }

    @PostMapping("/marketing")
    @ApiOperation("新增营销活动")
    public Result<MarketingActivity> saveMarketing(@RequestBody MarketingActivity activity) {
        return Result.success(managementCatalogService.saveMarketing(activity));
    }

    @PutMapping("/marketing")
    @ApiOperation("编辑营销活动")
    public Result<MarketingActivity> updateMarketing(@RequestBody MarketingActivity activity) {
        return Result.success(managementCatalogService.saveMarketing(activity));
    }

    @DeleteMapping("/marketing/{id}")
    @ApiOperation("删除营销活动")
    public Result deleteMarketing(@PathVariable Long id) {
        managementCatalogService.deleteMarketing(id);
        return Result.success();
    }

    @PostMapping("/marketing/status/{status}")
    @ApiOperation("启停营销活动")
    public Result setMarketingStatus(@PathVariable Integer status, Long id) {
        managementCatalogService.setMarketingStatus(id, status);
        return Result.success();
    }

    @GetMapping("/coupon/page")
    @ApiOperation("优惠券分页查询")
    public Result<PageResult> pageCoupons(ManagementPageQueryDTO queryDTO) {
        return Result.success(managementCatalogService.pageCoupons(queryDTO));
    }

    @PostMapping("/coupon")
    @ApiOperation("新增优惠券")
    public Result<CouponActivity> saveCoupon(@RequestBody CouponActivity coupon) {
        return Result.success(managementCatalogService.saveCoupon(coupon));
    }

    @PutMapping("/coupon")
    @ApiOperation("编辑优惠券")
    public Result<CouponActivity> updateCoupon(@RequestBody CouponActivity coupon) {
        return Result.success(managementCatalogService.saveCoupon(coupon));
    }

    @DeleteMapping("/coupon/{id}")
    @ApiOperation("删除优惠券")
    public Result deleteCoupon(@PathVariable Long id) {
        managementCatalogService.deleteCoupon(id);
        return Result.success();
    }

    @PostMapping("/coupon/status/{status}")
    @ApiOperation("启停优惠券")
    public Result setCouponStatus(@PathVariable Integer status, Long id) {
        managementCatalogService.setCouponStatus(id, status);
        return Result.success();
    }
}
