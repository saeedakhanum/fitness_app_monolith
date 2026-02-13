package com.project.fitness.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.fitness.entity.BlackListedToken;


public interface BlackListedTokenRepository extends JpaRepository<BlackListedToken,Long> {
	
	boolean existsByJwtToken(String jwtToken);

}
