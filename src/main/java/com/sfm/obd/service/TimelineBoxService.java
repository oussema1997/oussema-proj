package com.sfm.obd.service;

import java.util.List;

import com.sfm.obd.model.TimelineBox;
import com.sfm.obd.model.Utilisateur;

public interface TimelineBoxService {
	List<TimelineBox> findAllByBoitierId(Utilisateur userConnected, long boitierId);
}
