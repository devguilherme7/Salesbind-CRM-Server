package org.salesbind.infrastructure.web;

import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticationStateRepository {

    Optional<String> loadAccessToken(HttpServletRequest request);

    void saveAccessToken(String accessToken, HttpServletResponse response);

    void removeAccessToken(HttpServletRequest request, HttpServletResponse response);
}
