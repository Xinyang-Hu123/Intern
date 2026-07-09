package com.sky.vo;

import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderVO extends Orders implements Serializable {

    //订单菜品信息
    private String orderDishes;

    //订单详情
    private List<OrderDetail> orderDetailList;

    private Long memberId;

    private String memberName;

    private String productNames;

    private List<String> productImages;

    private String productStatus;

    private String receiverAddress;

    private String contactPhone;

    private LocalDateTime placedAt;

    private String marketingActivity;

    private String couponActivity;

    private BigDecimal plannedAmount;

    private BigDecimal discountAmount;

    private BigDecimal actualAmount;
}
