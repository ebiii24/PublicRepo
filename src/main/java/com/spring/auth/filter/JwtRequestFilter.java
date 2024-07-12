package com.spring.auth.filter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.spring.auth.service.CustomUserDetailsService;
import com.spring.auth.utils.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
@AllArgsConstructor // Lombok annotation for constructor injection (optional)
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired // Injecting dependency (can be replaced with @RequiredArgsConstructor)
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // Extract the authorization header from the request
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // Check if the authorization header exists and starts with "Bearer "
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Extract the JWT token from the header
            jwt = authorizationHeader.substring(7);
            // Extract the username from the JWT using JwtUtil
            username = jwtUtil.extractUsername(jwt);
        }

        // If username is extracted and there's no authentication set in context
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load user details from the username using custom user details service
            UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(username);

            // Validate the JWT token using JwtUtil
            if (jwtUtil.validateToken(jwt, userDetails)) {

                // Create a UsernamePasswordAuthenticationToken with userDetails and authorities
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                // Set details of the token (e.g. IP address) using WebAuthenticationDetailsSource
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set the authentication object in the SecurityContext
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        // Continue processing the request through the filter chain
        chain.doFilter(request, response);
    }
}
