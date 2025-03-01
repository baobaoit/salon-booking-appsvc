package com.hesmantech.salonbooking.security.utils;

import java.security.SecureRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class KeyGenerator {
    private static final SecureRandom random = new SecureRandom();
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private KeyGenerator() {
        throw new IllegalStateException("Utility class");
    }

    public static String randomString(int len) {
        return IntStream.range(0, len)
                .map(ignored -> random.nextInt(ALPHABET.length()))
                .mapToObj(ALPHABET::charAt)
                .map(String::valueOf)
                .collect(Collectors.joining());
    }
}
