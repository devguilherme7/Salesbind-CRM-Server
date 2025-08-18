package org.salesbind.infrastructure.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface RegistrationStateRepository {

    void saveRegistrationAttemptSessionId(String sessionId, HttpServletResponse response);

    void removeRegistrationState(HttpServletRequest request, HttpServletResponse response);
}
