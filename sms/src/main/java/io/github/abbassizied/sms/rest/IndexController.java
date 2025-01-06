package io.github.abbassizied.sms.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class IndexController {

	@GetMapping("/")
	String index(Principal principal) {
		return "Hello, " + principal.getName() + "\n A REST API with Spring Boot for a stock management application.";
	}

}
