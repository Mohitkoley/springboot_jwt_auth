package com.JWT.example.dummy.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter{
	
	
	private Logger logger = LoggerFactory.getLogger(JWTAuthenticationFilter.class);
	
	@Autowired
	private JWTHelper jwtHelper;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
				
		
		String requestHeader = request.getHeader("Authorization");
		// Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyb2NrZXkifQ.4QJ3zXf8J5d6Q5Ug6I8F9Q9pZv8kYy1e4l5h2zZ6V5w
		
		logger.info("Request Header: "+requestHeader);
		
		String username = null;
		String token = null;
		
		if (requestHeader != null && requestHeader.startsWith("Bearer")) {
			token = requestHeader.substring(7);
			try {
				username = JWTHelper.getUsernameFromToken(token);
			} catch (IllegalArgumentException e) {
				logger.error("Illegal argument while fetching username from token", e);
				e.printStackTrace();
			} catch (ExpiredJwtException e) {
                logger.error("Token has expired", e);
                e.printStackTrace();
			} catch (MalformedJwtException e) {
				logger.error("Token is malformed", e);
				e.printStackTrace();
			} catch (JwtException e) {
				logger.error("JWT exception", e);
				e.printStackTrace();
			} catch (Exception e) {
				logger.error("Exception", e);
				e.printStackTrace();
			}
			
		}else {
			logger.info("Invalid header info");
        }
		
		
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			
			//fetch user details from userName 
			
			UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
			Boolean validateToken = JWTHelper.validateToken(token, userDetails);
			
			if (validateToken) {
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authentication);

			}else {
				logger.info("Validation failed");
            }
			}
		filterChain.doFilter(request, response);
			
		}
		
		
	}
	
	
	
	


