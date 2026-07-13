package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.SeatDTO;
import com.sky.entity.DiningSession;
import com.sky.entity.DiningSessionParticipant;
import com.sky.entity.Seat;
import com.sky.exception.SeatBusinessException;
import com.sky.mapper.SeatMapper;
import com.sky.vo.SeatScanResultVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SeatServiceImplTest {

    private static final Long SEAT_ID = 1L;
    private static final Long SESSION_ID = 9001L;

    @Mock
    private SeatMapper seatMapper;

    private SeatServiceImpl seatService;

    @BeforeEach
    void setUp() {
        BaseContext.setCurrentId(101L);
        seatService = new SeatServiceImpl();
        ReflectionTestUtils.setField(seatService, "seatMapper", seatMapper);
        ReflectionTestUtils.setField(seatService, "qrSecretKey", "test-secret");
    }

    @AfterEach
    void tearDown() {
        BaseContext.removeCurrentId();
    }

    @Test
    void saveReturnsSeatReloadedAfterQrSignatureIsGenerated() {
        SeatDTO seatDTO = new SeatDTO();
        seatDTO.setSeatCode("A01");
        seatDTO.setSeatName("A区1号桌");
        seatDTO.setAreaName("A区");
        seatDTO.setCapacity(4);
        AtomicLong generatedId = new AtomicLong(SEAT_ID);
        Seat reloadedSeat = availableSeat(4);

        when(seatMapper.getBySeatCode("A01")).thenReturn(null);
        doAnswer(invocation -> {
            Seat seat = invocation.getArgument(0);
            seat.setId(generatedId.get());
            return null;
        }).when(seatMapper).insert(any(Seat.class));
        when(seatMapper.getById(SEAT_ID)).thenReturn(reloadedSeat);

        Seat savedSeat = seatService.save(seatDTO);

        assertEquals(reloadedSeat, savedSeat);
        verify(seatMapper).updateQrInfo(eq(SEAT_ID), eq(1), eq(signature("A01", 1)));
    }

    @Test
    void parseSeatBySceneOnlyReadsSeatAndDoesNotCreateSessionOrOccupySeat() {
        Seat seat = availableSeat(4);
        when(seatMapper.getBySeatCode(seat.getSeatCode())).thenReturn(seat);
        when(seatMapper.getOpenSessionBySeat(SEAT_ID)).thenReturn(null);

        SeatScanResultVO result = seatService.parseSeatByScene(signedScene(seat));

        assertTrue(result.getSuccess());
        assertEquals("扫码成功", result.getMessage());
        assertNull(result.getDiningSessionId());
        assertEquals(0, result.getParticipantCount());
        assertEquals(4, result.getCapacity());
        assertFalse(result.getJoined());
        assertFalse(result.getFull());
        verify(seatMapper, never()).insertSession(any(DiningSession.class));
        verify(seatMapper, never()).insertParticipant(any(DiningSessionParticipant.class));
        verify(seatMapper, never()).updateStatus(any(Long.class), any(String.class));
    }

    @Test
    void confirmSessionCreatesOpenSessionJoinsFirstUserAndOccupiesSeat() {
        Seat seat = availableSeat(4);
        DiningSession session = openSession();
        when(seatMapper.getByIdForUpdate(SEAT_ID)).thenReturn(seat);
        when(seatMapper.getOpenSessionBySeat(SEAT_ID)).thenReturn(null);
        doAnswer(invocation -> {
            DiningSession inserted = invocation.getArgument(0);
            inserted.setId(SESSION_ID);
            return null;
        }).when(seatMapper).insertSession(any(DiningSession.class));
        when(seatMapper.countParticipants(SESSION_ID)).thenReturn(0);
        when(seatMapper.countParticipantBySessionAndUser(SESSION_ID, 101L)).thenReturn(0);

        SeatScanResultVO result = seatService.confirmSession(SEAT_ID);

        assertTrue(result.getSuccess());
        assertTrue(result.getJoined());
        assertFalse(result.getFull());
        assertEquals(SESSION_ID, result.getDiningSessionId());
        assertEquals(1, result.getParticipantCount());
        assertEquals(4, result.getCapacity());
        ArgumentCaptor<DiningSessionParticipant> participantCaptor =
                ArgumentCaptor.forClass(DiningSessionParticipant.class);
        verify(seatMapper).insertParticipant(participantCaptor.capture());
        assertEquals(SESSION_ID, participantCaptor.getValue().getDiningSessionId());
        assertEquals(101L, participantCaptor.getValue().getUserId());
        verify(seatMapper).updateStatus(SEAT_ID, "OCCUPIED");
        InOrder inOrder = inOrder(seatMapper);
        inOrder.verify(seatMapper).getByIdForUpdate(SEAT_ID);
        inOrder.verify(seatMapper).getOpenSessionBySeat(SEAT_ID);
        inOrder.verify(seatMapper).insertSession(any(DiningSession.class));
    }

    @Test
    void confirmSessionForExistingParticipantIsIdempotent() {
        Seat seat = availableSeat(4);
        DiningSession session = openSession();
        when(seatMapper.getByIdForUpdate(SEAT_ID)).thenReturn(seat);
        when(seatMapper.getOpenSessionBySeat(SEAT_ID)).thenReturn(session);
        when(seatMapper.countParticipants(SESSION_ID)).thenReturn(1);
        when(seatMapper.countParticipantBySessionAndUser(SESSION_ID, 101L)).thenReturn(1);

        SeatScanResultVO result = seatService.confirmSession(SEAT_ID);

        assertTrue(result.getSuccess());
        assertTrue(result.getJoined());
        assertFalse(result.getFull());
        assertEquals(SESSION_ID, result.getDiningSessionId());
        assertEquals(1, result.getParticipantCount());
        verify(seatMapper, never()).insertParticipant(any(DiningSessionParticipant.class));
        verify(seatMapper, never()).updateStatus(any(Long.class), any(String.class));
    }

    @Test
    void confirmSessionRejectsNewParticipantWhenSeatIsFullWithoutWritingParticipant() {
        BaseContext.setCurrentId(102L);
        Seat seat = availableSeat(2);
        DiningSession session = openSession();
        when(seatMapper.getByIdForUpdate(SEAT_ID)).thenReturn(seat);
        when(seatMapper.getOpenSessionBySeat(SEAT_ID)).thenReturn(session);
        when(seatMapper.countParticipants(SESSION_ID)).thenReturn(2);
        when(seatMapper.countParticipantBySessionAndUser(SESSION_ID, 102L)).thenReturn(0);

        SeatBusinessException exception = assertThrows(
                SeatBusinessException.class,
                () -> seatService.confirmSession(SEAT_ID)
        );

        assertEquals("该座位已被占用，请联系店员", exception.getMessage());
        verify(seatMapper, never()).insertParticipant(any(DiningSessionParticipant.class));
        verify(seatMapper, never()).updateStatus(any(Long.class), any(String.class));
        InOrder inOrder = inOrder(seatMapper);
        inOrder.verify(seatMapper).getByIdForUpdate(SEAT_ID);
        inOrder.verify(seatMapper).getOpenSessionBySeat(SEAT_ID);
    }

    private Seat availableSeat(int capacity) {
        return Seat.builder()
                .id(SEAT_ID)
                .seatCode("A01")
                .seatName("A区1号桌")
                .areaName("A区")
                .capacity(capacity)
                .status("AVAILABLE")
                .qrVersion(1)
                .build();
    }

    private DiningSession openSession() {
        return DiningSession.builder()
                .id(SESSION_ID)
                .seatId(SEAT_ID)
                .status("OPEN")
                .build();
    }

    private String signedScene(Seat seat) {
        return seat.getSeatCode() + ":" + seat.getQrVersion() + ":"
                + signature(seat.getSeatCode(), seat.getQrVersion());
    }

    private String signature(String seatCode, Integer qrVersion) {
        String data = seatCode + ":" + qrVersion + ":test-secret";
        return DigestUtils.md5DigestAsHex(data.getBytes(StandardCharsets.UTF_8));
    }
}
