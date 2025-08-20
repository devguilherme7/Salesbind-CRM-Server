package org.salesbind.infrastructure.security.jwt;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtEncodingException;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;

/**
 * A {@link JwtEncoder} that creates encrypted JWTs (JWE) using direct encryption
 * (JWEAlgorithm.DIR) and A256GCM encryption method.
 *
 * <p>
 * This is a low-level infrastructure component responsible for the cryptographic
 * encoding of the token.
 * </p>
 */
public class EncryptedJwtEncoder implements JwtEncoder {

    private static final int AES_KEY_SIZE_BYTES = 32;

    private static final String CLAIM_ISSUER = "iss";
    private static final String CLAIM_SUBJECT = "sub";
    private static final String CLAIM_AUDIENCE = "aud";
    private static final String CLAIM_EXPIRATION = "exp";
    private static final String CLAIM_ISSUED_AT = "iat";
    private static final String CLAIM_NOT_BEFORE = "nbf";
    private static final String CLAIM_JWT_ID = "jti";

    private final SecretKey cekKey;

    public EncryptedJwtEncoder(String base64Secret) {
        Objects.requireNonNull(base64Secret, "base64Secret is required");
        byte[] keyBytes = Base64.getDecoder().decode(base64Secret);
        if (keyBytes.length != AES_KEY_SIZE_BYTES) {
            throw new IllegalArgumentException("Key must be 256 bits (32 bytes) for A256GCM");
        }
        this.cekKey = new SecretKeySpec(keyBytes, "AES");
    }

    @Override
    public Jwt encode(JwtEncoderParameters parameters) throws JwtEncodingException {
        JwtClaimsSet springClaims = parameters.getClaims();
        Map<String, Object> claimsMap = new HashMap<>(springClaims.getClaims());

        Instant issuedAt = parseInstant(claimsMap.get(CLAIM_ISSUED_AT));
        if (issuedAt == null) {
            issuedAt = Instant.now();
        }
        Instant expiresAt = parseInstant(claimsMap.get(CLAIM_EXPIRATION));

        var nimbusClaimsBuilder = new JWTClaimsSet.Builder();

        Object iss = claimsMap.remove(CLAIM_ISSUER);
        if (iss instanceof String) {
            nimbusClaimsBuilder.issuer((String) iss);
        }

        Object sub = claimsMap.remove(CLAIM_SUBJECT);
        if (sub instanceof String) {
            nimbusClaimsBuilder.subject((String) sub);
        }

        Object aud = claimsMap.remove(CLAIM_AUDIENCE);
        if (aud != null) {
            if (aud instanceof Collection<?>) {
                @SuppressWarnings("unchecked")
                var c = (Collection<Object>) aud;

                List<String> audList = new ArrayList<>();
                for (Object o : c) {
                    audList.add(String.valueOf(o));
                }
                nimbusClaimsBuilder.audience(audList);
            } else {
                nimbusClaimsBuilder.audience(Collections.singletonList(String.valueOf(aud)));
            }
        }

        Date expDate = toDate(extractAndRemove(claimsMap, CLAIM_EXPIRATION));
        if (expDate != null) {
            nimbusClaimsBuilder.expirationTime(expDate);
        }

        Date iatDate = toDate(extractAndRemove(claimsMap, CLAIM_ISSUED_AT));
        if (iatDate != null) {
            nimbusClaimsBuilder.issueTime(iatDate);
        } else {
            nimbusClaimsBuilder.issueTime(Date.from(issuedAt));
        }

        Date nbfDate = toDate(extractAndRemove(claimsMap, CLAIM_NOT_BEFORE));
        if (nbfDate != null) {
            nimbusClaimsBuilder.notBeforeTime(nbfDate);
        }

        Object jti = extractAndRemove(claimsMap, CLAIM_JWT_ID);
        if (jti != null) {
            nimbusClaimsBuilder.jwtID(String.valueOf(jti));
        }

        for (Map.Entry<String, Object> e : claimsMap.entrySet()) {
            nimbusClaimsBuilder.claim(e.getKey(), e.getValue());
        }

        JWTClaimsSet nimbusClaims = nimbusClaimsBuilder.build();

        JWEHeader header = new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A256GCM).build();

        try {
            var encryptedJWT = new EncryptedJWT(header, nimbusClaims);

            var encrypter = new DirectEncrypter(cekKey);
            encryptedJWT.encrypt(encrypter);

            String tokenValue = encryptedJWT.serialize();

            Map<String, Object> headersMap = new HashMap<>(encryptedJWT.getHeader().toJSONObject());

            // maintain caller's format
            Map<String, Object> jwtClaimsForSpring = new HashMap<>(springClaims.getClaims());

            // Ensures exp/iat exist (Instant) — Spring can extract them via Jwt.getExpiresAt()
            jwtClaimsForSpring.put(CLAIM_ISSUED_AT, issuedAt);
            jwtClaimsForSpring.put(CLAIM_EXPIRATION, expiresAt);

            // Build the Spring Jwt
            return new Jwt(tokenValue, issuedAt, expiresAt, headersMap, jwtClaimsForSpring);
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    private static Instant parseInstant(Object o) {
        switch (o) {
            case null -> {
                return null;
            }
            case Instant instant -> {
                return instant;
            }
            case Date date -> {
                return date.toInstant();
            }
            case Number number -> {
                return Instant.ofEpochSecond(number.longValue());
            }
            case String s -> {
                try {
                    return Instant.parse(s);
                } catch (Exception ignored) {
                    // try to parse as epoch seconds from a string
                    try {
                        long epoch = Long.parseLong(s);
                        return Instant.ofEpochSecond(epoch);
                    } catch (Exception ignored2) {
                        return null;
                    }
                }
            }
            default -> {
            }
        }
        return null;
    }

    private static Object extractAndRemove(Map<String, Object> m, String key) {
        if (m.containsKey(key)) {
            return m.remove(key);
        }
        return null;
    }

    private static Date toDate(Object o) {
        Instant i = parseInstant(o);
        if (i == null) {
            return null;
        }
        return Date.from(i);
    }
}
