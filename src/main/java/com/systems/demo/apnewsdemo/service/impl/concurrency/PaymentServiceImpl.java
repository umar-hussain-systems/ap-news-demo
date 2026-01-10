package com.systems.demo.apnewsdemo.service.impl.concurrency;

import com.systems.demo.apnewsdemo.dto.request.PaymentRequest;
import com.systems.demo.apnewsdemo.service.PaymentService;
import java.util.concurrent.ExecutorService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

  private final ExecutorService paymentExecutor;


  public PaymentServiceImpl(@Qualifier("paymentExecutor") ExecutorService paymentExecutor) {
    this.paymentExecutor = paymentExecutor;
  }

  @Override
  public void processPayment(PaymentRequest request) {
    paymentExecutor.submit(() -> heavyProcessing(request));
  }

  private void heavyProcessing(PaymentRequest request) {
    try {
      Thread.sleep(200); // simulate DB + network work
      System.out.println(Thread.currentThread().getName() + " processed " + request.getId());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
