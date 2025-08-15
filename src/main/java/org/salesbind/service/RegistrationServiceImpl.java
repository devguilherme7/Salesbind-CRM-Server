package org.salesbind.service;

import org.salesbind.dto.CompleteRegistrationRequest;
import org.salesbind.entity.AppUser;
import org.salesbind.entity.Organization;
import org.salesbind.entity.OrganizationMember;
import org.salesbind.entity.RegistrationAttempt;
import org.salesbind.entity.VerificationCode;
import org.salesbind.exception.EmailNotVerifiedException;
import org.salesbind.exception.InvalidVerificationCodeException;
import org.salesbind.exception.RegistrationAttemptNotFoundException;
import org.salesbind.exception.TooManyFailedAttemptsException;
import org.salesbind.infrastructure.configuration.RegistrationProperties;
import org.salesbind.infrastructure.security.OneTimeCodeGenerator;
import org.salesbind.repository.AppUserRepository;
import org.salesbind.repository.OrganizationMemberRepository;
import org.salesbind.repository.OrganizationRepository;
import org.salesbind.repository.RegistrationAttemptRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.UUID;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    private static final int VERIFICATION_CODE_LENGTH = 6;

    private final RegistrationAttemptRepository attemptRepository;
    private final RegistrationProperties registrationProperties;
    private final OneTimeCodeGenerator oneTimeCodeGenerator;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppUserRepository appUserRepository;
    private final OrganizationMemberRepository organizationMemberRepository;

    public RegistrationServiceImpl(RegistrationAttemptRepository attemptRepository,
            RegistrationProperties registrationProperties, OneTimeCodeGenerator oneTimeCodeGenerator,
            OrganizationRepository organizationRepository, PasswordEncoder passwordEncoder,
            AppUserRepository appUserRepository, OrganizationMemberRepository organizationMemberRepository) {
        this.attemptRepository = attemptRepository;
        this.registrationProperties = registrationProperties;
        this.oneTimeCodeGenerator = oneTimeCodeGenerator;
        this.organizationRepository = organizationRepository;
        this.passwordEncoder = passwordEncoder;
        this.appUserRepository = appUserRepository;
        this.organizationMemberRepository = organizationMemberRepository;
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

    @Override
    @Transactional
    public void completeRegistration(String provisionId, CompleteRegistrationRequest request) {
        RegistrationAttempt attempt = attemptRepository.findByProvisionId(provisionId)
                .orElseThrow(RegistrationAttemptNotFoundException::new);

        if (!attempt.isVerified()) {
            throw new EmailNotVerifiedException();
        }

        var organization = new Organization(request.organizationName());
        organizationRepository.save(organization);

        String encodedPassword = passwordEncoder.encode(request.password());
        var user = new AppUser(attempt.getEmail(), request.firstName(), request.lastName(), encodedPassword);
        user.verifyEmail();
        appUserRepository.save(user);

        var membership = new OrganizationMember();
        membership.setUser(user);
        membership.setOrganization(organization);
        organizationMemberRepository.save(membership);

        user.addMembership(membership);
        organization.addMember(membership);

        attemptRepository.delete(attempt);
    }
}
