package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.vo.ShopInfoVO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ShopControllerTest {

    @Test
    void getInfoReturnsShopInfo() {
        ShopController controller = new ShopController();

        Result<ShopInfoVO> result = controller.getInfo();

        assertEquals(1, result.getCode());
        assertNotNull(result.getData());
        assertEquals("苍穹外卖", result.getData().getName());
        assertEquals("北京市朝阳区新街大道一号楼8层", result.getData().getAddress());
        assertEquals("苍穹餐厅为顾客打造专业的大众化美食外送餐饮", result.getData().getSlogan());
        assertEquals("13800138000", result.getData().getPhone());
        assertEquals(0, new BigDecimal("6").compareTo(result.getData().getDeliveryFee()));
        assertEquals(12, result.getData().getEstimatedTime());
    }
}
