package com.project18.demo.init;

import com.project18.demo.model.Authors;
import com.project18.demo.model.Role;
import com.project18.demo.repository.AuthorsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Profile("prod")
public class Init {

    @Autowired
    AuthorsRepository authorsRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostConstruct
    public void addUser(){
        authorsRepository.save(Authors.builder()
                .email("admin@email.pl")
                .password(passwordEncoder.encode("password"))
                .fullName("admin")
                .enabled(true)
                .resetPasswordToken("token")
                .verificationCode("code")
                .role(Role.ADMIN)
                .build());
    }
}
