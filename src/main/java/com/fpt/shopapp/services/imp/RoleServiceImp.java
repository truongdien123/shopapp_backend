package com.fpt.shopapp.services.imp;

import com.fpt.shopapp.model.Role;
import com.fpt.shopapp.repositories.RoleRepository;
import com.fpt.shopapp.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImp implements RoleService {
    private final RoleRepository roleRepository;
    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}
