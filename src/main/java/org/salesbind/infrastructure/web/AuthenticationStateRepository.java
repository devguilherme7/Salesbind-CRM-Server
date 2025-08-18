package org.salesbind.infrastructure.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;

public interface AuthenticationStateRepository {

    Optional<String> loadAccessToken(HttpServletRequest request);

    void saveAccessToken(String accessToken, HttpServletResponse response);

    void removeAccessToken(HttpServletRequest request, HttpServletResponse response);
}
