package com.systems.demo.apnewsdemo.dto.request;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {

  private String id;            // idempotency or tracking id
  private String accountId;     // which account to debit/credit
  private BigDecimal amount;    // payment amount
  private String currency;      // e.g. "USD"
  private String description;   // optional
}

