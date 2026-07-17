package com.bsp.procedure_gateway.controller;


import java.io.IOException;
import java.time.Duration;
 

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
import org.springframework.http.HttpHeaders;
 
import org.springframework.http.ResponseCookie;
 
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/sso")
@CrossOrigin(origins = "*")
public class AuthController {
	
	private static final Logger log =
	        LoggerFactory.getLogger(AuthController.class);
	
 

    @GetMapping("/callback")
    public void callback(
            @RequestParam("token") String token,
            HttpServletResponse response)
            throws IOException {

        log.info("Received JWT from IAM");

        ResponseCookie jwtCookie =
                ResponseCookie.from(
                        "BSP_JWT",
                        token)
                        .httpOnly(true)
                        .secure(false) // true in production HTTPS
                        .sameSite("Lax")
                        .path("/")
                        .maxAge(Duration.ofMinutes(30))
                        .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                jwtCookie.toString());

        log.info("JWT cookie stored successfully");

        response.sendRedirect(
                 "/bspAPIgateway/procedure/search");
    }
    
 
}
