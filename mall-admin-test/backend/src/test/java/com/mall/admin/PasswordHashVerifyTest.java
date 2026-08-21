package com.mall.admin;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Verify seed password hashes in database/data.sql match expected plaintexts.
 */
class PasswordHashVerifyTest {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    void adminPasswordHashMatches() {
        String hash = "$2b$10$nZoatxAwL/KBmuiT9eQ.d.v/8gq60m9cWtGBmvr8aMCQZXFi6sZJ6";
        Assertions.assertTrue(encoder.matches("Admin@123", hash));
    }

    @Test
    void userPasswordHashMatches() {
        String hash = "$2b$10$auzI3kFwvdGmVVUojb/vaeM81tvCIiHC1/TNMl8sor99yiYxPkAba";
        Assertions.assertTrue(encoder.matches("User@123", hash));
    }
}
