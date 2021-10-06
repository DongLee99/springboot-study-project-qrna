package com.minionz.backend.user.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserWithdrawResponse {

    private String email;
    private int statsCode;

    public UserWithdrawResponse(String email) {
        this.email = email;
    }
}
