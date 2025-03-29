package com.fpt.shopapp.services.imp;

import com.fpt.shopapp.components.JwtTokenUtils;
import com.fpt.shopapp.components.LocalizationUtils;
import com.fpt.shopapp.dto.UpdateUserDTO;
import com.fpt.shopapp.dto.UserDTO;
import com.fpt.shopapp.exceptions.DataNotFoundException;
import com.fpt.shopapp.exceptions.PermissionDenyException;
import com.fpt.shopapp.model.Role;
import com.fpt.shopapp.model.User;
import com.fpt.shopapp.repositories.RoleRepository;
import com.fpt.shopapp.repositories.UserRepository;
import com.fpt.shopapp.services.UserService;
import com.fpt.shopapp.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;
    private final LocalizationUtils localizationUtils;

    @Override
    @Transactional
    public User createUser(UserDTO userDTO) {
        //register user
        String phoneNumber = userDTO.getPhoneNumber();
        if (userRepository.existsByPhoneNumber(phoneNumber)){
            throw new DataIntegrityViolationException("Phone number already exist");
        }
        Role role = roleRepository.findById(userDTO.getRoleId()).orElseThrow(() -> new DataNotFoundException("Role not found"));
        if (role.getName().toUpperCase().equals(Role.ADMIN)){
            throw new PermissionDenyException("You can't register an admin account");
        }
        User user = User.builder()
                .fullName(userDTO.getFullName())
                .phoneNumber(userDTO.getPhoneNumber())
                .password(userDTO.getPassword())
                .address(userDTO.getAddress())
                .dateOfBirth(userDTO.getDateOfBirth())
                .facebookAccountId(userDTO.getFacebookAccountId())
                .googleAccountId(userDTO.getGoogleAccountId())
                .build();

        user.setRole(role);
        // Kiểm tra nếu có accountId, không yêu cầu password
        if (userDTO.getFacebookAccountId()==0 && userDTO.getGoogleAccountId()==0){
            String password = user.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            user.setPassword(encodedPassword);
        }
        return userRepository.save(user);
    }

    @Override
    public String login(String phoneNumber, String password, Long roleId) {
        Optional<User> optionalUser = userRepository.findByPhoneNumber(phoneNumber);
        if (optionalUser.isEmpty()){
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.WRONG_PHONE_PASSWORD));
        }
        if (!optionalUser.get().isActive()) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.USER_IS_LOCKED));
        }
        User user = optionalUser.get();
        if (user.getFacebookAccountId()==0 && user.getGoogleAccountId()==0){
            if (!passwordEncoder.matches(password, user.getPassword())){
                throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.WRONG_PHONE_PASSWORD));
            }
        }
        Optional<Role> optionalRole = roleRepository.findById(roleId);
        if (optionalRole.isEmpty() || !roleId.equals(user.getRole().getId())) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.ROLE_DOES_NOT_EXISTS));
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(phoneNumber, password, user.getAuthorities());
        authenticationManager.authenticate(authenticationToken);
        return jwtTokenUtils.generateToken(optionalUser.get());
    }

    @Override
    public User getUserDetailFromToken(String token) throws Exception {
        if (jwtTokenUtils.isTokenExpired(token)) {
            throw new Exception("Token is expired");
        }
        String phoneNumber = jwtTokenUtils.extractPhoneNumber(token);
        Optional<User> userOptional = userRepository.findByPhoneNumber(phoneNumber);
        if (userOptional.isEmpty()) {
            throw new DataNotFoundException("User not found");
        }
        return userOptional.get();
    }

    @Override
    public User updateUser(Long userId, UpdateUserDTO updateUserDTO) throws Exception {
        User existingUser = userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User not found"));
        String newPhoneNumber = updateUserDTO.getPhoneNumber();
        if (!existingUser.getPhoneNumber().equals(newPhoneNumber) && userRepository.existsByPhoneNumber(newPhoneNumber)) {
            throw new DataIntegrityViolationException("Phone number already exist");
        }
        if (updateUserDTO.getFullName() != null) {
            existingUser.setFullName(updateUserDTO.getFullName());
        }
        if (newPhoneNumber != null) {
            existingUser.setPhoneNumber(newPhoneNumber);
        }
        if (updateUserDTO.getAddress() != null) {
            existingUser.setAddress(updateUserDTO.getAddress());
        }
        if (updateUserDTO.getDateOfBirth() != null) {
            existingUser.setDateOfBirth(updateUserDTO.getDateOfBirth());
        }
        if (updateUserDTO.getFacebookAccountId() > 0) {
            existingUser.setFacebookAccountId(updateUserDTO.getFacebookAccountId());
        }
        if (updateUserDTO.getGoogleAccountId() > 0) {
            existingUser.setGoogleAccountId(updateUserDTO.getGoogleAccountId());
        }
        if (updateUserDTO.getPassword() != null && !updateUserDTO.getPassword().isEmpty()) {
            if (!updateUserDTO.getPassword().equals(updateUserDTO.getRetypePassword())) {
                throw new DataNotFoundException("Password and retype password not the same");
            }
            String newPassword = updateUserDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(newPassword);
            existingUser.setPassword(encodedPassword);
        }
        return userRepository.save(existingUser);
    }
}
