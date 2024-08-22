package com.JWT.example.dummy.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JWTHelper {
	
	public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60; // 5 hours
	
	public static final String  key =   "sdfjksadfsadfasdfasdfasdfasdfasdfasdfasdfasdf";
	
	//retrieve username from jwt token
	public static String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}
	
	
	//retrieve expiration date from jwt token
	public static Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}
	
	
	//retrieve expiration date from jwt token
	public static boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}
	
	public static <T> T getClaimFromToken(String token, java.util.function.Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}
	
	//for retrieveing any information from token we will need the secret key
	private static Claims getAllClaimsFromToken(String token) {
		
		SecretKey SECRET = Keys.hmacShaKeyFor(Decoders.BASE64.decode(key));
		return Jwts.parser().verifyWith(SECRET).build().parseSignedClaims(token).getPayload();
	}
	
	
	
	//generate token for user
	public static String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		return doGenerateToken(claims, userDetails);
		
	}
	
	//while creating the token -
	//1. Define claims of the token, like Issuer, Expiration, Subject, and the ID
	//2. Sign the JWT using the HS512 algorithm and secret key.
	//3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
	//compaction of the JWT to a URL-safe string
	private static String doGenerateToken(Map<String, Object> claims, UserDetails userDetails) {
        
        return Jwts.builder().claims(claims).subject(userDetails.getUsername()).issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(key))).compact();
        
	}
	
	
	//validate token
	public static Boolean validateToken(String token, UserDetails userDetails) {
		final String username = getUsernameFromToken(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
	
	
	
	

}
