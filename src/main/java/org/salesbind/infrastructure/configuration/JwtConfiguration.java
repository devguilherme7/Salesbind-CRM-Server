package org.salesbind.infrastructure.configuration;

import java.util.Base64;

import org.salesbind.infrastructure.security.jwt.EncryptedJwtEncoder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.proc.JWEDecryptionKeySelector;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;

@Configuration
@EnableConfigurationProperties(AuthenticationProperties.class)
public class JwtConfiguration {

    private final AuthenticationProperties authenticationProperties;

    public JwtConfiguration(AuthenticationProperties authenticationProperties) {
        this.authenticationProperties = authenticationProperties;
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return new EncryptedJwtEncoder(authenticationProperties.getSecretKey());
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        byte[] keyBytes = Base64.getDecoder().decode(authenticationProperties.getSecretKey());

        var secretSource = new ImmutableSecret<>(keyBytes);

        var jweKeySelector = new JWEDecryptionKeySelector<>(JWEAlgorithm.DIR, EncryptionMethod.A256GCM, secretSource);
        var jwtProcessor = new DefaultJWTProcessor<>();
        jwtProcessor.setJWEKeySelector(jweKeySelector);

        return new NimbusJwtDecoder(jwtProcessor);
    }
}
