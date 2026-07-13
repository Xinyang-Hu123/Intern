package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.service.SeatService;
import com.sky.vo.SeatScanResultVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SeatUserControllerTest {

    @Mock
    private SeatService seatService;

    private SeatUserController seatUserController;

    @BeforeEach
    void setUp() {
        seatUserController = new SeatUserController();
        ReflectionTestUtils.setField(seatUserController, "seatService", seatService);
    }

    @Test
    void confirmSessionReturnsServiceResult() {
        SeatScanResultVO expected = SeatScanResultVO.builder().success(true).build();
        when(seatService.confirmSession(1L)).thenReturn(expected);

        Result<SeatScanResultVO> actual = seatUserController.confirmSession(1L);

        assertSame(expected, actual.getData());
        verify(seatService).confirmSession(1L);
    }
}
