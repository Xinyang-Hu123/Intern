package com.sky.service.impl;

import com.sky.entity.Seat;
import com.sky.exception.BaseException;
import com.sky.mapper.SeatMapper;
import com.sky.websocket.WebSocketServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeatServiceImplTest {

    @Mock
    private SeatMapper seatMapper;

    @Mock
    private WebSocketServer webSocketServer;

    @InjectMocks
    private SeatServiceImpl seatService;

    @Test
    void scanRejectsUnknownOrUnavailableSeat() {
        when(seatMapper.getBySeatNumber("A1")).thenReturn(null);
        assertThrows(BaseException.class, () -> seatService.getAvailableBySeatNumber("A1"));

        when(seatMapper.getBySeatNumber("A1")).thenReturn(Seat.builder().id(1L).seatNumber("A1").status(1).build());
        assertThrows(BaseException.class, () -> seatService.getAvailableBySeatNumber("A1"));
    }

    @Test
    void occupyUsesAtomicUpdateAndPublishesStatusChange() {
        when(seatMapper.occupyIfAvailable(1L)).thenReturn(1);

        seatService.occupy(1L);

        verify(seatMapper).occupyIfAvailable(1L);
        verify(webSocketServer).sendToAllClient(contains("seat-status-changed"));

        when(seatMapper.occupyIfAvailable(2L)).thenReturn(0);
        assertThrows(BaseException.class, () -> seatService.occupy(2L));
    }

    @Test
    void releaseOnlyChangesOccupiedSeat() {
        when(seatMapper.getById(1L)).thenReturn(Seat.builder().id(1L).status(1).build());

        seatService.release(1L);

        verify(seatMapper).updateStatus(any(Seat.class));
        verify(webSocketServer).sendToAllClient(contains("seat-status-changed"));
        verify(seatMapper, never()).occupyIfAvailable(anyLong());
    }
}
