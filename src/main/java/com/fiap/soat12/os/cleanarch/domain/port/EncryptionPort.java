package com.fiap.soat12.os.cleanarch.domain.port;

public interface EncryptionPort {

    String hash(String rawValue);

    boolean checkMatch(String rawValue, String hashedValue);
}