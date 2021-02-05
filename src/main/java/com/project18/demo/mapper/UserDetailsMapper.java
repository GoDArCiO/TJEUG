package com.project18.demo.mapper;

import com.project18.demo.model.Authors;
import com.project18.demo.model.dto.AuthorDetails;

public class UserDetailsMapper {

    private static final String PREFIX_ROLE = "ROLE_";

    public static AuthorDetails getAuthorDetails(Authors author) {
        return AuthorDetails.builder()
                .id(author.getId())
                .email(author.getEmail())
                .role(author.getRole().toString())
                .isEnabled(true)
                .password(author.getPassword())
                .build();
    }

}
