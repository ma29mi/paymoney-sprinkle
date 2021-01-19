package com.kakaopay.paymoneysprinkle.repository;

import com.kakaopay.paymoneysprinkle.domain.SprinkleEventDetail;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;
import java.util.List;

public interface SprinkleEventDetailRepository extends CrudRepository<SprinkleEventDetail, BigInteger> {
    SprinkleEventDetail save(SprinkleEventDetail sprinkleEventDetail);
    SprinkleEventDetail findByUserId(long UserId);
    List<SprinkleEventDetail> findAllBySprinkleId(BigInteger sprinkleId);
}
