package org.example.apiblitz.util;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.model.UserSignIn;
import org.example.apiblitz.model.UserSignUp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtUtil {

	public static final long EXPIRE_TIME = 3600;
	private SecretKey secretKey;

	@Value("${jwt.secret}")
	private String secret;

	@PostConstruct
	private void init() {
		secretKey = Keys.hmacShaKeyFor(secret.getBytes());
	}

	public String userSignUpGenerateToken(UserSignUp user) {

		long nowMills = System.currentTimeMillis();
		Date expireDate = new Date(nowMills + EXPIRE_TIME * 1000);

		Map<String, Object> claims = userSignUpGetClaims(user);

		JwtBuilder jwtBuilder = Jwts.builder()
				.setClaims(claims)
				.setSubject(user.getName())
				.setIssuedAt(new Date(nowMills))
				.setExpiration(expireDate)
				.signWith(SignatureAlgorithm.HS256, secretKey);

		return jwtBuilder.compact();
	}

	public String userSignInGenerateToken(UserSignIn user) {

		long nowMills = System.currentTimeMillis();
		Date expireDate = new Date(nowMills + EXPIRE_TIME * 1000);

		Map<String, Object> claims = userSignInGetClaims(user);

		JwtBuilder jwtBuilder = Jwts.builder()
				.setClaims(claims)
				.setSubject(user.getName())
				.setIssuedAt(new Date(nowMills))
				.setExpiration(expireDate)
				.signWith(SignatureAlgorithm.HS256, secretKey);

		return jwtBuilder.compact();
	}

	public Map<String, Object> userSignUpGetClaims(UserSignUp user) {

		Map<String, Object> claims = new HashMap<>();
		claims.put("name", user.getName());
		claims.put("email", user.getEmail());
		return claims;
	}

	public Map<String, Object> userSignInGetClaims(UserSignIn user) {

		Map<String, Object> claims = new HashMap<>();
		claims.put("name", user.getName());
		claims.put("email", user.getEmail());
		return claims;
	}
}
