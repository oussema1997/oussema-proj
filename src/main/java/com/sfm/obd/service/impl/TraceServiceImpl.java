package com.sfm.obd.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sfm.obd.dao.TraceDao;
import com.sfm.obd.dao.UserDao;
import com.sfm.obd.enumer.UserRoles;
import com.sfm.obd.exception.NotAuthorizedException;
import com.sfm.obd.model.Trace;
import com.sfm.obd.model.Utilisateur;
import com.sfm.obd.service.TraceService;

@Service(value = "traceService")
public class TraceServiceImpl implements TraceService {

	@Autowired
	private TraceDao traceDao;
	
	@Autowired
	private UserDao userDao;

	@Override
	public Trace save(Trace trace) {
		trace.setAction(trace.getAction().trim());
		return traceDao.save(trace);
	}

	@Override
	public List<Trace> findAll() {
		List<Trace> list = new ArrayList<>();
		traceDao.findAllByOrderByDateDesc().iterator().forEachRemaining(list::add);
		return list;
	}

	@Override
	public void delete(long id) {
		traceDao.deleteById(id);

	}

	@Override
	public Trace findById(Long id) {
		return traceDao.findById(id).get();
	}

	@Override
	public long count() {
		return traceDao.count();
	}
	
	@Override
	public List<Trace> findAllByUserIdAndDateBetween(Utilisateur userConnected, long id, Date debut, Date fin) {
		
		if (userConnected.getRole() == UserRoles.SuperAdministrateur) {
			//Il peut tout voir 
			if (id == 0) { // Pas d'utilisateur spécifié
				return traceDao.findAllByDateBetweenOrderByDateDesc(debut, fin);
			} else {
				return traceDao.findAllByUserIdAndDateBetweenOrderByDateDesc(id, debut, fin);
			}
		} else if (userConnected.getRole() == UserRoles.Administrateur) {
			
			if (id == 0) { // Pas d'utilisateur spécifié, il peut voir les traces de tous les utilisateurs de son entreprise
				return traceDao.findAllByUserEntrepriseAndDateBetweenOrderByDateDesc(userConnected.getEntreprise(),debut, fin);
			} else {
				//S'assurer que c'est un utilisateur de la même entreprise
				Utilisateur user = userDao.findById(id).get();
				if (user.getEntreprise() == null)
					throw new NotAuthorizedException();
				if (user.getEntreprise().getId() != userConnected.getEntreprise().getId()) {
					throw new NotAuthorizedException();
				}
				
				return traceDao.findAllByUserIdAndUserEntrepriseAndDateBetweenOrderByDateDesc(id,userConnected.getEntreprise(), debut, fin);
			}
			
		} else { // Il ne peut voir que ses propres traces
			return traceDao.findAllByUserIdAndDateBetweenOrderByDateDesc(userConnected.getId(), debut, fin);
		}
		
	}
}
