package com.kakaopay.paymoneysprinkle.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SprinkleEventDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger sprinkleDetailId;
    @NotNull
    private BigInteger sprinkleId;
    private long userId ;

    @DecimalMin(value="0.0" , inclusive = false , message = "0원 이상 입력해주세요")
    private BigDecimal amount;
    private Timestamp createDateTime;
    private Timestamp updateDateTime;
    private char status;
}
