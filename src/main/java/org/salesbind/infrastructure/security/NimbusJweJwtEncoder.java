package org.salesbind.infrastructure.security;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtEncodingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
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

public class NimbusJweJwtEncoder implements JwtEncoder {

    private final SecretKey cekKey;

    public NimbusJweJwtEncoder(String base64Secret) {
        Objects.requireNonNull(base64Secret, "base64Secret is required");
        byte[] keyBytes = Base64.getDecoder().decode(base64Secret);
        if (keyBytes.length != 32) {
            throw new IllegalArgumentException("Key must be 256 bits (32 bytes) for A256GCM");
        }
        this.cekKey = new SecretKeySpec(keyBytes, "AES");
    }

    @Override
    public Jwt encode(JwtEncoderParameters parameters) throws JwtEncodingException {
        JwtClaimsSet springClaims = parameters.getClaims();
        Map<String, Object> claimsMap = new HashMap<>(springClaims.getClaims());

        Instant issuedAt = parseInstant(claimsMap.get("iat"));
        if (issuedAt == null) {
            issuedAt = Instant.now();
        }
        Instant expiresAt = parseInstant(claimsMap.get("exp"));

        var nimbusClaimsBuilder = new JWTClaimsSet.Builder();

        Object iss = claimsMap.remove("iss");
        if (iss instanceof String) nimbusClaimsBuilder.issuer((String) iss);

        Object sub = claimsMap.remove("sub");
        if (sub instanceof String) nimbusClaimsBuilder.subject((String) sub);

        Object aud = claimsMap.remove("aud");
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

        Date expDate = toDate(extractAndRemove(claimsMap, "exp"));
        if (expDate != null) nimbusClaimsBuilder.expirationTime(expDate);

        Date iatDate = toDate(extractAndRemove(claimsMap, "iat"));
        if (iatDate != null) nimbusClaimsBuilder.issueTime(iatDate);
        else nimbusClaimsBuilder.issueTime(Date.from(issuedAt));

        Date nbfDate = toDate(extractAndRemove(claimsMap, "nbf"));
        if (nbfDate != null) nimbusClaimsBuilder.notBeforeTime(nbfDate);

        Object jti = extractAndRemove(claimsMap, "jti");
        if (jti != null) nimbusClaimsBuilder.jwtID(String.valueOf(jti));

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

            Map<String, Object> jwtClaimsForSpring = new HashMap<>();
            jwtClaimsForSpring.putAll(springClaims.getClaims()); // maintain caller's format

            // Ensures exp/iat exist (Instant) — Spring can extract them via Jwt.getExpiresAt()
            jwtClaimsForSpring.put("iat", issuedAt);
            jwtClaimsForSpring.put("exp", expiresAt);

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
        return (i == null) ? null : Date.from(i);
    }
}
