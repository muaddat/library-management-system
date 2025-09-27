package com.book.librarymanagement.security;

import java.io.IOException;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtValidator extends OncePerRequestFilter {
	
	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
			throws ServletException, IOException {
		String jwt = request.getHeader(JwtConstant.REQUEST_HEADER);
		if(jwt != null)
		{
			jwt = jwt.substring(7);
			
			try
			{
				SecretKey keys = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());
				Claims claims = Jwts.parserBuilder().setSigningKey(keys).build().parseClaimsJws(jwt).getBody();
				String email = String.valueOf(claims.get("email"));
				String authorities = String.valueOf(claims.get("authorities"));
				List<GrantedAuthority> auths=AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);
                Authentication authentication=new UsernamePasswordAuthenticationToken(email,null, auths);
				SecurityContextHolder.getContext().setAuthentication(authentication);		
			}
			catch(Exception e)
			{
				throw new BadCredentialsException("Invalid token");
			}
		}
		filterChain.doFilter(request, response);
}

}
