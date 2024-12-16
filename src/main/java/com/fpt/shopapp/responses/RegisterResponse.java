package com.fpt.shopapp.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fpt.shopapp.model.User;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterResponse {
    @JsonProperty("message")
    private String message;

    @JsonProperty("user")
    private User user;
}
