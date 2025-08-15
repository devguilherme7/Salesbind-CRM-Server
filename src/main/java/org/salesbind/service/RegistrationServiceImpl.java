package org.salesbind.service;

import org.salesbind.entity.RegistrationAttempt;
import org.salesbind.entity.VerificationCode;
import org.salesbind.exception.InvalidVerificationCodeException;
import org.salesbind.exception.RegistrationAttemptNotFoundException;
import org.salesbind.exception.TooManyFailedAttemptsException;
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

    @Override
    public void verifyCode(String provisionId, String verificationCode) {
        RegistrationAttempt attempt = attemptRepository.findByProvisionId(provisionId)
                .orElseThrow(RegistrationAttemptNotFoundException::new);

        if (attempt.isVerified()) {
            return; // Success. Idempotent
        }

        if (attempt.hasExceededMaxAttempts(registrationProperties.getOneTimeCode().getMaxAttempts())) {
            attemptRepository.delete(attempt);
            throw new TooManyFailedAttemptsException();
        }

        var verificationCodeObj = new VerificationCode(verificationCode);
        boolean verified = attempt.verifyCode(verificationCodeObj);

        if (!verified) {
            attemptRepository.save(attempt, registrationProperties.getTtl());
            throw new InvalidVerificationCodeException();
        }

        attemptRepository.save(attempt, registrationProperties.getTtl());
    }
}
