package xyz.tomorrowlearncamp.count10shop.domain.common.util;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import xyz.tomorrowlearncamp.count10shop.domain.common.entity.JwtToken;
import xyz.tomorrowlearncamp.count10shop.domain.common.etc.JwtProperties;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {
	private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

	// 토큰 생성
	public JwtToken generateToken(Long id, String email) {
		long now = System.currentTimeMillis();

		String accessToken = JWT.create()
			.withSubject(JwtProperties.APP_TITLE)
			.withIssuedAt(new Date(now))
			.withExpiresAt(new Date(now + JwtProperties.ACCESS_EXPIRATION_TIME))
			.withClaim("id", id)
			.withClaim("email", email)
			.sign(Algorithm.HMAC256(key.getEncoded()));

		return JwtToken.builder()
			.grantType(JwtProperties.TOKEN_PREFIX)
			.accessToken(accessToken)
			.build();
	}

	//로그아웃용 토큰 만료시키기
	public JwtToken generateExpiredToken(Long id, String email) {
		long now = System.currentTimeMillis();

		String accessToken = JWT.create()
			.withSubject(JwtProperties.APP_TITLE)
			.withIssuedAt(new Date(now))
			.withExpiresAt(new Date(now))
			.withClaim("id", id)
			.withClaim("email", email)
			.sign(Algorithm.HMAC256(key.getEncoded()));

		return JwtToken.builder()
			.grantType(JwtProperties.TOKEN_PREFIX)
			.accessToken(accessToken)
			.build();
	}

	public String extractEmail(String token) {
		String reToken = token.replace(JwtProperties.TOKEN_PREFIX, "");
		return JWT.require(Algorithm.HMAC256(key.getEncoded())).build().verify(reToken).getClaim("email").asString();
	}

	public Long extractUserId(String token) {
		String reToken = token.replace(JwtProperties.TOKEN_PREFIX, "");
		return Long.parseLong(JWT.require(Algorithm.HMAC256(key.getEncoded())).build().verify(reToken).getClaim("id").toString());
	}

	public String substringToken(String tokenValue) {
		if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(JwtProperties.TOKEN_PREFIX)) {
			return tokenValue.substring(7);
		}
		log.error("Not Found Token");
		throw new NullPointerException("Not Found Token");
	}

	public Claims extractClaims(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token)
			.getBody();
	}
}
