package io.github.abbassizied.sms.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.abbassizied.sms.dtos.LoginRequest;
import io.github.abbassizied.sms.services.TokenService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	
    @Autowired
    private AuthenticationManager authenticationManager;	

    private final TokenService tokenService;

    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/login") 
    public String login(@RequestBody LoginRequest userLogin) throws IllegalAccessException {
    	
    	System.out.println("************** Login ********");
    	
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(userLogin.username(), userLogin.password()));

         //SecurityContextHolder.getContext().setAuthentication(authentication);
    	
         
        return tokenService.generateToken(authentication);
        
        
    }

}