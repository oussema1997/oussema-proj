package com.sfm.obd.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthOtp {

    private int otp;
    private String email;

    public AuthOtp() {
    }

    public AuthOtp(int otp, String email) {
        this.otp = otp;
        this.email = email;
    }
}
