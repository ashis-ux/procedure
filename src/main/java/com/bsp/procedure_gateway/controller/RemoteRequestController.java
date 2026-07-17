package com.bsp.procedure_gateway.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
 
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/api")
@PreAuthorize("hasAuthority('APP_PROCEDUREAPIGATEWAY')")
public class RemoteRequestController {

    @Value("${app.iam.dashboard-url}")
    private String iamDashboardServiceUrl;
    
    @Value("${app.iam.logout-url}")
    private String iamLogoutUrl;
 


    @GetMapping("/forward/dashboard")
    public void forwardRequestToDashboard(
            @CookieValue("BSP_JWT") String token ,HttpServletResponse response) throws IOException {

        log.info("Forwarding request to remote service");

       
        String url = UriComponentsBuilder
                .fromHttpUrl(iamDashboardServiceUrl)
                .queryParam("token", token)
                .toUriString();

        try {
			response.sendRedirect(url);
			 log.info("dashboard hit successfully");
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}

        
    }
    
    @GetMapping("/forward/logout")
    public void forwardRequestToLogOut(
            @CookieValue("BSP_JWT") String token,
            HttpServletResponse response) {

    	 log.info("Forwarding request to remote service");
    	 
    	  // Delete JWT cookie
         ResponseCookie deleteCookie = ResponseCookie.from("BSP_JWT", "")
                 .httpOnly(true)
                 .secure(false) // true in production
                 .sameSite("Lax")
                 .path("/")
                 .maxAge(0)
                 .build();

         response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());


         String url = UriComponentsBuilder
                 .fromHttpUrl(iamLogoutUrl)
                 .queryParam("token", token)
                 .toUriString();

         try {
 			response.sendRedirect(url);
 		} catch (java.io.IOException e) {
 			e.printStackTrace();
 		}
    }
}
