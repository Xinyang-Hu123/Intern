package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.Orders;
import com.sky.entity.Seat;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.SeatService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderSubmitVO;
import com.sky.websocket.WebSocketServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderSeatBindingTest {

    @Mock private OrderMapper orderMapper;
    @Mock private OrderDetailMapper orderDetailMapper;
    @Mock private ShoppingCartMapper shoppingCartMapper;
    @Mock private AddressBookMapper addressBookMapper;
    @Mock private UserMapper userMapper;
    @Mock private WeChatPayUtil weChatPayUtil;
    @Mock private WebSocketServer webSocketServer;
    @Mock private SeatService seatService;
    @InjectMocks private OrderServiceImpl orderService;

    @AfterEach
    void clearContext() {
        BaseContext.removeCurrentId();
    }

    @Test
    void submitOrderBindsScannedSeatAndOccupiesIt() {
        BaseContext.setCurrentId(7L);
        OrdersSubmitDTO request = new OrdersSubmitDTO();
        request.setAddressBookId(1L);
        request.setSeatNumber("A1");
        request.setAmount(new BigDecimal("38.00"));
        when(addressBookMapper.getById(1L)).thenReturn(AddressBook.builder().phone("13800000000").detail("店内").consignee("顾客").build());
        when(shoppingCartMapper.list(any(ShoppingCart.class))).thenReturn(Collections.singletonList(ShoppingCart.builder().name("炒饭").number(1).amount(new BigDecimal("38.00")).build()));
        when(seatService.getAvailableBySeatNumber("A1")).thenReturn(Seat.builder().id(11L).seatNumber("A1").status(0).build());
        doAnswer(invocation -> { invocation.getArgument(0, Orders.class).setId(100L); return null; }).when(orderMapper).insert(any(Orders.class));

        OrderSubmitVO response = orderService.submitOrder(request);

        ArgumentCaptor<Orders> orderCaptor = ArgumentCaptor.forClass(Orders.class);
        verify(orderMapper).insert(orderCaptor.capture());
        assertEquals(11L, orderCaptor.getValue().getSeatId());
        assertEquals(100L, response.getId());
        verify(seatService).occupy(11L);
        verify(shoppingCartMapper).deleteByUserId(7L);
    }

    @Test
    void userCancellationReleasesBoundSeat() throws Exception {
        Orders order = Orders.builder().id(100L).seatId(11L).status(Orders.PENDING_PAYMENT).payStatus(Orders.UN_PAID).build();
        when(orderMapper.getById(100L)).thenReturn(order);

        orderService.userCancelById(100L);

        verify(orderMapper).update(any(Orders.class));
        verify(seatService).release(11L);
    }
}
