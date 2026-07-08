package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CouponDTO;
import com.sky.dto.CouponPageQueryDTO;
import com.sky.entity.Coupon;
import com.sky.exception.CouponBusinessException;
import com.sky.mapper.CouponMapper;
import com.sky.result.PageResult;
import com.sky.service.CouponService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponMapper couponMapper;

    @Override
    public void save(CouponDTO couponDTO) {
        validate(couponDTO);

        Coupon coupon = new Coupon();
        BeanUtils.copyProperties(couponDTO, coupon);
        LocalDateTime now = LocalDateTime.now();
        coupon.setStatus(StatusConstant.ENABLE);
        coupon.setCreateTime(now);
        coupon.setUpdateTime(now);
        coupon.setCreateUser(BaseContext.getCurrentId());
        coupon.setUpdateUser(BaseContext.getCurrentId());

        couponMapper.insert(coupon);
    }

    @Override
    public PageResult pageQuery(CouponPageQueryDTO couponPageQueryDTO) {
        PageHelper.startPage(couponPageQueryDTO.getPage(), couponPageQueryDTO.getPageSize());
        Page<Coupon> page = couponMapper.pageQuery(couponPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void update(CouponDTO couponDTO) {
        validate(couponDTO);

        Coupon coupon = new Coupon();
        BeanUtils.copyProperties(couponDTO, coupon);
        coupon.setUpdateTime(LocalDateTime.now());
        coupon.setUpdateUser(BaseContext.getCurrentId());

        couponMapper.update(coupon);
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        Coupon coupon = Coupon.builder()
                .id(id)
                .status(status)
                .updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())
                .build();
        couponMapper.update(coupon);
    }

    @Override
    public void deleteById(Long id) {
        couponMapper.deleteById(id);
    }

    private void validate(CouponDTO couponDTO) {
        if (couponDTO.getName() == null || couponDTO.getName().trim().isEmpty()) {
            throw new CouponBusinessException("优惠券名称不能为空");
        }
        if (couponDTO.getDiscount() == null
                || couponDTO.getDiscount().compareTo(BigDecimal.ZERO) <= 0
                || couponDTO.getDiscount().compareTo(new BigDecimal("10")) > 0) {
            throw new CouponBusinessException("优惠券折扣必须大于0且不超过10折");
        }
        if (couponDTO.getStartTime() == null || couponDTO.getEndTime() == null) {
            throw new CouponBusinessException("优惠券有效期不能为空");
        }
        if (couponDTO.getEndTime().isBefore(couponDTO.getStartTime())) {
            throw new CouponBusinessException("优惠券截止日期不能早于开始日期");
        }
    }
}
