package org.salesbind.service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.salesbind.dto.CompleteRegistrationRequest;
import org.salesbind.entity.AppUser;
import org.salesbind.entity.Organization;
import org.salesbind.entity.OrganizationMember;
import org.salesbind.entity.RegistrationAttempt;
import org.salesbind.entity.VerificationCode;
import org.salesbind.exception.CodeRequestTooSoonException;
import org.salesbind.exception.EmailAlreadyRegisteredException;
import org.salesbind.exception.EmailAlreadyVerifiedException;
import org.salesbind.exception.EmailNotVerifiedException;
import org.salesbind.exception.InvalidRegistrationAttempt;
import org.salesbind.exception.InvalidVerificationCodeException;
import org.salesbind.exception.TooManyFailedAttemptsException;
import org.salesbind.infrastructure.configuration.RegistrationProperties;
import org.salesbind.infrastructure.email.EmailService;
import org.salesbind.infrastructure.security.AppPasswordEncoder;
import org.salesbind.infrastructure.security.OneTimeCodeGenerator;
import org.salesbind.repository.AppUserRepository;
import org.salesbind.repository.OrganizationMemberRepository;
import org.salesbind.repository.OrganizationRepository;
import org.salesbind.repository.RegistrationAttemptRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    private static final int VERIFICATION_CODE_LENGTH = 6;

    private final RegistrationAttemptRepository attemptRepository;
    private final RegistrationProperties registrationProperties;
    private final OneTimeCodeGenerator oneTimeCodeGenerator;
    private final OrganizationRepository organizationRepository;
    private final AppPasswordEncoder passwordEncoder;
    private final AppUserRepository appUserRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final EmailService emailService;

    public RegistrationServiceImpl(RegistrationAttemptRepository attemptRepository,
            RegistrationProperties registrationProperties, OneTimeCodeGenerator oneTimeCodeGenerator,
            OrganizationRepository organizationRepository, AppPasswordEncoder passwordEncoder,
            AppUserRepository appUserRepository, OrganizationMemberRepository organizationMemberRepository,
            EmailService emailService) {
        this.attemptRepository = attemptRepository;
        this.registrationProperties = registrationProperties;
        this.oneTimeCodeGenerator = oneTimeCodeGenerator;
        this.organizationRepository = organizationRepository;
        this.passwordEncoder = passwordEncoder;
        this.appUserRepository = appUserRepository;
        this.organizationMemberRepository = organizationMemberRepository;
        this.emailService = emailService;
    }

    @Override
    public String requestEmailVerification(String email) {
        if (appUserRepository.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException();
        }

        RegistrationAttempt attempt = attemptRepository.findByEmail(email)
                .orElseGet(() -> new RegistrationAttempt(UUID.randomUUID().toString(), email));

        if (attempt.isVerified()) {
            throw new EmailAlreadyVerifiedException();
        }

        Duration cooldownPeriod = registrationProperties.getOneTimeCode().getRequestCooldown();
        if (!attempt.canRequestNewCode(cooldownPeriod)) {
            Duration remaining = attempt.getRemainingCooldown(cooldownPeriod);
            throw new CodeRequestTooSoonException(remaining);
        }

        String verificationCode = oneTimeCodeGenerator.generate(VERIFICATION_CODE_LENGTH);
        var verificationCodeObj = new VerificationCode(verificationCode);

        Instant expiresAt = Instant.now().plus(registrationProperties.getTtl());
        attempt.assignVerificationCode(verificationCodeObj, expiresAt);

        attemptRepository.save(attempt, registrationProperties.getTtl());
        emailService.sendVerificationCode(email, verificationCode);

        return attempt.getProvisionId();
    }

    @Override
    public void verifyCode(String provisionId, String verificationCode) {
        RegistrationAttempt attempt = attemptRepository.findByProvisionId(provisionId)
                .orElseThrow(InvalidRegistrationAttempt::new);

        if (attempt.isVerified()) {
            return;
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
                .orElseThrow(InvalidRegistrationAttempt::new);

        if (!attempt.isVerified()) {
            throw new EmailNotVerifiedException();
        }

        if (appUserRepository.existsByEmail(attempt.getEmail())) {
            throw new EmailAlreadyRegisteredException();
        }

        Organization organization = Organization.create(request.organizationName());
        organization = organizationRepository.save(organization);

        String encodedPassword = passwordEncoder.encode(request.password());
        AppUser user = AppUser.create(attempt.getEmail(), request.firstName(), request.lastName(), encodedPassword);
        user.verifyEmail();
        user = appUserRepository.save(user);

        OrganizationMember membership = OrganizationMember.create(organization, user);
        organizationMemberRepository.save(membership);

        attemptRepository.delete(attempt);
    }
}
