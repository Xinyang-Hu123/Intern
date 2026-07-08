package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.CouponPageQueryDTO;
import com.sky.entity.Coupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CouponMapper {

    /**
     * 新增优惠券
     * @param coupon
     */
    void insert(Coupon coupon);

    /**
     * 优惠券分页查询
     * @param couponPageQueryDTO
     * @return
     */
    Page<Coupon> pageQuery(CouponPageQueryDTO couponPageQueryDTO);

    /**
     * 修改优惠券
     * @param coupon
     */
    void update(Coupon coupon);

    /**
     * 根据id删除优惠券
     * @param id
     */
    void deleteById(@Param("id") Long id);
}
