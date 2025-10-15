package com.bwp.utils;


import com.bwp.data.account.User;

import java.security.SecureRandom;

public class Token {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int TOKEN_LENGTH = 32;
    private static final SecureRandom random = new SecureRandom();

    private final String secret;
    private final User user;
    private final long created;


    public Token(User user) {
        this.secret = generateSecret();
        this.user = user;
        created = System.currentTimeMillis();
    }

    private String generateSecret() {
        StringBuilder token = new StringBuilder(TOKEN_LENGTH);
        for (int i = 0; i < TOKEN_LENGTH; i++) {
            token.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return token.toString();
    }

    public final String secret() {
        return secret;
    }

    public boolean expired() {
        long currentTime = System.currentTimeMillis();
        // Token is considered expired if it was created more than 24 hours ago
        return (currentTime - created) > TokenUtils.TOKEN_EXPIRATION_TIME;
    }

    public User user() {
        return user;
    }
}
