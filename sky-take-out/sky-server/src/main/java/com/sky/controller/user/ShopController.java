package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.vo.ShopInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController("userShopController")
@RequestMapping("/user/shop")
@Api(tags = "店铺相关接口")
@Slf4j
public class ShopController {

    public static final String KEY = "SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 获取店铺的营业状态
     * @return
     */
    @GetMapping("/status")
    @ApiOperation("获取店铺的营业状态")
    public Result<Integer> getStatus() {
        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
        log.info("获取到店铺的营业状态为：{}",status == 1 ? "营业中" : "打烊中");
        return Result.success(status);
    }

    /**
     * 获取店铺信息
     * @return
     */
    @GetMapping("/info")
    @ApiOperation("获取店铺信息")
    public Result<ShopInfoVO> getInfo() {
        ShopInfoVO info = ShopInfoVO.builder()
                .name("苍穹外卖")
                .address("北京市朝阳区新街大道一号楼8层")
                .slogan("苍穹餐厅为顾客打造专业的大众化美食外送餐饮")
                .phone("13800138000")
                .deliveryFee(new BigDecimal("6"))
                .estimatedTime(12)
                .build();
        return Result.success(info);
    }

}
