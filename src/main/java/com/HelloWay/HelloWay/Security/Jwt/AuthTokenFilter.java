package com.HelloWay.HelloWay.Security.Jwt;

import com.HelloWay.HelloWay.services.UserDetailsImpl;
import com.HelloWay.HelloWay.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserService userDetailsService;

    @Autowired
    private SessionRegistry sessionRegistry;


    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

   @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Check if the user account is activated
                if (!isAccountActivated(userDetails)) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Account not activated");
                    return;
                }

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails,
                                null,
                                userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }

        filterChain.doFilter(request, response);
    }


 /*   @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Check if the current session is the first session
                if (!isFirstSession(request, username)) {
                    SecurityContextHolder.clearContext(); // Clear the authentication context
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Only the first session can access this resource.");
                    return;
                }

                // Set the authentication
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }

        filterChain.doFilter(request, response);
    } */

    // be careful with this
    private boolean isFirstSession(HttpServletRequest request, String username) {
        String sessionId = request.getSession().getId();
        List<SessionInformation> sessions = sessionRegistry.getAllSessions(username, true);
        return sessions.stream()
                .filter(sessionInformation -> !sessionInformation.isExpired())
                .map(SessionInformation::getSessionId)
                .findFirst()
                .map(id -> id.equals(sessionId))
                .orElse(false);
    }


    private String parseJwt(HttpServletRequest request) {
        String jwt = jwtUtils.getJwtFromCookies(request);
        if (jwt == null) {
            // Extract the token from the QR code parameter
            String qrCodeToken = request.getParameter("qrCodeToken");
            if (qrCodeToken != null && !qrCodeToken.isEmpty()) {
                return qrCodeToken;
            }
        }
        return jwt;
    }
    private boolean isAccountActivated(UserDetails userDetails) {
        // Assuming the UserDetailsImpl class has a method to check if the account is activated
        return ((UserDetailsImpl) userDetails).isActivated();
    }

}