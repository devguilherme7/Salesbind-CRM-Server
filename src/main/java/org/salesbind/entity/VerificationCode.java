package org.salesbind.entity;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@SuppressWarnings("unused")
public class VerificationCode {

    private String value;

    public VerificationCode(String value) {
        this.value = value;
    }

    protected VerificationCode() {
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        var that = (VerificationCode) obj;

        // Constant-time comparison to prevent timing attacks
        byte[] aBytes = this.value.getBytes(StandardCharsets.UTF_8);
        byte[] bBytes = that.value.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(aBytes, bBytes);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    public String getValue() {
        return value;
    }
}
