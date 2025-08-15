package org.salesbind.service;

import org.salesbind.entity.RegistrationAttempt;
import org.salesbind.entity.VerificationCode;
import org.salesbind.infrastructure.configuration.RegistrationProperties;
import org.salesbind.infrastructure.security.OneTimeCodeGenerator;
import org.salesbind.repository.RegistrationAttemptRepository;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.UUID;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    private static final int VERIFICATION_CODE_LENGTH = 6;

    private final RegistrationAttemptRepository attemptRepository;
    private final RegistrationProperties registrationProperties;
    private final OneTimeCodeGenerator oneTimeCodeGenerator;

    public RegistrationServiceImpl(RegistrationAttemptRepository attemptRepository,
            RegistrationProperties registrationProperties, OneTimeCodeGenerator oneTimeCodeGenerator) {
        this.attemptRepository = attemptRepository;
        this.registrationProperties = registrationProperties;
        this.oneTimeCodeGenerator = oneTimeCodeGenerator;
    }

    @Override
    public String requestEmailVerification(String email) {
        RegistrationAttempt attempt = attemptRepository.findByEmail(email)
                .orElseGet(() -> new RegistrationAttempt(UUID.randomUUID().toString(), email));

        var verificationCode = new VerificationCode(oneTimeCodeGenerator.generate(VERIFICATION_CODE_LENGTH));
        Instant expiresAt = Instant.now().plus(registrationProperties.getTtl());
        attempt.assignVerificationCode(verificationCode, expiresAt);

        attemptRepository.save(attempt, registrationProperties.getTtl());

        return attempt.getProvisionId();
    }
}
