package com.sfm.obd.security;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
		
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		
		 	final String expiredMsg = (String) request.getAttribute("expired");
		    final String msg = (expiredMsg != null) ? expiredMsg : "Unauthorized";
		    
		    System.out.println("EntryPoint  ----------------> Message : " + msg);
		    
		    Map<String, Object> aMap = new HashMap<>();
	        aMap.put("status", response.getStatus());
	        aMap.put("error", "Unauthorized");
	        aMap.put("message", msg);
	        
	        byte[] body = new ObjectMapper().writeValueAsBytes(aMap);
		    
		    response.getOutputStream().write(body);
    }
}