package com.systems.demo.apnewsdemo.multi.threading.excutor.service;

import java.util.concurrent.*;
import java.util.function.Supplier;

public class SimpleCompletableFutureDemo {

  public static void main(String[] args) {

    ExecutorService executor = Executors.newFixedThreadPool(4);
    AsyncService asyncService = new AsyncService(executor);

    Long optionalCountryId = null; // try changing to 1L

    // 1️⃣ First parallel future
    CompletableFuture<PersonaDto> personaFuture =
        asyncService.asyncFetch(() -> fetchPersona());

    // 2️⃣ Optional lookup using completedFuture(null)
    CompletableFuture<CountryDto> countryFuture =
        optionalCountryId == null
            ? CompletableFuture.completedFuture(null)
            : asyncService.asyncFetch(() -> fetchCountry(optionalCountryId));

    // 3️⃣ Dependent future based on personaFuture
    CompletableFuture<String> serviceTypeFuture =
        personaFuture.thenApplyAsync(persona ->
            "ServiceType: " + persona.serviceType(), executor);

    // 4️⃣ Wait for all
    CompletableFuture.allOf(
        personaFuture,
        countryFuture,
        serviceTypeFuture
    ).join();

    // 5️⃣ Unwrap
    PersonaDto persona = personaFuture.join();
    CountryDto country = countryFuture.join();
    String serviceType = serviceTypeFuture.join();

    System.out.println("Persona = " + persona);
    System.out.println("Country = " + country);
    System.out.println("Derived = " + serviceType);



    CompletableFuture<Integer> future =
        CompletableFuture.supplyAsync(() -> 10 / 0);

    CompletableFuture<Integer> handled =
        future.handle((result, ex) -> {
          if (ex != null) {
            throw new CustomBusinessException("Calculation failed", ex);
          }
          return result;
        });

    System.out.println(handled.join()); // will throw CompletionException


    executor.shutdown();
  }

  // ---- Async wrapper ----
  static class AsyncService {
    private final Executor executor;

    AsyncService(Executor executor) {
      this.executor = executor;
    }

    <T> CompletableFuture<T> asyncFetch(Supplier<T> supplier) {
      return CompletableFuture.supplyAsync(supplier, executor);
    }
  }

  // ---- Fake services (simulate latency) ----
  static PersonaDto fetchPersona() {
    sleep(500);
    return new PersonaDto("123", "STANDARD");
  }

  static CountryDto fetchCountry(Long id) {
    sleep(700);
    return new CountryDto(id, "Pakistan");
  }

  static void sleep(long ms) {
    try { Thread.sleep(ms); }
    catch (InterruptedException e) { Thread.currentThread().interrupt(); }
  }

  // ---- Simple DTOs ----
  record PersonaDto(String id, String serviceType) {}
  record CountryDto(Long id, String name) {}
}
