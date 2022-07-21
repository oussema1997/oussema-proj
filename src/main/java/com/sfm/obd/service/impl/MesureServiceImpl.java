package com.sfm.obd.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sfm.obd.model.Boitier;
import com.sfm.obd.service.BoitierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sfm.obd.dao.IndicateurDao;
import com.sfm.obd.dao.MesureDao;
import com.sfm.obd.dao.TypeEtatDao;
import com.sfm.obd.dto.EntityPage;
import com.sfm.obd.dto.PageUtil;
import com.sfm.obd.dto.mesures.IndicateurMesures;
import com.sfm.obd.dto.mesures.LastMesure;
import com.sfm.obd.dto.mesures.MesureDTOLibelle;
import com.sfm.obd.dto.mesures.MesureDTOVal;
import com.sfm.obd.model.Indicateur;
import com.sfm.obd.model.Mesure;
import com.sfm.obd.service.MesureService;

@Service(value = "mesureService")
public class MesureServiceImpl implements MesureService {

    @Autowired
    private MesureDao mesureDao;
    
    @Autowired
    private IndicateurDao indicateurDao;

    @Autowired
    private BoitierService boitierService;
    
    @Autowired
    private TypeEtatDao typeEtatDao;

    @Override
    public Mesure save(Mesure mesure) {
        mesure = mesureDao.save(mesure);
        mesureDao.refresh(mesure);
        return mesure;
    }

    @Override
    public List<Mesure> findAll() {
        List<Mesure> list = new ArrayList<>();
        mesureDao.findAll().iterator().forEachRemaining(list::add);
        return list;
    }

    @Override
    public void delete(long id) {
        mesureDao.deleteById(id);
    }

    @Override
    public Mesure findById(Long id) {
        return mesureDao.findById(id).get();
    }

    @Override
    public EntityPage<Mesure> listMesure(long boitier_id, long indicateur_id, Pageable pageable) {
        Page<Mesure> mesuresPage = mesureDao.findByBoitierIdAndIndicateurId(boitier_id, indicateur_id, pageable);

        EntityPage<Mesure> mesures = new EntityPage<Mesure>();

        mesures.setList(mesuresPage.getContent());

        PageUtil pageUtil = new PageUtil();
        pageUtil.setNombreElementParPage(mesuresPage.getNumberOfElements());
        pageUtil.setNombrePage(mesuresPage.getTotalPages());
        pageUtil.setNumeroPage(mesuresPage.getNumber() + 1);
        pageUtil.setNombreTotalElement(mesuresPage.getTotalElements());

        mesures.setPageUtil(pageUtil);

        return mesures;
    }
    
	@Override
    public List<Mesure> listMesureByDate(long boitier_id, long indicateur_id, Date debut, Date fin) {
        List<Mesure> mesures = mesureDao.findByBoitierIdAndIndicateurIdAndDateBetweenOrderByDate(boitier_id, indicateur_id, debut, fin);
        return mesures;
    }
	
	@Override
	public LastMesure lastMesures(long idBoitier) {
		
		LastMesure retour = new LastMesure();
		List<MesureDTOLibelle> retourList = new ArrayList<>();
		
		List<Indicateur> indicateurs = indicateurDao.findAll();
		if (!indicateurs.isEmpty())
		for (Indicateur indicateur : indicateurs) {
			Mesure mesure = mesureDao.findTopByBoitierIdAndIndicateurIdOrderByIdDesc(idBoitier, indicateur.getId());
			if (mesure != null) {
				MesureDTOLibelle m = new MesureDTOLibelle(
						mesure.getVal1(), mesure.getVal2(), mesure.getVal3(),
						indicateur.getId(),indicateur.getLibelle());
				//Récupération de l'état
				/*if (indicateur.getId() == indicEtatWagon) {
					TypeEtat typeEtat = typeEtatDao.findById(Long.parseLong(mesure.getVal1())).get(); 
					retour.setEtat(new EtatDTO(typeEtat.getId(), typeEtat.getLibelle()));
				}*/
				retourList.add(m);
			}
		}
		
		retour.setMesures(retourList);
		
		return retour;
		
	}
	
//	@Override
//	public List<IndicateurMesures> allMesuresByDate(long idBoitier, Date dateDebut, Date dateFin){
//		
//		List<IndicateurMesures> retour = new ArrayList<>();
//		
//		List<Indicateur> indicateurs = indicateurDao.findAll();
//		if (!indicateurs.isEmpty())
//		for (Indicateur indicateur : indicateurs) {
//			List<MesureDTOVal> dtoVals = mesureDao.findByBoitierAndIndicateur(idBoitier, indicateur.getId(), dateDebut, dateFin);
//			if (!dtoVals.isEmpty()) {
//				retour.add(new IndicateurMesures(indicateur,dtoVals));
//			}
//		}
//		
//		return retour;
//		
//	}
	
	@Override
	public List<IndicateurMesures> allMesuresByDate(long idBoitier, Date dateDebut, Date dateFin){
		
		List<IndicateurMesures> retour = new ArrayList<>();
		
		List<Indicateur> indicateurs = indicateurDao.findAll();
		if (!indicateurs.isEmpty())
		for (Indicateur indicateur : indicateurs) {
			
			List<MesureDTOVal> mesures = new ArrayList<>();
			
			List<?> dtoVals1 = mesureDao.findByBoitierAndIncateurVal1(idBoitier, indicateur.getId(), dateDebut, dateFin);
			if (!dtoVals1.isEmpty()) {
				mesures.add(new MesureDTOVal(dtoVals1));
			}
			
			List<?> dtoVals2 = mesureDao.findByBoitierAndIncateurVal2(idBoitier, indicateur.getId(), dateDebut, dateFin);
			if (!dtoVals2.isEmpty()) {
				mesures.add(new MesureDTOVal(dtoVals2));
			}
			
			List<?> dtoVals3 = mesureDao.findByBoitierAndIncateurVal3(idBoitier, indicateur.getId(), dateDebut, dateFin);
			if (!dtoVals3.isEmpty()) {
				mesures.add(new MesureDTOVal(dtoVals3));
			}
            List<?> dtoVals4 = mesureDao.findByBoitierAndIncateurVal4(idBoitier, indicateur.getId(), dateDebut, dateFin);
            if (!dtoVals4.isEmpty()) {
                mesures.add(new MesureDTOVal(dtoVals4));
            }
            List<?> dtoVals5 = mesureDao.findByBoitierAndIncateurVal5(idBoitier, indicateur.getId(), dateDebut, dateFin);
            if (!dtoVals5.isEmpty()) {
                mesures.add(new MesureDTOVal(dtoVals5));
            }

            Boitier box = boitierService.findById(idBoitier);
			retour.add(new IndicateurMesures(box, indicateur, mesures));
		}
		
		return retour;
		
	}
}
