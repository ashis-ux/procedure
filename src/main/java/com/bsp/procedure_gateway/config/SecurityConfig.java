 package com.bsp.procedure_gateway.config;

 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.springframework.context.annotation.Bean;
 import org.springframework.context.annotation.Configuration;
 import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
 import org.springframework.security.config.annotation.web.builders.HttpSecurity;
 import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
 import org.springframework.security.config.http.SessionCreationPolicy;
 import org.springframework.security.web.SecurityFilterChain;
 import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

  

 @Configuration
 @EnableWebSecurity
 @EnableMethodSecurity
 public class SecurityConfig {

     private final IamJwtFilter iamJwtFilter;
     
     public SecurityConfig(IamJwtFilter iamJwtFilter) {
 		super();
 		this.iamJwtFilter = iamJwtFilter;
 	}
     
     private static final Logger log =
 	        LoggerFactory.getLogger(SecurityConfig.class);


 	@Bean
     public SecurityFilterChain securityFilterChain(
             HttpSecurity http) throws Exception {
 		log.info("information recieved");
         return http
                 .csrf(csrf -> csrf.disable())
                 .sessionManagement(session ->
                         session.sessionCreationPolicy(
                                 SessionCreationPolicy.STATELESS))
                 .addFilterBefore(
                         iamJwtFilter,
                         UsernamePasswordAuthenticationFilter.class)
                 
                 .authorizeHttpRequests(auth -> auth
                         .requestMatchers(
                                 "/css/**",
                                 "/js/**",
                                 "/sso/callback/**",
                                 "/bsp_api/api/**",
                                 "/images/**")
                         .permitAll()
                         .anyRequest()
                         .authenticated())
                 .build();
     }
 }
