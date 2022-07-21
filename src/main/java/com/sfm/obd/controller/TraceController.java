package com.sfm.obd.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sfm.obd.dto.ApiResponse;
import com.sfm.obd.dto.RequestResultat;
import com.sfm.obd.enumer.UserRoles;
import com.sfm.obd.exception.NotAuthorizedException;
import com.sfm.obd.model.TimelineBox;
import com.sfm.obd.model.Trace;
import com.sfm.obd.model.Utilisateur;
import com.sfm.obd.service.TimelineBoxService;
import com.sfm.obd.service.TraceService;
import com.sfm.obd.service.UserService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/trace")
public class TraceController {

    @Autowired
    private TraceService traceService;
    
    @Autowired
    private TimelineBoxService timelineBoxService;
    
    @Autowired
    private UserService userService;
    
    @RequestMapping(value="/list", method = RequestMethod.POST)
    public ApiResponse listTraceByUser(@RequestBody RequestResultat requestResultat){    	
    	// Récupérer l'utilisateur actif
		Utilisateur userConnected = getConnectedUser();
		List<Trace> retour = traceService.findAllByUserIdAndDateBetween(userConnected, requestResultat.getId(),requestResultat.getDateDebut(), requestResultat.getDateFin());
		return new ApiResponse(retour, "OK", HttpStatus.OK.value());
    }
    
    @RequestMapping(value = "/timeline", method = RequestMethod.POST)
    public ApiResponse listTimelineBox(@RequestBody long idBoitier) {
    	// Récupérer l'utilisateur actif
     	Utilisateur userConnected = getConnectedUser();
     	if (userConnected.getRole()==UserRoles.Utilisateur) throw new NotAuthorizedException();
        
        List<TimelineBox> retour = timelineBoxService.findAllByBoitierId(userConnected, idBoitier);

        return new ApiResponse(retour, "OK", HttpStatus.OK.value());

    }
	
	private Utilisateur getConnectedUser() {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
		String email = userDetails.getUsername();
		return userService.findByEmail(email);
	}

}
