package com.bwp.utils.secrets;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

/**
 * Utility constants and helpers for token generation and validation.
 * Defines the allowed character set, default length, expiration policy, and
 * a SecureRandom instance used by Token for id generation.
 */
public class TokenUtils {

    /** Default token validity period (90 days) expressed in milliseconds. */
    public static final long TOKEN_EXPIRATION_TIME = TimeUnit.MILLISECONDS.convert(90, TimeUnit.DAYS);
    /** Alphabet used for token id generation. */
    public static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    /** Default length of generated token ids. */
    public static final int TOKEN_LENGTH = 32;
    /** Shared secure PRNG used for token generation. */
    public static final SecureRandom random = new SecureRandom();


}
