package com.web.Mongo.util;

import com.mongodb.client.model.vault.DataKeyOptions;
import com.mongodb.client.model.vault.EncryptOptions;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EncryptionUtil {

    private static final String DETERMINISTIC = "AEAD_AES_256_CBC_HMAC_SHA_512-Deterministic";
    private static final String RANDOM = "AEAD_AES_256_CBC_HMAC_SHA_512-Random";


    public EncryptOptions deterministic(String keyAltName) {
        return (new EncryptOptions("AEAD_AES_256_CBC_HMAC_SHA_512-Deterministic")).keyAltName(keyAltName);
    }

    public EncryptOptions random(String keyAltName) {
        return (new EncryptOptions("AEAD_AES_256_CBC_HMAC_SHA_512-Random")).keyAltName(keyAltName);
    }

    public DataKeyOptions keyAltName(String altName) {
        return (new DataKeyOptions()).keyAltNames(List.of(altName));
    }
}
