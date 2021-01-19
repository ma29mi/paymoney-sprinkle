package com.kakaopay.paymoneysprinkle.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SprinkleEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger sprinkleId;
    private String roomId;
    private String token;
    private long userId;

    @DecimalMin(value="0.0" , inclusive = false , message = "0원 이상 입력해주세요")
    private BigDecimal totalAmount;
    private BigDecimal remitAmount;
    @Min(value = 1, message = "1명 이상부터 뿌리기 가능합니다.")
    private int totalEventCount;


    private Timestamp createDateTime;
    private Timestamp updateDateTime;
    private Timestamp expireDateTime;

    private char status; // 이벤트 유효 상태

}
