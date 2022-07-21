package com.sfm.obd.security;

import static com.sfm.obd.config.Constants.HEADER_STRING;
import static com.sfm.obd.config.Constants.TOKEN_PREFIX;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sfm.obd.exception.ErrorException;
import com.sfm.obd.model.Utilisateur;
import com.sfm.obd.service.UserService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private UserService utilisateurService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(HEADER_STRING);
        String email = null;
        String role = null;
        String authToken = null;
        
        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            authToken = header.replace(TOKEN_PREFIX,"");
            
            try {
                email = jwtTokenUtil.getEmailFromToken(authToken);
                role = jwtTokenUtil.getRoleFromToken(authToken);
                
                Utilisateur utilisateur = utilisateurService.findByEmail(email);
                
                if (!utilisateur.isEnable()) {
            		req.setAttribute("expired", "Session expirée");
            		throw new ErrorException("Vous n'êtes pas autorisés à effectuer cette action !");
    			}
                
            } catch (IllegalArgumentException e) {
                logger.error("an error occured during getting username and role from token", e);
            } catch (ExpiredJwtException  e) {
                logger.warn("the token is expired and not valid anymore", e);
                req.setAttribute("expired", e.getMessage());
            } catch(SignatureException e){
                logger.error("Authentication Failed. Username or Password not valid.");
            } catch (Exception e) {
				email = null;
				e.printStackTrace();
			}
        } else {
            logger.warn("couldn't find bearer string, will ignore the header");
        }
        
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            if (jwtTokenUtil.validateToken(authToken, userDetails)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, Arrays.asList(new SimpleGrantedAuthority(role)));
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                logger.info("authenticated user " + email + ", setting security context");
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(req, res);
    }
}