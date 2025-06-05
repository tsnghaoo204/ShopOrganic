package com.practice1;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UtilMain {
    private static PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    public static void main(String[] args) {
        System.out.println(passwordEncoder.encode("admin"));
    }
}
