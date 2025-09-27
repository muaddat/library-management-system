package com.book.librarymanagement.security;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.SecretKey;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtProvider {

	  private SecretKey keys = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());
	  
	  public String generateToken(Authentication auths) {
		  
		  Collection<? extends GrantedAuthority> authorities = auths.getAuthorities();
	      String roles = populateAuthorities(authorities);
	      
	      String jwt = Jwts.builder()
     		   .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime()+86400000))
                .claim("email", auths.getName())
                .claim("authorities", roles)
                .signWith(keys)
                .compact();
     return jwt;
	  }

	  public String getTokenFromJwt(String jwt)
		{
			jwt = jwt.substring(7);
			
			Claims claims=Jwts.parserBuilder().setSigningKey(keys).build().parseClaimsJws(jwt).getBody();
			
			String email = String.valueOf(claims.get("email"));
			return email;
		}
	  
	  
	private String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        
      Set<String> auths=new HashSet<>();
		

		for(GrantedAuthority authority:authorities) {
			auths.add(authority.getAuthority());
		}
		return String.join(",",auths);

	}
}
