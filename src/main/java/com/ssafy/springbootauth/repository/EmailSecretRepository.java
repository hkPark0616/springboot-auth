package com.ssafy.springbootauth.repository;

import com.ssafy.springbootauth.entitiy.EmailSecretEntity;
import org.springframework.data.repository.CrudRepository;

public interface EmailSecretRepository extends CrudRepository<EmailSecretEntity, String> {
}
