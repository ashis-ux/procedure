package com.bsp.procedure_gateway.config;
 

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class IamPublicKeyProvider {

    private static final Logger log =
            LoggerFactory.getLogger(IamPublicKeyProvider.class);


    @Value("${iam.public-key-url}")
    private String publicKeyUrl;


    private volatile RSAPublicKey publicKey;


    private final Object lock = new Object();

    public RSAPublicKey getPublicKey() {

        if (publicKey == null) {
            refreshPublicKey();
        }

        return publicKey;
    }

    public void refreshPublicKey() {
        synchronized(lock) {
            RSAPublicKey newKey =
                    loadPublicKey();
            publicKey = newKey;
            log.info(
                "IAM public key updated successfully"
            );
        }
    }



    private RSAPublicKey loadPublicKey() {

        try {

            log.info(
                "Fetching IAM public key from {}",
                publicKeyUrl
            );


            RestTemplate restTemplate =
                    new RestTemplate();


            String pem =
                    restTemplate.getForObject(
                            publicKeyUrl,
                            String.class);



            String stripped = pem
                    .replace(
                       "-----BEGIN PUBLIC KEY-----",
                       "")
                    .replace(
                       "-----END PUBLIC KEY-----",
                       "")
                    .replaceAll("\\s+", "");



            byte[] decoded =
                    Base64.getDecoder()
                    .decode(stripped);



            X509EncodedKeySpec spec =
                    new X509EncodedKeySpec(decoded);



            return (RSAPublicKey)
                    KeyFactory
                    .getInstance("RSA")
                    .generatePublic(spec);


        } catch(Exception ex) {

            log.error(
                "Unable to load IAM public key",
                ex
            );

            throw new RuntimeException(
                "IAM public key loading failed",
                ex
            );
        }
    }
}