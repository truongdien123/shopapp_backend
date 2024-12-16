package com.fpt.shopapp.services;

import com.fpt.shopapp.dto.UserDTO;
import com.fpt.shopapp.model.User;

public interface UserService {
    User createUser(UserDTO userDTO);
    String login(String phoneNumber, String password);

}
