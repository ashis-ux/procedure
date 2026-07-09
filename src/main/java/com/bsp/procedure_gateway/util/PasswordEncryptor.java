package com.bsp.procedure_gateway.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.stereotype.Component;

@Component
public class PasswordEncryptor {

    public String encode(String password) {

        if (password == null) {
            return null;
        }

        return Base64.getEncoder()
                .encodeToString(
                    password.getBytes(StandardCharsets.UTF_8)
                );
    }


    public String decode(String encodedPassword) {

        if (encodedPassword == null) {
            return null;
        }

        byte[] decoded =
                Base64.getDecoder()
                .decode(encodedPassword);

        return new String(decoded, StandardCharsets.UTF_8);
    }
}