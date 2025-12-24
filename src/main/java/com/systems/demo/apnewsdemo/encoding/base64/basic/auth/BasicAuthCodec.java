package com.systems.demo.apnewsdemo.encoding.base64.basic.auth;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class BasicAuthCodec {

  public static void main(String[] args) {
    var encoded = "YI77LpS1G2qFdRBxI7nH5OcJJWMrFF0a81xnCGTrk1c=";

    // Decode the known Base64 value
    var decoded = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
    System.out.println("Decoded: " + decoded);

    // If a plain-text value is supplied, encode it
    if (args.length > 0) {
      var plain = args[0];
      var reEncoded =
          Base64.getEncoder().encodeToString(plain.getBytes(StandardCharsets.UTF_8));
      System.out.println("Encoded: " + reEncoded);
    } else {
      System.out.println("Pass a plain-text argument to encode it.");
    }
  }
}
