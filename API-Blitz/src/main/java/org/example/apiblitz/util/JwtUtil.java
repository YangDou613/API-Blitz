package org.example.apiblitz.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.error.TokenParsingException;
import org.example.apiblitz.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtUtil {

	public static final long EXPIRE_TIME = 86400000;
	private SecretKey secretKey;

	@Value("${jwt.secret}")
	private String secret;

	@PostConstruct
	private void init() {
		secretKey = Keys.hmacShaKeyFor(secret.getBytes());
	}

	public String generateToken(User user) {

		long nowMills = System.currentTimeMillis();
		Date expireDate = new Date(nowMills + EXPIRE_TIME * 30);

		Map<String, Object> claims = getClaims(user);

		JwtBuilder jwtBuilder = Jwts.builder()
				.setClaims(claims)
				.setSubject(user.getName())
				.setIssuedAt(new Date(nowMills))
				.setExpiration(expireDate)
				.signWith(SignatureAlgorithm.HS256, secretKey);

		return jwtBuilder.compact();
	}

	public Map<String, Object> getClaims(User user) {

		Map<String, Object> claims = new HashMap<>();

		claims.put("userId", user.getId());
		claims.put("name", user.getName());
		claims.put("email", user.getEmail());

		return claims;
	}

	public String extractAccessToken(String authorization) {
		String[] parts = authorization.split(" ");
		if (parts.length == 2 && parts[0].equalsIgnoreCase("Bearer")) {
			return parts[1];
		} else {
			return null;
		}
	}

	public Claims parseToken(String accessToken) throws TokenParsingException {
		try {
			return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken).getBody();
		} catch (Exception e) {
			log.error("Parsing token error: " + e.getMessage());
			throw new TokenParsingException(e);
		}
	}
}
