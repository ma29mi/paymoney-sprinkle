package com.kakaopay.paymoneysprinkle.service;

import com.kakaopay.paymoneysprinkle.domain.SprinkleEvent;
import com.kakaopay.paymoneysprinkle.exception.GeneralException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class SprinkleEventServiceTest {
    @Autowired
    SprinkleEventService sprinkleEventService;

    @org.junit.Test
    public void testSprinklePayMoney() {

        long senderId = 1234L;
        long userId = 9999L;
        String roomId = "r01";
        int totalCount = 4;
        BigDecimal totalAmount = BigDecimal.valueOf(10000);

        SprinkleEvent sprinkleEvent = new SprinkleEvent();
        sprinkleEvent.setUserId(senderId);
        sprinkleEvent.setRoomId(roomId);
        sprinkleEvent.setTotalAmount(totalAmount);
        sprinkleEvent.setTotalEventCount(totalCount);

        String token = sprinkleEventService.sprinklePayMoney(sprinkleEvent);
        assertThat(token).isNotBlank();
        assertThat(token.length()).isEqualTo(3);
    }

    @org.junit.Test
    public void testPickUpPayMoney() {

        long senderId = 1234L;
        long userId = 9999L;
        String roomId = "r01";
        int totalCount = 4;
        BigDecimal totalAmount = BigDecimal.valueOf(10000);

        SprinkleEvent sprinkleEvent = new SprinkleEvent();
        sprinkleEvent.setUserId(senderId);
        sprinkleEvent.setRoomId(roomId);
        sprinkleEvent.setTotalAmount(totalAmount);
        sprinkleEvent.setTotalEventCount(totalCount);

        String token = sprinkleEventService.createToken();

        BigDecimal ammount = sprinkleEventService.pickUpPayMoney(userId, roomId, token);
        assertThat(ammount.intValue()).isGreaterThan(0);
    }

    @Test(expected = GeneralException.class)
    public void testSprinkleExceptionPickupMe() {

        long senderId = 12345;
        String roomId = "r01";
        int totalCount = 4;
        BigDecimal totalAmount = BigDecimal.valueOf(50000);

        SprinkleEvent sprinkleEvent = new SprinkleEvent();
        sprinkleEvent.setUserId(senderId);
        sprinkleEvent.setRoomId(roomId);
        sprinkleEvent.setTotalAmount(totalAmount);
        sprinkleEvent.setTotalEventCount(totalCount);

        String token = sprinkleEventService.sprinklePayMoney(sprinkleEvent);

        // 받는 사용자 아이디를 뿌린 사용자 아이디(senderId)로 설정
        sprinkleEventService.pickUpPayMoney(senderId, roomId, token);
    }


    @org.junit.Test(expected = GeneralException.class)
    public void testSprinkleExceptionNotSameRoom() {

        long senderId = 12345;
        String roomId = "r01";
        int totalCount = 4;
        BigDecimal totalAmount = BigDecimal.valueOf(10000);

        SprinkleEvent sprinkleEvent = new SprinkleEvent();
        sprinkleEvent.setUserId(senderId);
        sprinkleEvent.setRoomId(roomId);
        sprinkleEvent.setTotalAmount(totalAmount);
        sprinkleEvent.setTotalEventCount(totalCount);

        String token = sprinkleEventService.sprinklePayMoney(sprinkleEvent);

        // 속하지 않은 대화방 아이디 설정
        String anotherRoomId = "R99";
        sprinkleEventService.pickUpPayMoney(senderId, anotherRoomId, token);
    }

    @org.junit.Test(expected = GeneralException.class)
    public void testSprinkleExceptionOnlyOne() {

        long senderId = 12345;
        String roomId = "r01";
        int totalCount = 4;
        BigDecimal totalAmount = BigDecimal.valueOf(10000);

        SprinkleEvent sprinkleEvent = new SprinkleEvent();
        sprinkleEvent.setUserId(senderId);
        sprinkleEvent.setRoomId(roomId);
        sprinkleEvent.setTotalAmount(totalAmount);
        sprinkleEvent.setTotalEventCount(totalCount);

        String token = sprinkleEventService.sprinklePayMoney(sprinkleEvent);

        // 뿌린 돈은 최초 1회만 받을 수 있다
        sprinkleEventService.pickUpPayMoney(receiverId, roomId, token);

        // 두 번 이상 받을 경우에는 오류 발생
        sprinkleEventService.pickUpPayMoney(receiverId, roomId, token);
    }

    @Test
    public void testDescribeMoneySpraying() {
        long senderId = 12345;
        String roomId = "r01";
        int totalCount = 4;
        BigDecimal totalAmount = BigDecimal.valueOf(10000);

        SprinkleEvent sprinkleEvent = new SprinkleEvent();
        sprinkleEvent.setUserId(senderId);
        sprinkleEvent.setRoomId(roomId);
        sprinkleEvent.setTotalAmount(totalAmount);
        sprinkleEvent.setTotalEventCount(totalCount);

        String token = sprinkleEventService.sprinklePayMoney(sprinkleEvent);
        assertThat(sprinkleEventService.getSprinkleEventDetail(senderId, roomId, token)).isNotNull();
    }
}