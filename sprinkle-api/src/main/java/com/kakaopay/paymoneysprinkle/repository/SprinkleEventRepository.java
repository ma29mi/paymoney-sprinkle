package com.kakaopay.paymoneysprinkle.repository;

import com.kakaopay.paymoneysprinkle.domain.SprinkleEvent;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;

public interface SprinkleEventRepository extends CrudRepository<SprinkleEvent, BigInteger> {
    SprinkleEvent save(SprinkleEvent sprinkleEvent);
    SprinkleEvent findAllBySprinkleId(BigInteger sprinkleId);
    SprinkleEvent findByRoomIdAndToken(String roomId, String token);
}
