package com.project18.demo.service;

import com.project18.demo.model.Authors;
import com.project18.demo.model.dto.AuthorDetails;
import com.project18.demo.repository.AuthorsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.project18.demo.mapper.UserDetailsMapper.getAuthorDetails;

@Slf4j
@Service
public class UserDetailsProvider implements UserDetailsService {

    private final AuthorsRepository authorsRepository;

    @Autowired
    public UserDetailsProvider(AuthorsRepository authorsRepository) {
        this.authorsRepository = authorsRepository;
    }

    @Override
    public AuthorDetails loadUserByUsername(String email) {
        Optional<Authors> user = authorsRepository.findByEmail(email);

        return getAuthorDetails(user.get());
    }
}
