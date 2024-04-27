package com.HelloWay.HelloWay.Security.Jwt;

import com.HelloWay.HelloWay.payload.Value;
import com.HelloWay.HelloWay.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.HelloWay.HelloWay.entities.ERole.ROLE_GUEST;
import static com.HelloWay.HelloWay.entities.ERole.ROLE_USER;


@Component
public class CustomSessionRegistry implements SessionRegistry {

    private final ConcurrentMap<String, SessionInformation> sessionInformationMap = new ConcurrentHashMap<>();
    private final Map<String, String> usersInformationMap = new LinkedHashMap<>();

    private final Map<String, Value> usersConnectedWithTablesMap = new LinkedHashMap<>();

    @Autowired
    private    SessionUtils sessionUtils;


    @Override
    public void registerNewSession(String sessionId, Object principal) {
        if (principal instanceof UserDetailsImpl) {
            UserDetailsImpl user = (UserDetailsImpl) principal;
            SessionInformation sessionInformation = new SessionInformation(user, sessionId, new Date());
            sessionInformationMap.put(sessionId, sessionInformation);
        }
    }


    public void setNewUserOnTable(String sessionId,String tableId) {
            usersInformationMap.put(sessionId, tableId);
    }

    public void setNewUserOnTableWithRole(String sessionId,Value table_role) {
        usersConnectedWithTablesMap.put(sessionId, table_role);
    }

    @Override
    public void removeSessionInformation(String sessionId) {
        SessionInformation sessionInformation = sessionInformationMap.get(sessionId);
        if (sessionInformation != null) {
            sessionInformation.expireNow();
            sessionInformationMap.remove(sessionId);
        }
    }


    //we will use this after passing a command
    public void removeUserFromTable(String sessionId) {
        if (usersInformationMap.containsKey(sessionId)) {
            sessionInformationMap.remove(sessionId);
        }
        else System.out.println("user not sated in this table");
    }

    public void removeUserFromTableWithRole(String sessionId) {
        if (usersConnectedWithTablesMap.containsKey(sessionId)) {
            usersConnectedWithTablesMap.remove(sessionId);
        }
        else System.out.println("user not sated in this table");
    }

    @Override
    public SessionInformation getSessionInformation(String sessionId) {
        return sessionInformationMap.get(sessionId);
    }

    @Override
    public List<Object> getAllPrincipals() {
        List<Object> principals = new ArrayList<>();
        for (SessionInformation sessionInformation : sessionInformationMap.values()) {
            if (sessionInformation != null && sessionInformation.isExpired()) {
                principals.add(sessionInformation.getPrincipal());
            }
        }
        return principals;
    }

    @Override
    public List<SessionInformation> getAllSessions(Object principal, boolean includeExpiredSessions) {
        List<SessionInformation> sessions = new ArrayList<>();
        for (SessionInformation sessionInformation : sessionInformationMap.values()) {
         //   i have remove this  down
            if (sessionInformation != null && ((UserDetailsImpl) sessionInformation.getPrincipal()).getUsername().equals(principal) ) {
                if (includeExpiredSessions || !sessionInformation.isExpired()) {
                    sessions.add(sessionInformation);
                }
            }
        }
        return sessions;
    }



    public List<String> getAllUsersSessionsIdSatedInThisTable(String tableId){
        List<String> sessionsInThisTable = new ArrayList<>();
        for (String sessionId : usersInformationMap.keySet()){
            if (sessionId != null && usersInformationMap.get(sessionId).equals(tableId)){
                sessionsInThisTable.add(sessionId);
            }
        }
        return sessionsInThisTable;
    }

    public List<String> getAllUsersSessionsIdSatedInThisTableWithRole(String tableId){
        List<String> sessionsInThisTable = new ArrayList<>();
        for (String sessionId : usersConnectedWithTablesMap.keySet()){
            if (sessionId != null && (usersConnectedWithTablesMap.get(sessionId).getTableId()).equals(tableId)){
                sessionsInThisTable.add(sessionId);
            }
        }
        return sessionsInThisTable;
    }

    public List<String> getOurUsersSessionsIdSatedInThisTable(String tableId){
        List<String> sessionsInThisTable = new ArrayList<>();
        for (String sessionId : usersConnectedWithTablesMap.keySet()){
            if (sessionId != null && (usersConnectedWithTablesMap.get(sessionId).getTableId()).equals(tableId) && (usersConnectedWithTablesMap.get(sessionId).getRole().equals(ROLE_USER.toString()))){
                sessionsInThisTable.add(sessionId);
            }
        }
        return sessionsInThisTable;
    }

    public List<String> getGuestsSessionsIdSatedInThisTable(String tableId){
        List<String> sessionsInThisTable = new ArrayList<>();
        for (String sessionId : usersConnectedWithTablesMap.keySet()){
            if (sessionId != null && (usersConnectedWithTablesMap.get(sessionId).getTableId()).equals(tableId) && (usersConnectedWithTablesMap.get(sessionId).getRole().equals(ROLE_GUEST.toString()))){
                sessionsInThisTable.add(sessionId);
            }
        }
        return sessionsInThisTable;
    }

    @Override
    public void refreshLastRequest(String sessionId) {
        SessionInformation sessionInformation = sessionInformationMap.get(sessionId);
        if (sessionInformation != null) {
            sessionInformation.refreshLastRequest();
        }
    }

    // remove users connected or sated with this tableId
 /*   public void removeUsersWhenCommandIsPayed(String tableId){
        List<String> sessionIds = new ArrayList<>();
        for (String key : usersConnectedWithTablesMap.keySet()){
            if (usersConnectedWithTablesMap.get(key).getTableId().equals(tableId)){
                if (usersConnectedWithTablesMap.get(key).getRole().equals(ROLE_GUEST)){
                    sessionIds.add(key);
                }
                usersConnectedWithTablesMap.remove(key);
            }
        }
        sessionUtils.disconnectUsers(sessionIds);
    }

  */
    // remove users connected or seated with this tableId
    public void removeUsersWhenCommandIsPayed(String tableId) {
        List<String> sessionIds = new ArrayList<>();
        Iterator<Map.Entry<String, Value>> iterator = usersConnectedWithTablesMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Value> entry = iterator.next();
            String sessionId = entry.getKey();
            Value value = entry.getValue();
            if (value.getTableId().equals(tableId) && value.getRole().equals(ROLE_GUEST.toString())) {
                sessionIds.add(sessionId);
                iterator.remove();
            }
        }
        sessionUtils.disconnectUsers(sessionIds);
    }
}
