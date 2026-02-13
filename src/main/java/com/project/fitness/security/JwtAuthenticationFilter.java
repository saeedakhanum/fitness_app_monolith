package com.project.fitness.security;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.project.fitness.repository.BlackListedTokenRepository;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private final JwtUtils jwtUtils;
	private final BlackListedTokenRepository blackListedTokenRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		System.out.println("Jwt authentication filter called");
		// TODO Auto-generated method stub
		try {
			String jwtToken = jwtUtils.getJwtTokenFromHeader(request);
			if (jwtToken != null && jwtUtils.validateJwtToken(jwtToken)) {
				if (blackListedTokenRepository.existsByJwtToken(jwtToken)) {
				    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				    response.setContentType("application/json");
				    response.getWriter().write("""
				    {
				        "status": 401,
				        "message": "Token is logged out",
				        "requestUri": "%s"
				    }
				    """.formatted(request.getRequestURI()));
				    return;
				}

				String userId = jwtUtils.getUserIdFromJwtToken(jwtToken);
				Claims claims = jwtUtils.getAllClaims(jwtToken);
				List<String> roles = claims.get("roles", List.class);
				System.out.println("ROLES : " + roles);
				List<GrantedAuthority> authorities = roles.stream()
						.map(role -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_"+role)).toList();
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId,null,
						authorities);
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		filterChain.doFilter(request, response);
	}

}
