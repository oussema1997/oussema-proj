package com.sfm.obd.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sfm.obd.dao.BoitierDao;
import com.sfm.obd.dao.TimelineBoxDao;
import com.sfm.obd.enumer.UserRoles;
import com.sfm.obd.exception.NotAuthorizedException;
import com.sfm.obd.model.Boitier;
import com.sfm.obd.model.TimelineBox;
import com.sfm.obd.model.Utilisateur;
import com.sfm.obd.service.TimelineBoxService;

@Service(value = "timelineBoxService")
public class TimelineBoxServiceImpl implements TimelineBoxService {

    @Autowired
    private TimelineBoxDao timelineBoxDao;
    
    @Autowired
    private BoitierDao boitierDao;
    
    @Override
    public List<TimelineBox> findAllByBoitierId(Utilisateur userConnected, 
    		long boitierId) {
    	
    	if (userConnected.getRole() == UserRoles.SuperAdministrateur) {
    		// Il a le droit
		} else if (userConnected.getRole() == UserRoles.Administrateur) {
			Boitier boitier = boitierDao.findById(boitierId).get();
			//S'assurer que c'est un utilisateur de la même entreprise
			if (boitier.getEntreprise().getId() != userConnected.getEntreprise().getId()) {
				throw new NotAuthorizedException();
			}
		} else {
			throw new NotAuthorizedException();
		}
    	
    	return timelineBoxDao.findAllByBoitierId(boitierId);
    }
    
    

}
