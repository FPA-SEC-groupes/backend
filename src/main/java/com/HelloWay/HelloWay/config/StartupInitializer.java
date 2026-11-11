package com.HelloWay.HelloWay.config;

import com.HelloWay.HelloWay.entities.ERole;
import com.HelloWay.HelloWay.entities.Role;
import com.HelloWay.HelloWay.entities.User;
import com.HelloWay.HelloWay.repos.RoleRepository;
import com.HelloWay.HelloWay.repos.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Au démarrage : crée les rôles manquants et, si aucun admin n'existe,
 * un premier compte admin à partir de ADMIN_USERNAME / ADMIN_EMAIL / ADMIN_PASSWORD.
 */
@Component
public class StartupInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(StartupInitializer.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Value("${app.admin.username:}")
    private String adminUsername;
    @Value("${app.admin.email:}")
    private String adminEmail;
    @Value("${app.admin.password:}")
    private String adminPassword;

    public StartupInitializer(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder encoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        for (ERole name : ERole.values()) {
            if (roleRepository.findByName(name).isEmpty()) {
                Role role = new Role();
                role.setName(name);
                roleRepository.save(role);
            }
        }

        boolean adminExists = userRepository.findAll().stream()
                .anyMatch(u -> u.getRoles().stream().anyMatch(r -> r.getName() == ERole.ROLE_ADMIN));
        if (adminExists) {
            return;
        }
        if (adminUsername.isBlank() || adminEmail.isBlank() || adminPassword.length() < 8) {
            logger.warn("Aucun admin : définir ADMIN_USERNAME, ADMIN_EMAIL et ADMIN_PASSWORD (8 caractères min.) pour en créer un.");
            return;
        }
        User admin = new User(adminUsername, "Admin", "HelloWay", null, null, 0, adminEmail, encoder.encode(adminPassword));
        admin.setActivated(true);
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName(ERole.ROLE_ADMIN).orElseThrow());
        admin.setRoles(roles);
        userRepository.save(admin);
        logger.info("Compte admin initial créé : {}", adminUsername);
    }
}
