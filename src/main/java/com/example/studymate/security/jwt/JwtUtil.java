package com.example.studymate.security.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.example.studymate.security.CustomUserDetailsService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Getter
@Slf4j
public class JwtUtil {

	private final CustomUserDetailsService customUserDetailsService;
	
	@Value("${myapp.jwt.secret}")
	private String secretKeyStr; 
	
	private SecretKey secretKey; 
	
	private final long tokenValidDuration = 1000L * 60 * 60 * 24; // 24 시간	
	
	@PostConstruct
	protected void init() {
		secretKey = new SecretKeySpec(
				secretKeyStr.getBytes(StandardCharsets.UTF_8),
				Jwts.SIG.HS256.key().build().getAlgorithm()
		);
	}
	
	public String createToken(String username, List<String> roles) {
		Date now = new Date();
		
		return Jwts.builder()
				.subject(username)  
				.claim("roles", roles) 
				.issuedAt(now)  
				.expiration(new Date(now.getTime() + tokenValidDuration))
				.signWith(secretKey, Jwts.SIG.HS256)
				.compact();
	}
	
	public String getUsernameFromToken(String token) {
		return Jwts.parser()
				.verifyWith(secretKey) 
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.getSubject();
	}
	
	public String getTokenFromHeader(HttpServletRequest request) {
		return request.getHeader("X-AUTH-TOKEN");
	}
	
	public Claims validateToken(String token) {
		try {
			Claims claims = Jwts.parser()
					.verifyWith(secretKey)
					.build()
					.parseSignedClaims(token)
					.getPayload();
			
			if( claims.getExpiration() != null && claims.getExpiration().before(new Date())) {
				return null;
			}
			
			return claims;
			
		}catch(Exception e) {
			return null;
		}
	}
	
	public UsernamePasswordAuthenticationToken getAuthentication(String token) {
		UserDetails userDetails = customUserDetailsService.loadUserByUsername(getUsernameFromToken(token));
		return new UsernamePasswordAuthenticationToken(
				userDetails, 
				"", 
				userDetails.getAuthorities());
	}
}
