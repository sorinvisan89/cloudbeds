package com.cloudbeds.demo.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.security.Key;
import java.util.Base64;

@Converter
public class PasswordConverter implements AttributeConverter<String, String> {

    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final String ENCRYPTION_ALGORITHM = "AES";

    private final byte[] encryptionKey;

    @Autowired
    public PasswordConverter(@Value("${password.encryption.key}") final String encryptionKey) {
        this.encryptionKey = encryptionKey.getBytes();
    }

    @Override
    public String convertToDatabaseColumn(final String toEncode) {
        final Key key = new SecretKeySpec(this.encryptionKey, ENCRYPTION_ALGORITHM);
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            return Base64.getEncoder().encodeToString(cipher.doFinal(toEncode.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String convertToEntityAttribute(final String dbData) {
        final Key key = new SecretKeySpec(this.encryptionKey, ENCRYPTION_ALGORITHM);
        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);

            return new String(cipher.doFinal(Base64.getDecoder().decode(dbData)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
