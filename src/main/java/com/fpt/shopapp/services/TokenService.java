package com.fpt.shopapp.services;

import com.fpt.shopapp.exceptions.ExpiredTokenException;
import com.fpt.shopapp.model.Token;
import com.fpt.shopapp.model.User;

public interface TokenService {
    Token addToken(User user, String token, boolean isMobileDevice);
    Token refreshToken(String refreshToken, User user) throws ExpiredTokenException;
}
