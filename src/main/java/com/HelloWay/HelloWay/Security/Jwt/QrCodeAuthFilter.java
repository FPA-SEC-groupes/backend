package com.HelloWay.HelloWay.Security.Jwt;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class QrCodeAuthFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    public QrCodeAuthFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        setFilterProcessesUrl("/api/auth/login-qr"); // Update the URL path for authentication
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        String tableIdentifier = obtainTableIdentifier(request);

        // Create an authentication token with the table identifier
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                tableIdentifier, null);

        // Perform authentication
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        // Handle authentication failure
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed");
    }

    private String obtainTableIdentifier(HttpServletRequest request) {
        // Implement the logic to extract the table identifier from the QR code
        // This could involve reading the QR code data from the request, decoding it, and extracting the table identifier
        // Return the extracted table identifier
        // Example implementation assuming the table identifier is passed as a query parameter named "tableId":
        return request.getParameter("tableId");
    }
}
