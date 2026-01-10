package com.systems.demo.apnewsdemo.controller.concurrecy;

import com.systems.demo.apnewsdemo.dto.request.PaymentRequest;
import com.systems.demo.apnewsdemo.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

  private final PaymentService service;

  @PostMapping
  public ResponseEntity<String> pay(@RequestBody PaymentRequest req) {
    service.processPayment(req);
    return ResponseEntity.accepted().body("Accepted");
  }
}
