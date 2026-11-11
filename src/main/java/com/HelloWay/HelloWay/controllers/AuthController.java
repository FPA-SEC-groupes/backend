package com.HelloWay.HelloWay.controllers;

import com.HelloWay.HelloWay.DistanceLogic.DistanceCalculator;
import com.HelloWay.HelloWay.Security.Jwt.CustomSessionRegistry;
import com.HelloWay.HelloWay.Security.Jwt.JwtUtils;
import com.HelloWay.HelloWay.Security.Jwt.SessionUtils;
import com.HelloWay.HelloWay.entities.*;
import com.HelloWay.HelloWay.payload.Value;
import com.HelloWay.HelloWay.payload.request.LoginRequest;
import com.HelloWay.HelloWay.payload.request.ResetPasswordRequest;
import com.HelloWay.HelloWay.payload.request.ChangePasswordRequest;
import com.HelloWay.HelloWay.payload.request.ResetPasswordConfirmRequest;
import com.HelloWay.HelloWay.Security.AccessGuard;
import com.HelloWay.HelloWay.Security.QrTokens;
import com.HelloWay.HelloWay.payload.request.SignupRequest;
import com.HelloWay.HelloWay.payload.response.InformationAfterScan;
import com.HelloWay.HelloWay.payload.response.MessageResponse;
import com.HelloWay.HelloWay.payload.response.UserInfoResponse;
import com.HelloWay.HelloWay.repos.BoardRepository;
import com.HelloWay.HelloWay.repos.RoleRepository;
import com.HelloWay.HelloWay.repos.SpaceRepository;
import com.HelloWay.HelloWay.repos.UserRepository;
import com.HelloWay.HelloWay.services.DemandeService;
import com.HelloWay.HelloWay.services.EmailService;
import com.HelloWay.HelloWay.services.SpaceService;
import com.HelloWay.HelloWay.services.UserDetailsImpl;
import com.HelloWay.HelloWay.services.WifiService;
import com.HelloWay.HelloWay.services.ZoneService;
import org.apache.coyote.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.HelloWay.HelloWay.entities.ERole.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    SpaceRepository spaceRepository;
    
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    CustomSessionRegistry customSessionRegistry;

    @Autowired
    SessionUtils  sessionUtils;

    @Autowired
    ZoneService zoneService;

    @Autowired
    EmailService emailService;
    @Autowired
    SpaceService spaceService;

    @Autowired
    private WifiService wifiService;

    @Autowired
    private DemandeService demandeService;

    @Autowired
    private BoardRepository boardRepository;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private static final int RESET_CODE_MINUTES = 15;
    private static final int RESET_MAX_ATTEMPTS = 5;
    private static final String RESET_EMAIL_SENT = "Si un compte existe pour cet e-mail, un code de réinitialisation a été envoyé.";

    @Autowired
    private AccessGuard guard;

    /** Comptes techniques créés pour chaque table (Board{id}) : jamais de connexion par mot de passe. */
    private static boolean isTableAccount(User user) {
        return user.getUsername() != null && user.getUsername().matches("Board\\d+")
                && user.getRoles().stream().anyMatch(r -> r.getName() == ROLE_GUEST);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Optional<User> candidate = userRepository.findByUsername(loginRequest.getUsername());
        if (candidate.isPresent() && isTableAccount(candidate.get())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Bad credentials"));
        }

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Check if the user is activated
        if (!userDetails.isActivated()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account not activated");
        }
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setToken(loginRequest.getToken());
        userRepository.save(user);
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("id", userDetails.getId());
        responseBody.put("name", userDetails.getName());
        responseBody.put("lastname", userDetails.getLastname());
        responseBody.put("birthday", userDetails.getBirthday());
        responseBody.put("phone", userDetails.getPhone());
        responseBody.put("username", userDetails.getUsername());
        responseBody.put("email", userDetails.getEmail());
        responseBody.put("roles", roles);
        responseBody.put("idToken", jwtCookie.getValue());
        responseBody.put("numberOfRestrictions", user.getNumberOfRestrictions());
        responseBody.put("percentage", user.getPercentage());
        responseBody.put("token", user.getToken());
        responseBody.put("space_id", user.getModeratorSpace() != null && user.getModeratorSpace().getId_space() != null ? user.getModeratorSpace().getId_space() : 0);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(responseBody);
    }

    /**
     * Inscription publique = client (ROLE_USER) uniquement.
     * admin / provider : réservé à un ADMIN connecté ; waiter : ADMIN ou PROVIDER connecté.
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        Set<String> strRoles = signUpRequest.getRole() == null ? Set.of() : signUpRequest.getRole();
        Set<String> callerRoles = guard.currentRoles();
        boolean callerIsAdmin = callerRoles.contains("ROLE_ADMIN");
        boolean callerIsProvider = callerRoles.contains("ROLE_PROVIDER");

        Set<Role> roles = new HashSet<>();
        for (String role : strRoles) {
            switch (role) {
                case "admin":
                case "provider":
                    if (!callerIsAdmin) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Error: Only an admin can create this account."));
                    }
                    roles.add(findRole(role.equals("admin") ? ROLE_ADMIN : ROLE_PROVIDER));
                    break;
                case "waiter":
                    if (!callerIsAdmin && !callerIsProvider) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Error: Only a manager can create a waiter."));
                    }
                    roles.add(findRole(ROLE_WAITER));
                    break;
                case "guest":
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Error: Role not allowed."));
                default:
                    roles.add(findRole(ROLE_USER));
            }
        }
        if (roles.isEmpty()) {
            roles.add(findRole(ROLE_USER));
        }

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getName(),
                signUpRequest.getLastname(),
                signUpRequest.getBirthday(),
                signUpRequest.getPhone(),
                signUpRequest.getPercentage(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));
        user.setActivated(true);
        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    private Role findRole(ERole name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request, HttpServletResponse response) {
        HttpSession httpSession = request.getSession(false);
        if (httpSession != null) {
            customSessionRegistry.removeUserFromTableWithRole(httpSession.getId());
            httpSession.invalidate();
        }
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok().body(new MessageResponse("You've been signed out!"));
    }

    /** Vérifie le QR scanné : format idTable-jeton-idZone, table active, bonne zone, bon jeton. */
    private ResponseEntity<?> checkTableQr(QrTokens.Parsed qr) {
        if (qr == null) {
            return ResponseEntity.badRequest().body("Invalid QR code.");
        }
        Optional<Board> tableOptional = boardRepository.findById(qr.tableId);
        if (tableOptional.isEmpty() || !tableOptional.get().isActivated()) {
            return ResponseEntity.ok().body("The table is not active or does not exist.");
        }
        Board board = tableOptional.get();
        if (board.getZone() == null || !Long.valueOf(qr.zoneId).equals(board.getZone().getIdZone())
                || !QrTokens.same(board.getQrToken(), qr.token)) {
            return ResponseEntity.badRequest().body("Invalid QR code.");
        }
        if (!customSessionRegistry.getAllUsersSessionsIdSatedInThisTable(String.valueOf(qr.tableId)).isEmpty()) {
            return ResponseEntity.ok().body("A user is already seated at this table.");
        }
        return null;
    }

    /**
     * Connexion invité à une table (client sans compte).
     * Mode « gps » : la position doit être dans l'espace.
     * Mode « wifi » : le serveur ne voit pas le réseau du téléphone ; la preuve de présence
     * est le jeton secret du QR imprimé sur la table.
     */
    @PostMapping("/signin/qr_Code/{qr_Code}/userLatitude/{userLatitude}/userLongitude/{userLongitude}/{accuracy}")
    public ResponseEntity<?> authenticateUser(
            @PathVariable String qr_Code,
            @PathVariable String userLatitude,
            @PathVariable String userLongitude,
            @PathVariable String accuracy,
            HttpServletRequest request) {

        QrTokens.Parsed qr = QrTokens.parse(qr_Code);
        ResponseEntity<?> invalid = checkTableQr(qr);
        if (invalid != null) return invalid;

        Space space = zoneService.findZoneById(qr.zoneId).getSpace();
        if (!"gps".equals(space.getValidation()) && !"wifi".equals(space.getValidation())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bad credentials");
        }
        if ("gps".equals(space.getValidation())
                && !DistanceCalculator.isTheUserInTheSpaCe(userLatitude, userLongitude, Double.parseDouble(accuracy), space)) {
            return ResponseEntity.ok().body("the user not in the space so we are sorry you cant be connected");
        }

        String idTable = String.valueOf(qr.tableId);
        String userName = "Board" + idTable;
        User tableUser = userRepository.findByUsername(userName).orElseGet(() -> {
            // Mot de passe aléatoire : ce compte ne se connecte que par QR
            User newUser = new User(userName, null, space.getId_space().toString(), null, null, 0, null,
                    encoder.encode(QrTokens.newToken()));
            newUser.setActivated(true);
            Set<Role> roles = new HashSet<>();
            roles.add(findRole(ROLE_GUEST));
            newUser.setRoles(roles);
            return userRepository.save(newUser);
        });

        UserDetailsImpl userDetails = (UserDetailsImpl) UserDetailsImpl.build(tableUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        Value value = new Value(idTable, roles.get(0));
        customSessionRegistry.registerNewSession(sessionId, userDetails);
        customSessionRegistry.setNewUserOnTableWithRole(sessionId, value);
        HttpSession session = request.getSession();
        sessionUtils.addSession(session);
        customSessionRegistry.setNewUserOnTable(sessionId, idTable);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(new UserInfoResponse(userDetails.getId(),
                        userDetails.getName(),
                        userDetails.getLastname(),
                        userDetails.getBirthday(),
                        userDetails.getPhone(),
                        userDetails.getUsername(),
                        userDetails.getEmail(),
                        roles,
                        sessionId));
    }

    /** Client connecté qui scanne le QR d'une table. */
    @PostMapping("/qr_Code_for_app_user/{qr_Code}/userLatitude/{userLatitude}/userLongitude/{userLongitude}/{accuracy}")
    public ResponseEntity<?> setUserInTable(@PathVariable String qr_Code,
                                            @PathVariable String userLatitude,
                                            @PathVariable String userLongitude,
                                            @PathVariable String accuracy) {
        Authentication current = SecurityContextHolder.getContext().getAuthentication();
        Object principal = current == null ? null : current.getPrincipal();
        if (!(principal instanceof UserDetailsImpl)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) principal;

        QrTokens.Parsed qr = QrTokens.parse(qr_Code);
        ResponseEntity<?> invalid = checkTableQr(qr);
        if (invalid != null) return invalid;

        Space space = zoneService.findZoneById(qr.zoneId).getSpace();
        if ("gps".equals(space.getValidation())
                && !DistanceCalculator.isTheUserInTheSpaCe(userLatitude, userLongitude, Double.parseDouble(accuracy), space)) {
            return ResponseEntity.ok().body("The user is not in the space, so we are sorry you can't be seated at this table.");
        }

        String idTable = String.valueOf(qr.tableId);
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        Value value = new Value(idTable, ROLE_USER.toString());
        customSessionRegistry.registerNewSession(sessionId, userDetails);
        customSessionRegistry.setNewUserOnTable(sessionId, idTable);
        customSessionRegistry.setNewUserOnTableWithRole(sessionId, value);

        InformationAfterScan informationAfterScan = new InformationAfterScan(space.getId_space().toString(), idTable, sessionId);
        return ResponseEntity.ok().body(informationAfterScan);
    }

    /** Étape 1 : envoie un code à 6 chiffres (valable 15 min). Même réponse que le compte existe ou non. */
    @PostMapping("/reset-password/email")
    public MessageResponse resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        userRepository.findByEmail(resetPasswordRequest.getEmail()).ifPresent(user -> {
            String code = QrTokens.newSixDigitCode();
            user.setResetCodeHash(encoder.encode(code));
            user.setResetCodeExpiry(LocalDateTime.now().plusMinutes(RESET_CODE_MINUTES));
            user.setResetAttempts(0);
            userRepository.save(user);
            sendResetCodeEmail(user.getEmail(), code);
        });
        return new MessageResponse(RESET_EMAIL_SENT);
    }

    /** Étape 2 : code + nouveau mot de passe. 5 essais maximum par code. */
    @PostMapping("/reset-password/confirm")
    public ResponseEntity<?> confirmResetPassword(@Valid @RequestBody ResetPasswordConfirmRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user == null || user.getResetCodeHash() == null || user.getResetCodeExpiry() == null
                || user.getResetCodeExpiry().isBefore(LocalDateTime.now())
                || user.getResetAttempts() >= RESET_MAX_ATTEMPTS) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Invalid or expired code."));
        }
        if (!encoder.matches(request.getCode(), user.getResetCodeHash())) {
            user.setResetAttempts(user.getResetAttempts() + 1);
            userRepository.save(user);
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Invalid or expired code."));
        }
        user.setPassword(encoder.encode(request.getNewPassword()));
        user.setResetCodeHash(null);
        user.setResetCodeExpiry(null);
        user.setResetAttempts(0);
        userRepository.save(user);
        return ResponseEntity.ok().body(new MessageResponse("Password reset successfully."));
    }

    /** Changement de mot de passe de l'utilisateur connecté (ancien mot de passe obligatoire). */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Long userId = guard.currentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Error: Not authenticated."));
        }
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || isTableAccount(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Error: Not allowed."));
        }
        if (!encoder.matches(request.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Current password is incorrect."));
        }
        user.setPassword(encoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return ResponseEntity.ok().body(new MessageResponse("Password changed successfully."));
    }

    private void sendResetCodeEmail(String email, String code) {
        String subject = "Réinitialisation de votre mot de passe SnapWaiter";
        String message = "Bonjour,\n\n"
                + "Nous avons reçu une demande de réinitialisation du mot de passe de votre compte SnapWaiter.\n\n"
                + "Votre code : " + code + "\n\n"
                + "Ce code est valable " + RESET_CODE_MINUTES + " minutes. Saisissez-le dans l'application avec votre nouveau mot de passe.\n\n"
                + "Si vous n'êtes pas à l'origine de cette demande, ignorez cet e-mail : votre mot de passe actuel reste valable.\n\n"
                + "Cordialement,\n"
                + "L'équipe SnapWaiter";
        EmailDetails details = new EmailDetails(email, message, subject);
        emailService.sendSimpleMail(details);
    }

    @GetMapping("/{id}/validation")
    public ResponseEntity<?> getValidation(@PathVariable Long id) {
        try {
            Space space = zoneService.findZoneById(id).getSpace();
            String validation = spaceService.getValidationById(space.getId_space());
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("validation", validation);
            responseBody.put("id", space.getId_space());
            return ResponseEntity.ok()
                .body(responseBody);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    /** Public : uniquement les noms de réseaux (SSID), jamais les mots de passe Wi-Fi. */
    @GetMapping("/space/{spaceId}")
    public List<Map<String, Object>> getWifisBySpaceId(@PathVariable Long spaceId) {
        return wifiService.getWifisBySpaceId(spaceId).stream().map(w -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", w.getId());
            m.put("ssid", w.getSsid());
            return m;
        }).collect(Collectors.toList());
    }

    @PostMapping("/demande")
    public ResponseEntity<Demande> createDemande(@RequestBody Demande demande) {
        Demande savedDemande = demandeService.save(demande);
        return ResponseEntity.ok(savedDemande);
    }
}
