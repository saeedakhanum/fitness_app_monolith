package com.project.fitness.security;

import java.security.Key;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtils {

	private String jwtSecret = "364876487dgdhhdguhdegb6et4784dhbfdhjfjhf784y74fg";
	private int jwtExpirationMs = 172800000;

	public String generateJwtToken(Long userId, List<String> roles) {
		return Jwts.builder().subject(String.valueOf(userId)).claim("roles", roles).issuedAt(new Date())
				.expiration(new Date(new Date().getTime() + jwtExpirationMs))
				.signWith(key()).compact();
	}

	public String getJwtTokenFromHeader(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			String jwtToken = bearerToken.substring(7);
			return jwtToken;
		} else {
			return null;
		}
	}

	public boolean validateJwtToken(String jwtToken) {
		try {
			Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(jwtToken);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public Key key() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
	}

	public String getUserIdFromJwtToken(String jwtToken) {
		return Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(jwtToken).getPayload()
				.getSubject();
	}

	public Claims getAllClaims(String jwtToken) {
		return Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(jwtToken).getPayload();
	}
}
