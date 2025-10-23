package com.bwp.utils.secrets;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

public class TokenUtils {

    public static final long TOKEN_EXPIRATION_TIME = TimeUnit.MILLISECONDS.convert(90, TimeUnit.DAYS);
    public static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    public static final int TOKEN_LENGTH = 32;
    public static final SecureRandom random = new SecureRandom();


}
