package com.ssafy.springbootauth.repository;

import com.ssafy.springbootauth.entity.RefreshTokenEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface RefreshTokenRepository extends CrudRepository<RefreshTokenEntity, String> {

}
