package com.HelloWay.HelloWay.controllers;

import com.HelloWay.HelloWay.Security.Jwt.CustomSessionRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {
    @Autowired
    private CustomSessionRegistry sessionRegistry;

    @GetMapping("/validate-session")
    @PreAuthorize("hasAnyRole('WAITER', 'USER', 'GUEST')")
    public String validateSession() {
        String username = getUsername();
        String sessionId = getSessionId();

        if (isFirstSession(username, sessionId)) {
            // This is the first session for the user
            return "First session";
        } else {
            // This is not the first session for the user
            return "Not the first session";
        }
    }

    @GetMapping("/validate-session/for_our_user/{tableId}")
    @PreAuthorize("hasAnyRole('WAITER', 'USER', 'GUEST', 'PROVIDER')")
    public String validateSessionLatest(@PathVariable String tableId) {
        String sessionId = getSessionId();

        if (isFirstUserSatedInThisTable(tableId, sessionId)) {
            // This is the first session for the user
            return "First session";
        } else {
            // This is not the first session for the user
            return "Not the first session";
        }
    }

    @GetMapping("/validate-session/latest/{tableId}")
    @PreAuthorize("hasAnyRole('WAITER', 'USER', 'GUEST')")
    public String validateSessionLatestWithRole(@PathVariable String tableId) {
        String sessionId = getSessionId();

        if (isFirstUserSatedInThisTableWithRole(tableId, sessionId)) {
            // This is the first session for the user
            return "First session";
        } else {
            // This is not the first session for the user
            return "Not the first session";
        }
    }

    private String getUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    private String getSessionId() {
        return RequestContextHolder.currentRequestAttributes().getSessionId();
    }

    private boolean isFirstSession(String username, String sessionId) {
        List<SessionInformation> sessions = sessionRegistry.getAllSessions(username, false);
        return sessions.stream()
                .filter(sessionInformation -> !sessionInformation.isExpired())
                .map(SessionInformation::getSessionId)
                .findFirst()
                .map(id -> id.equals(sessionId))
                .orElse(false);
    }

    private boolean isFirstUserSatedInThisTable(String tableId, String sessionId) {
        List<String> sessions = sessionRegistry.getAllUsersSessionsIdSatedInThisTable(tableId);
        return sessions.stream()
                .findFirst()
                .map(id -> id.equals(sessionId))
                .orElse(false);
    }

    private boolean isFirstUserSatedInThisTableWithRole(String tableId, String sessionId) {
        List<String> usersSessions = sessionRegistry.getOurUsersSessionsIdSatedInThisTable(tableId);
        List<String> guestSessions = sessionRegistry.getGuestsSessionsIdSatedInThisTable(tableId);

        Boolean result = false;

        if (!usersSessions.isEmpty()) {
           result =  usersSessions.stream()
                    .findFirst()
                    .map(id -> id.equals(sessionId))
                    .orElse(false);
        } else {

            result =  guestSessions.stream()
                    .findFirst()
                    .map(id -> id.equals(sessionId))
                    .orElse(false);
        }
        return result ;
    }
}



