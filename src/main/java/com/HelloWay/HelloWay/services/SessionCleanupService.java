package com.HelloWay.HelloWay.services;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

import com.HelloWay.HelloWay.Security.Jwt.CustomSessionRegistry;
import com.HelloWay.HelloWay.Security.Jwt.SessionUtils;
import com.HelloWay.HelloWay.entities.Basket;
import com.HelloWay.HelloWay.entities.Board;
import com.HelloWay.HelloWay.entities.Command;
import com.HelloWay.HelloWay.repos.CommandRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class SessionCleanupService {

    private final CommandRepository commandRepository;
    private final SessionRegistry sessionRegistry;
    private final CustomSessionRegistry customSessionRegistry;
    private final SessionUtils sessionUtils;

    public SessionCleanupService(CommandRepository commandRepository, SessionRegistry sessionRegistry, 
                                 CustomSessionRegistry customSessionRegistry, SessionUtils sessionUtils) {
        this.commandRepository = commandRepository;
        this.sessionRegistry = sessionRegistry;
        this.customSessionRegistry = customSessionRegistry;
        this.sessionUtils = sessionUtils;
    }

    @Scheduled(fixedRate = 60000) // Run every 1 minute
    public void cleanUpInactiveSessions() {
        List<Object> allPrincipals = sessionRegistry.getAllPrincipals();

        for (Object principal : allPrincipals) {
            List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, false);

            for (SessionInformation sessionInfo : sessions) {
                if (sessionInfo == null) continue;

                String sessionId = sessionInfo.getSessionId();

                // ✅ Convert Date to Instant
                Instant lastRequestTime = sessionInfo.getLastRequest().toInstant();
                Instant oneMinuteAgo = Instant.now().minus(Duration.ofMinutes(15));

                // ✅ Check if session is inactive for more than 1 minute
                if (lastRequestTime.isBefore(oneMinuteAgo)) {
                    boolean hasActiveCommand = commandRepository.existsBySessionId(sessionId);
                    Command command = commandRepository.findBySessionId(sessionId);
                    if (!hasActiveCommand) {
                        // ❌ No active command → Invalidate session
                        Board board = command.getBasket().getBoard();
                        Basket basket = new Basket();
                        basket.setBoard(board);
                        sessionInfo.expireNow();
                        sessionUtils.disconnectUsers(List.of(sessionId));
                        customSessionRegistry.removeSessionInformation(sessionId);

                        System.out.println("Session " + sessionId + " ended due to inactivity.");
                    } else {
                        // ✅ Session has an active command → Keep it alive
                        System.out.println("Session " + sessionId + " is active because of an active command.");
                    }
                }
            }
        }
    }
}