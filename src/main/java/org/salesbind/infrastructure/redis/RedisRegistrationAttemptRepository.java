package org.salesbind.infrastructure.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.salesbind.entity.RegistrationAttempt;
import org.salesbind.repository.RegistrationAttemptRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import java.time.Duration;
import java.util.Optional;

@Repository
public class RedisRegistrationAttemptRepository implements RegistrationAttemptRepository {

    private static final String REGISTRATION_ATTEMPT_KEY = "registration:attempt:";
    private static final String REGISTRATION_EMAIL_INDEX_KEY = "registration:email:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisRegistrationAttemptRepository(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void save(RegistrationAttempt attempt, Duration ttl) {
        redisTemplate.opsForValue().set(key(attempt.getProvisionId()), attempt, ttl);
        redisTemplate.opsForValue().set(emailIndex(attempt.getEmail()), attempt.getProvisionId(), ttl);
    }

    @Override
    public Optional<RegistrationAttempt> findByProvisionId(String provisionId) {
        Object rawObject = redisTemplate.opsForValue().get(key(provisionId));
        if (rawObject == null) {
            return Optional.empty();
        }

        RegistrationAttempt attempt = objectMapper.convertValue(rawObject, RegistrationAttempt.class);
        return Optional.of(attempt);
    }

    @Override
    public Optional<RegistrationAttempt> findByEmail(String email) {
        var provisionId = (String) redisTemplate.opsForValue().get(emailIndex(email));
        return Optional.ofNullable(provisionId).flatMap(this::findByProvisionId);
    }

    private String key(String id) {
        return REGISTRATION_ATTEMPT_KEY + id;
    }

    private String emailIndex(String email) {
        return REGISTRATION_EMAIL_INDEX_KEY + email;
    }
}
