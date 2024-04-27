package com.HelloWay.HelloWay.services;

import com.HelloWay.HelloWay.exception.UserNotFoundException;
import com.HelloWay.HelloWay.entities.Role;
import com.HelloWay.HelloWay.repos.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class RoleService {
    private static RoleRepository roleRepository;


    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RoleService(RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;

        this.passwordEncoder = passwordEncoder;

    }

    public RoleService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public Role addRole(Role role) {

        return roleRepository.save(role);
    }

    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }

    public Role updateRole(Role role) {
        return roleRepository.save(role);
    }

    public Role findRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User by id " + id + " was not found"));
    }

    public void deleteRole(Long id){
        roleRepository.deleteById(id);
    }

}
