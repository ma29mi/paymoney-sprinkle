package com.kakaopay.paymoneysprinkle.service;

import com.kakaopay.paymoneysprinkle.domain.SprinkleEvent;
import com.kakaopay.paymoneysprinkle.consts.Constants.ResponseKey;
import com.kakaopay.paymoneysprinkle.consts.Constants.ResponseCode;
import com.kakaopay.paymoneysprinkle.domain.SprinkleEventDetail;
import com.kakaopay.paymoneysprinkle.repository.SprinkleEventDetailRepository;
import com.kakaopay.paymoneysprinkle.repository.SprinkleEventRepository;
import com.kakaopay.paymoneysprinkle.exception.GeneralException;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;


@Service
@Transactional
public class SprinkleEventService {

    @Autowired
    private SprinkleEventRepository sprinkleEventRepository;
    @Autowired
    private SprinkleEventDetailRepository sprinkleEventDetailRepository;

    public static final int DIGITS = 3;


    public String sprinklePayMoney(SprinkleEvent sprinkleEvent){
        sprinkleEvent.setToken(createToken());

        sprinkleEventRepository.save(sprinkleEvent);

        SprinkleEventDetail sprinkleEventDetail = new SprinkleEventDetail();
        sprinkleEventDetail.setSprinkleId(sprinkleEvent.getSprinkleId());

        BigDecimal[] amounts = getAmountPerPerson(sprinkleEvent.getTotalAmount(), sprinkleEvent.getTotalEventCount());

        for (BigDecimal amount : amounts) {
            sprinkleEventDetail.setAmount(amount);
            sprinkleEventDetailRepository.save(sprinkleEventDetail);
        }

        String token="n";
        return token;
    }


    @Transactional
    public BigDecimal pickUpPayMoney(long userId, String roomId, String token){

        SprinkleEvent sprinkleEvent = new SprinkleEvent();
        sprinkleEvent = sprinkleEventRepository.findByRoomIdAndToken(sprinkleEvent.getRoomId(), sprinkleEvent.getToken());


        if (sprinkleEvent == null) {
            throw new GeneralException(ResponseCode.FAIL, "잘못된 요청이거나, 기간 만료된 뿌리기 이벤트 입니다");
        }

        if (sprinkleEvent.getTotalAmount().equals(sprinkleEvent.getRemitAmount())) {
            throw new GeneralException(ResponseCode.FAIL, "모든 금액이 소진되었습니다");
        }

        BigInteger sprinkleId = sprinkleEvent.getSprinkleId();

        long senderId = sprinkleEvent.getUserId();
        if (userId == senderId) {
            throw new GeneralException(ResponseCode.FAIL, "자신이 뿌린 이벤트는 참여할 수 없습니다.");
        }

        SprinkleEventDetail sprinkleEventDetail = new SprinkleEventDetail();
        sprinkleEventDetail.setSprinkleId(sprinkleId);
        sprinkleEventDetail.setUserId(userId);
        if (sprinkleEventDetailRepository.findByUserId(sprinkleEventDetail.getUserId()) != null) {
            throw new GeneralException(ResponseCode.FAIL, "뿌리기 이벤트는 한번만 참여가능합니다");
        }

        sprinkleEventDetailRepository.save(sprinkleEventDetail);
        BigDecimal totalRemittedAmount = sprinkleEvent.getRemitAmount().add(sprinkleEventDetail.getAmount());
        sprinkleEvent.setSprinkleId(sprinkleId);
        sprinkleEvent.setRemitAmount(totalRemittedAmount);
        sprinkleEventRepository.save(sprinkleEvent);

        return sprinkleEventDetail.getAmount();
    }

    private void updateSprinkleEventDetail(BigInteger sprinkleId, BigDecimal remitAmount) {
        SprinkleEvent sprinkleEvent = sprinkleEventRepository.findAllBySprinkleId(sprinkleId);
        sprinkleEvent.setSprinkleId(sprinkleId);
        sprinkleEvent.setRemitAmount(remitAmount);

        sprinkleEventRepository.save(sprinkleEvent);
    }

    public Map<String, Object> getSprinkleEventDetail (long userId, String roomId, String token){

        SprinkleEvent sprinkleEvent = new SprinkleEvent();
        sprinkleEvent.setUserId(userId);
        sprinkleEvent.setToken(token);
        sprinkleEvent.setRoomId(roomId);

        // 찾기 구현
        sprinkleEvent = sprinkleEventRepository.findByRoomIdAndToken(roomId, token);

        if (sprinkleEvent == null) {
            throw new GeneralException(ResponseCode.FAIL, "발생한적 없는 뿌리기 이벤트 입니다");
        }

        SprinkleEventDetail sprinkleEventDetail = new SprinkleEventDetail();
        sprinkleEventDetail.setSprinkleId(sprinkleEvent.getSprinkleId());
        List<SprinkleEventDetail> sprinkleEventDetailList = sprinkleEventDetailRepository.findAllBySprinkleId(sprinkleEventDetail.getSprinkleId());

        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put(ResponseKey.INFO, sprinkleEvent);
        resultMap.put(ResponseKey.DETAIL, sprinkleEventDetailList );

        return resultMap;
    }

    //3자리 토큰 발급
    public String createToken() {
        String token = RandomStringUtils.randomAscii(3);
        return token;
    }

    private BigDecimal[] getAmountPerPerson(BigDecimal totalAmount, int totalCount) {

        BigDecimal[] amounts = new BigDecimal[totalCount];
        int total = totalAmount.intValue();

        Random rand = new Random();
        for (int i = 0; i < amounts.length - 1; i++) {
            amounts[i] = new BigDecimal(rand.nextInt(total));
            total -= amounts[i].intValue();
        }
        amounts[amounts.length - 1] = new BigDecimal(total);

        return amounts;
    }

}
