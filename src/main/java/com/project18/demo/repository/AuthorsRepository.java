package com.project18.demo.repository;

import com.project18.demo.model.Authors;
import com.project18.demo.model.BlogModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorsRepository extends JpaRepository<Authors, Long> {
    Optional<Authors> findByEmail(String email);

    Optional<Authors> findByFullName(String fullName);

    Optional<Authors> findByVerificationCode(String code);

    List<Authors> findAllByPosts(BlogModel blogModel);
}
