package com.systems.demo.apnewsdemo.service;

import com.systems.demo.apnewsdemo.dto.request.PaymentRequest;

public interface PaymentService {

  void processPayment(PaymentRequest request);
}
