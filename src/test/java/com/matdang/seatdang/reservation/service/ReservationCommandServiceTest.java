package com.matdang.seatdang.reservation.service;

import com.matdang.seatdang.member.entity.Member;
import com.matdang.seatdang.member.entity.StoreOwner;
import com.matdang.seatdang.member.repository.MemberRepository;
import com.matdang.seatdang.member.vo.StoreVo;
import com.matdang.seatdang.reservation.dto.ReservationSaveRequestDto;
import com.matdang.seatdang.reservation.dto.ReservationTicketRequestDTO;
import com.matdang.seatdang.reservation.entity.Reservation;
import com.matdang.seatdang.reservation.repository.ReservationRepository;
import com.matdang.seatdang.reservation.vo.*;
import com.matdang.seatdang.store.entity.Store;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReservationCommandServiceTest {
    @Autowired
    ReservationCommandService reservationCommandService;
    @Autowired
    ReservationSlotCommandService reservationSlotCommandService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ReservationRepository reservationRepository;

    @Transactional
    @DisplayName("가예약 1건 생성")
    @Test
    public void test1() {
        //given
        Optional<Member> optMember = memberRepository.findById(11L);
        StoreOwner storeOwner = (StoreOwner) optMember.orElse(null);
        StoreVo store = storeOwner.getStore();
        ReservationTicketRequestDTO ticketRequestDTO = ReservationTicketRequestDTO.builder()
                .storeId(storeOwner.getStore().getStoreId())
                .date(LocalDate.of(2024, 8, 30))
                .time(LocalTime.of(15,30))
                .maxReservation(5)
                .build();

        ReservationSaveRequestDto saveRequestDto = ReservationSaveRequestDto.builder()
                .store(new StoreInfo(store.getStoreId(), store.getStoreName(), "02-1234-1234"))
                .storeOwner(new StoreOwnerInfo(storeOwner.getMemberId(), storeOwner.getMemberName()))
                .customer(new CustomerInfo(1L, "이재용", "02-1234-1234"))
                .reservedAt(LocalDateTime.of(2024,8,20,15,30))
                .orderedMenuList(List.of(new OrderedMenu("떡케이크", 30000, "imageurl", null)))
                .build();

        //when
        ReservationTicket reservationTicket = reservationSlotCommandService.getReservationTicket(ticketRequestDTO);
        if(reservationTicket.equals(ReservationTicket.AVAILABLE)){
            reservationCommandService.createCustomMenuReservation(saveRequestDto);
        }
        List<Reservation> result = reservationRepository.findByStoreOwner_StoreOwnerId(11L);
        //then
        assertThat(result).isNotEmpty();
        result.forEach((reservation)->{
            assertThat(reservation.getStore().getStoreId()).isEqualTo(store.getStoreId());
            System.out.println(reservation);
        });
    }
}