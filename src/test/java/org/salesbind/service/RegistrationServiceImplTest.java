package org.salesbind.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.salesbind.dto.CompleteRegistrationRequest;
import org.salesbind.entity.AppUser;
import org.salesbind.entity.Organization;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceImplTest {

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_CODE = "789321";
    private static final String PROVISION_ID = "testid";

    @Mock
    private RegistrationAttemptRepository attemptRepository;

    @Mock
    private RegistrationProperties registrationProperties;

    @Mock
    private RegistrationProperties.OneTimeCodeProperties oneTimeCodeProperties;

    @Mock
    private OneTimeCodeGenerator oneTimeCodeGenerator;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private AppPasswordEncoder passwordEncoder;

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private OrganizationMemberRepository organizationMemberRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private RegistrationServiceImpl registrationService;

    @Captor
    private ArgumentCaptor<RegistrationAttempt> attemptCaptor;

    @BeforeEach
    void setUp() {
        lenient().when(oneTimeCodeProperties.getMaxAttempts()).thenReturn(5);
        lenient().when(registrationProperties.getTtl()).thenReturn(Duration.ofMinutes(15));
        lenient().when(registrationProperties.getOneTimeCode()).thenReturn(oneTimeCodeProperties);
    }

    @Nested
    class RequestEmailVerification {

        @Test
        void whenNewUser_shouldCreateAttemptAndSendCode() {
            when(appUserRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
            when(attemptRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
            when(oneTimeCodeGenerator.generate(anyInt())).thenReturn(TEST_CODE);
            doNothing().when(emailService).sendVerificationCode(anyString(), anyString());

            String result = registrationService.requestEmailVerification(TEST_EMAIL);

            assertThat(result).isNotNull();
            verify(attemptRepository).save(attemptCaptor.capture(), any());
            RegistrationAttempt savedAttempt = attemptCaptor.getValue();
            assertThat(savedAttempt.getEmail()).isEqualTo(TEST_EMAIL);
            assertThat(savedAttempt.getVerificationCode().getValue()).isEqualTo(TEST_CODE);

            verify(emailService).sendVerificationCode(TEST_EMAIL, TEST_CODE);
        }

        @Test
        void whenEmailExists_shouldThrowException() {
            when(appUserRepository.existsByEmail(TEST_EMAIL)).thenReturn(true);

            assertThatThrownBy(() -> registrationService.requestEmailVerification(TEST_EMAIL))
                    .isInstanceOf(EmailAlreadyRegisteredException.class);
            verify(attemptRepository, never()).save(any(), any());
        }

        @Test
        void whenAttemptAlreadyVerified_shouldThrowException() {
            RegistrationAttempt verifiedAttempt = mock(RegistrationAttempt.class);
            when(verifiedAttempt.isVerified()).thenReturn(true);
            when(attemptRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(verifiedAttempt));

            assertThatThrownBy(() -> registrationService.requestEmailVerification(TEST_EMAIL))
                    .isInstanceOf(EmailAlreadyVerifiedException.class);
        }

        @Test
        void whenWithinCooldown_shouldThrowException() {
            RegistrationAttempt recentAttempt = mock(RegistrationAttempt.class);
            Duration cooldown = Duration.ofMinutes(2);
            when(oneTimeCodeProperties.getRequestCooldown()).thenReturn(cooldown);
            when(recentAttempt.canRequestNewCode(cooldown)).thenReturn(false);
            when(recentAttempt.getRemainingCooldown(cooldown)).thenReturn(Duration.ofSeconds(30));
            when(attemptRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(recentAttempt));

            assertThatThrownBy(() -> registrationService.requestEmailVerification(TEST_EMAIL))
                    .isInstanceOf(CodeRequestTooSoonException.class);
        }
    }

    @Nested
    class VerifyCode {

        @Test
        void whenValidCode_shouldSucceed() {
            var attempt = new RegistrationAttempt(PROVISION_ID, TEST_EMAIL);
            attempt.assignVerificationCode(new VerificationCode(TEST_CODE), Instant.now().plus(Duration.ofMinutes(5)));
            when(attemptRepository.findByProvisionId(PROVISION_ID)).thenReturn(Optional.of(attempt));

            registrationService.verifyCode(PROVISION_ID, TEST_CODE);

            verify(attemptRepository).save(attemptCaptor.capture(), any(Duration.class));
            assertThat(attemptCaptor.getValue().isVerified()).isTrue();
        }

        @Test
        void whenInvalidCode_shouldThrowException() {
            var attempt = new RegistrationAttempt(PROVISION_ID, TEST_EMAIL);
            attempt.assignVerificationCode(new VerificationCode(TEST_CODE), Instant.now().plus(Duration.ofMinutes(5)));
            when(attemptRepository.findByProvisionId(PROVISION_ID)).thenReturn(Optional.of(attempt));

            assertThatThrownBy(() -> registrationService.verifyCode(PROVISION_ID, "wrong-code"))
                    .isInstanceOf(InvalidVerificationCodeException.class);
            verify(attemptRepository).save(attemptCaptor.capture(), any(Duration.class));
            assertThat(attemptCaptor.getValue().getFailedAttempts()).isEqualTo(1);
        }

        @Test
        void whenMaxAttemptsExceeded_shouldThrowExceptionAndDelete() {
            RegistrationAttempt attempt = mock(RegistrationAttempt.class);
            when(attemptRepository.findByProvisionId(PROVISION_ID)).thenReturn(Optional.of(attempt));
            when(attempt.hasExceededMaxAttempts(5)).thenReturn(true);

            assertThatThrownBy(() -> registrationService.verifyCode(PROVISION_ID, TEST_CODE))
                    .isInstanceOf(TooManyFailedAttemptsException.class);
            verify(attemptRepository).delete(attempt);
        }

        @Test
        void whenUnknownProvisionId_shouldThrowException() {
            when(attemptRepository.findByProvisionId(PROVISION_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> registrationService.verifyCode(PROVISION_ID, TEST_CODE))
                    .isInstanceOf(InvalidRegistrationAttempt.class);
        }
    }

    @Nested
    class CompleteRegistration {

        private CompleteRegistrationRequest request;

        @BeforeEach
        void setUp() {
            request = new CompleteRegistrationRequest("Test", "User", "ValidPass123!", "Test Corp");
        }

        @Test
        void whenAttemptIsVerified_shouldCreateUserAndOrg() {
            RegistrationAttempt attempt = mock(RegistrationAttempt.class);
            when(attempt.isVerified()).thenReturn(true);
            when(attempt.getEmail()).thenReturn(TEST_EMAIL);
            when(attemptRepository.findByProvisionId(PROVISION_ID)).thenReturn(Optional.of(attempt));
            when(appUserRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("encoded-pass");

            registrationService.completeRegistration(PROVISION_ID, request);

            verify(organizationRepository).save(any(Organization.class));
            verify(appUserRepository).save(any(AppUser.class));
            verify(organizationMemberRepository).save(any());
            verify(attemptRepository).delete(attempt);
        }

        @Test
        void whenAttemptNotVerified_shouldThrowException() {
            RegistrationAttempt attempt = mock(RegistrationAttempt.class);
            when(attempt.isVerified()).thenReturn(false);
            when(attemptRepository.findByProvisionId(PROVISION_ID)).thenReturn(Optional.of(attempt));

            assertThatThrownBy(() -> registrationService.completeRegistration(PROVISION_ID, request))
                    .isInstanceOf(EmailNotVerifiedException.class);

            verify(appUserRepository, never()).save(any());
        }

        @Test
        void whenEmailBecomesRegistered_shouldThrowException() {
            RegistrationAttempt attempt = mock(RegistrationAttempt.class);
            when(attempt.isVerified()).thenReturn(true);
            when(attempt.getEmail()).thenReturn(TEST_EMAIL);
            when(attemptRepository.findByProvisionId(PROVISION_ID)).thenReturn(Optional.of(attempt));
            when(appUserRepository.existsByEmail(TEST_EMAIL)).thenReturn(true);

            assertThatThrownBy(() -> registrationService.completeRegistration(PROVISION_ID, request))
                    .isInstanceOf(EmailAlreadyRegisteredException.class);

            verify(organizationRepository, never()).save(any());
        }
    }
}
