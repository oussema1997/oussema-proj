package com.sfm.obd.security;

import static com.sfm.obd.config.Constants.ACCESS_TOKEN_VALIDITY_SECONDS;
import static com.sfm.obd.config.Constants.SIGNING_KEY;
import static com.sfm.obd.config.Constants.TOKEN_ISSUER;

import java.io.Serializable;
import java.util.Date;
import java.util.function.Function;

import com.sfm.obd.config.Constants;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.sfm.obd.model.Utilisateur;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String getEmailFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
	
	public String getRoleFromToken(String token) {
		
		Claims claims = Jwts.parser()
                .setSigningKey(SIGNING_KEY)
                .parseClaimsJws(token)
                .getBody();
		return claims.get("role", String.class);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(SIGNING_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(Utilisateur user) {
        return doGenerateToken(user.getEmail(), user.getRole().name());
    }

    private String doGenerateToken(String subject, String role) {

        Claims claims = Jwts.claims().setSubject(subject);
        claims.put("role", role);
        
        return Jwts.builder()
                .setClaims(claims)
                .setIssuer(TOKEN_ISSUER)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_SECONDS*1000))
                .signWith(SignatureAlgorithm.HS256, SIGNING_KEY)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String email = getEmailFromToken(token);
        return (
              email.equals(userDetails.getUsername())
                    && !isTokenExpired(token));
    }

    public String generatePasswordResetToken(Utilisateur user) {
        Claims claims = Jwts.claims().setSubject(user.getEmail());
        claims.put("role", user.getRole());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer(TOKEN_ISSUER)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + Constants.PASSWORD_RESET_EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SIGNING_KEY)
                .compact();

    }

}
