package com.JWT.example.dummy.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.JWT.example.dummy.model.JwtRequest;
import com.JWT.example.dummy.model.JwtResponse;
import com.JWT.example.dummy.security.JWTHelper;

@RestController
@RequestMapping("/auth")
public class AuthController {
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JWTHelper helper;
	
	
	private Logger logger = LoggerFactory.getLogger(AuthController.class);
	
	
	@PostMapping("/login")
	
	public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request) {
		
		this.doAuthenticate(request.getEmail(), request.getPassword());
		UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
		
		String token = this.helper.generateToken(userDetails);
		
		
		JwtResponse response = JwtResponse.builder().jwtToken(token).username(userDetails.getUsername()).build();
		return ResponseEntity.ok(response);
		
	}

	private void doAuthenticate(String email, String password) {
		
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);
		
		try {
			authenticationManager.authenticate(token);
			
		} catch (BadCredentialsException e) {
			throw new RuntimeException("Invalid username or password");
		}
		
	}
	

}
