package com.bsp.procedure_gateway.sevice.impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;

@Component
public class SecurityUtil {
	
	 
	public SecurityUtil() {
	}

	public static String getLoggedInUser() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null) {
			return "SYSTEM";
		}
		
		if (!(authentication.getPrincipal() instanceof Claims claims)) {
	        return "SYSTEM";
	    }
		
		 Object usernameObj = claims.get("username");

		 return usernameObj != null ? usernameObj.toString() : "SYSTEM";
	}
}
