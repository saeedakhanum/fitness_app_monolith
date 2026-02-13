package com.project.fitness.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.fitness.entity.User;
import com.project.fitness.entity.UserRole;

@Repository
public interface UserRepository extends JpaRepository<User,Long>{

	@Query("select u from User u where u.email=:email")
	Optional<User> findByEmail(@Param("email") String email);
	
	public boolean existsByEmail(String email);
	
	@Query("select count(distinct u.id) from User u join u.roles r where r=:role")
	Long countUsersByRole(@Param("role") UserRole role);
	
	@Query("select distinct u from User u join u.roles r where r=:role")
	List<User> findAllUsersByRole(@Param("role") UserRole role);
}
