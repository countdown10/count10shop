package xyz.tomorrowlearncamp.count10shop.domain.common.etc;

public interface JwtProperties {
	String HEADER_STRING = "Authorization";
	String REFRESH_HEADER_STRING = "Set-Cookie";
	String TOKEN_PREFIX = "Bearer ";
	int ACCESS_EXPIRATION_TIME = 1000 * 60 * 15;
	int REFRESH_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7;
	String APP_TITLE = "count10shop";
}