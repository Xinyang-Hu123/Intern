package com.sky.service;

import com.sky.dto.CouponDTO;
import com.sky.dto.CouponPageQueryDTO;
import com.sky.result.PageResult;

public interface CouponService {

    /**
     * 新增优惠券
     * @param couponDTO
     */
    void save(CouponDTO couponDTO);

    /**
     * 分页查询优惠券
     * @param couponPageQueryDTO
     * @return
     */
    PageResult pageQuery(CouponPageQueryDTO couponPageQueryDTO);

    /**
     * 修改优惠券
     * @param couponDTO
     */
    void update(CouponDTO couponDTO);

    /**
     * 启用、禁用优惠券
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 删除优惠券
     * @param id
     */
    void deleteById(Long id);
}
