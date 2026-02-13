package com.project.fitness.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.fitness.entity.PasswordResetToken;
import com.project.fitness.entity.User;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

	@Query(value="select * from password_reset_token where user_id=:userId and otp_used=false order by otp_expiry_time desc limit 1",nativeQuery=true)
	Optional<PasswordResetToken> getLatestOtpOfTheUser(@Param("userId") Long userId);

	@Query("select p from PasswordResetToken p where otp=:otp")
	Optional<PasswordResetToken> findByOtp(@Param("otp") String otp);
}
