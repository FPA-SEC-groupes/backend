package com.HelloWay.HelloWay.controllers;

import com.HelloWay.HelloWay.DistanceLogic.DistanceCalculator;
import com.HelloWay.HelloWay.Security.Jwt.CustomSessionRegistry;
import com.HelloWay.HelloWay.Security.Jwt.JwtUtils;
import com.HelloWay.HelloWay.Security.Jwt.SessionUtils;
import com.HelloWay.HelloWay.entities.*;
import com.HelloWay.HelloWay.payload.Value;
import com.HelloWay.HelloWay.payload.request.LoginRequest;
import com.HelloWay.HelloWay.payload.request.ResetPasswordRequest;
import com.HelloWay.HelloWay.payload.request.ResetPasswordRequestIntern;
import com.HelloWay.HelloWay.payload.request.SignupRequest;
import com.HelloWay.HelloWay.payload.response.InformationAfterScan;
import com.HelloWay.HelloWay.payload.response.MessageResponse;
import com.HelloWay.HelloWay.payload.response.UserInfoResponse;
import com.HelloWay.HelloWay.repos.RoleRepository;
import com.HelloWay.HelloWay.repos.UserRepository;
import com.HelloWay.HelloWay.services.EmailService;
import com.HelloWay.HelloWay.services.UserDetailsImpl;
import com.HelloWay.HelloWay.services.ZoneService;
import org.apache.coyote.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.HelloWay.HelloWay.entities.ERole.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

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

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Check if the user is activated
        if (!userDetails.isActivated()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account not activated");
        }

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(new UserInfoResponse(userDetails.getId(),
                        userDetails.getName(),
                        userDetails.getLastname(),
                        userDetails.getBirthday(),
                        userDetails.getPhone(),
                        userDetails.getUsername(),
                        userDetails.getEmail(),
                        roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account ya 3loulou
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getName(),
                signUpRequest.getLastname(),
                signUpRequest.getBirthday(),
                signUpRequest.getPhone(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        user.setActivated(true); // Set the 'activated' field to true


        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "provider":
                        Role providerRole = roleRepository.findByName(ROLE_PROVIDER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(providerRole);
                        break;
                    case "waiter":
                        Role assistantRole = roleRepository.findByName(ROLE_WAITER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(assistantRole);
                    case "guest":
                        Role guestRole = roleRepository.findByName(ROLE_GUEST)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(guestRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

   /* @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        String sessionId =  RequestContextHolder.currentRequestAttributes().getSessionId();
        customSessionRegistry.removeSessionInformation(sessionId);
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("You've been signed out!"));
    }

    */

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request, HttpServletResponse response) {
        String sessionId = request.getSession(false).getId();
        if (sessionId != null) {
            customSessionRegistry.removeUserFromTableWithRole(sessionId);
            request.getSession().invalidate();
        }
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok().body(new MessageResponse("You've been signed out!"));
    }



    //EzzzabWakahw
   /* @PostMapping("/login-qr")
    public ResponseEntity<?> authenticateWithQrCode(@RequestBody QrCodeAuthenticationRequest qrCodeAuthenticationRequest,
                                                    HttpServletResponse response) {
        String tableIdentifier = qrCodeAuthenticationRequest.getTableIdentifier();

        try {
            // Generate a temporary authentication token
            String token = jwtUtils.generateTokenFromTableIdentifier(tableIdentifier);

            // Create a temporary user object
            User temporaryUser = new User();
            temporaryUser.setId(-1L); // Replace with appropriate user ID
            temporaryUser.setName("Temporary"); // Replace with appropriate name
            temporaryUser.setLastname("User"); // Replace with appropriate lastname
            temporaryUser.setBirthday(null); // Replace with appropriate birthday
            temporaryUser.setPhone(null); // Replace with appropriate phone
            temporaryUser.setUsername("temp"); // Replace with appropriate username
            temporaryUser.setEmail(null); // Replace with appropriate email
            temporaryUser.setPassword(null); // Replace with appropriate password
            temporaryUser.setRoles(null); // Replace with appropriate authorities

            // Create a UserDetails object using the temporary user information
            UserDetails userDetails = UserDetailsImpl.build(temporaryUser);

            // Set the authentication token in the response cookies
            ResponseCookie jwtCookie = jwtUtils.generateJwtCookie((UserDetailsImpl) userDetails);
            response.addHeader("Set-Cookie", jwtCookie.toString());

            // Create the response object
            QrCodeAuthenticationResponse authenticationResponse = new QrCodeAuthenticationResponse(token);

            return ResponseEntity.ok(authenticationResponse);
        } catch (Exception e) {
            // Handle authentication failure
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication failed: " + e.getMessage());
        }
    } */

    @PostMapping("/signin/qr_Code/{qr_Code}")
    public ResponseEntity<?> authenticateUser(@PathVariable String qr_Code) {
        String[] splitArray = qr_Code.split("-"); // Splitting using the hyphen character "-"

        String idTable = splitArray[0];
        String idZone = splitArray[splitArray.length - 1];

        String userName = "Board"+ idTable;
        String password = "Pass"+ idTable +"*"+idZone;


        LoginRequest loginRequest = new LoginRequest(userName,password);
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String sessionId =  RequestContextHolder.currentRequestAttributes().getSessionId();

        customSessionRegistry.registerNewSession(sessionId,userDetails);


        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

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


    @PostMapping("/signin/qr_Code/{qr_Code}/userLatitude/{userLatitude}/userLongitude/{userLongitude}/{accuracy}")
    public ResponseEntity<?> authenticateUser(@PathVariable String qr_Code, @PathVariable String userLatitude, @PathVariable String userLongitude, HttpServletRequest request, @PathVariable String accuracy) {
        String[] splitArray = qr_Code.split("-"); // Splitting using the hyphen character "-"

        String idTable = splitArray[0];
        String idZone = splitArray[splitArray.length - 1];

        Space space = zoneService.findZoneById(Long.parseLong(idZone)).getSpace();

        if (DistanceCalculator.isTheUserInTheSpaCe(userLatitude, userLongitude, Double.parseDouble(accuracy) , space))
        {
            String userName = "Board"+ idTable;
            String password = "Pass"+ idTable +"*"+idZone;


            LoginRequest loginRequest = new LoginRequest(userName,password);
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String sessionId =  RequestContextHolder.currentRequestAttributes().getSessionId();

            //  customSessionRegistry.registerNewSession(sessionId,userDetails);
            //  customSessionRegistry.setNewUserOnTable(sessionId,idTable);


            ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            Value value = new Value(idTable, roles.get(0));
            customSessionRegistry.setNewUserOnTableWithRole(sessionId,value);
            HttpSession session =  request.getSession();
            sessionUtils.addSession(request.getSession());


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
        else {
            return ResponseEntity.ok().body("the user not in the space so we are sorry you cant be connected");
        }
    }

    @PostMapping("/qr_Code_for_app_user/{qr_Code}/userLatitude/{userLatitude}/userLongitude/{userLongitude}/{accuracy}")
    public ResponseEntity<?> setUserInTable(@PathVariable String qr_Code, @PathVariable String userLatitude, @PathVariable String userLongitude, @PathVariable String accuracy) {
        String[] splitArray = qr_Code.split("-"); // Splitting using the hyphen character "-"

        String idTable = splitArray[0];
        String idZone = splitArray[splitArray.length - 1];

        Space space = zoneService.findZoneById(Long.parseLong(idZone)).getSpace();

        if (DistanceCalculator.isTheUserInTheSpaCe(userLatitude, userLongitude, Double.parseDouble(accuracy), space))
        {

            String sessionId =  RequestContextHolder.currentRequestAttributes().getSessionId();
            Value  value     = new Value(idTable, ROLE_USER.toString());
            //  customSessionRegistry.setNewUserOnTable(sessionId,idTable);
            customSessionRegistry.setNewUserOnTableWithRole(sessionId,value);

            InformationAfterScan informationAfterScan = new InformationAfterScan(space.getId_space().toString(),idTable, sessionId) ;
            return ResponseEntity.ok()
                    .body( informationAfterScan);
        }
        else {
            return ResponseEntity.ok().body("the user not in the space so we are sorry you cant be sated in this table");
        }
    }

    @PostMapping("/reset-password/email")
    public MessageResponse resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        String email = resetPasswordRequest.getEmail();

        User user = userRepository.findByEmail(email).orElse(null);

        if (!userRepository.existsByEmail(resetPasswordRequest.getEmail())) {
             return new MessageResponse("Error: User not found with the provided email.");
        }

        // Generate a new password
        String newPassword = generateRandomPassword();

        // Update the user's password
        String encodedPassword = encoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);

        // Send the new password to the user's email
        sendNewPasswordEmail(user.getEmail(), newPassword);

        return new MessageResponse("Password reset successfully. Please check your email for the new password.");
    }

    private String generateRandomPassword() {
        // Generate a random password using UUID
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private void sendNewPasswordEmail(String email, String newPassword) {
        String subject = "Password Reset";
        String message = "Hello from HelloWay,\n\n"
                + "You have requested to reset your password. Please use the following password to connect your account :\n"
                + newPassword + "\n\n"
                + "If you didn't request this password reset, you can ignore this email.\n\n"
                + "Best regards,\n"
                + "HelloWay Team";
        EmailDetails details = new EmailDetails(email, message, subject);
        emailService.sendSimpleMail(details);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequestIntern resetPasswordRequest) {
        String email = resetPasswordRequest.getEmail();
        String newPassword = resetPasswordRequest.getNewPassword();

        // Check if the user exists with the provided email
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: User not found with the provided email."));
        }

        // Update the user's password
        String encodedPassword = encoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);

        return ResponseEntity.ok().body(new MessageResponse("Password reset successfully."));
    }


}

