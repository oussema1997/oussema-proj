package com.sfm.obd.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sfm.obd.dao.MesureBrutDao;
import com.sfm.obd.dto.EntityPage;
import com.sfm.obd.dto.PageUtil;
import com.sfm.obd.exception.EntityNotFoundException;
import com.sfm.obd.model.MesureBrut;
import com.sfm.obd.service.MesureBrutService;


@Service(value = "mesureBrutService")
public class MesureBrutServiceImpl implements MesureBrutService {
	
	@Autowired
	private MesureBrutDao mesureBrutDao;

	@Override
	public MesureBrut save(MesureBrut mesureBrut) {
		mesureBrut = mesureBrutDao.save(mesureBrut);
		mesureBrutDao.refresh(mesureBrut);
		return mesureBrut;
	}
	
	@Override
	public List<MesureBrut> findAll() {
		List<MesureBrut> list = new ArrayList<>();
		mesureBrutDao.findAll().iterator().forEachRemaining(list::add);
		return list;
	}

	@Override
	public void delete(long id) {
		mesureBrutDao.deleteById(id);
	}
	
	@Override
	public MesureBrut findById(Long id) {
		MesureBrut mesureBrut =  mesureBrutDao.findById(id)
				.orElseGet(() -> mesureBrutDao.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("MesureBrut introuvable")));
		
		return mesureBrut;
	}
	
	@Override
	public EntityPage<MesureBrut> findByKeyword(String keyword, Pageable pageable) {
		
		Page<MesureBrut> mesureBrutsPage = mesureBrutDao.findByMesureContaining(keyword,pageable);

		EntityPage<MesureBrut> mesureBruts = new EntityPage<MesureBrut>();

		mesureBruts.setList(mesureBrutsPage.getContent());

		PageUtil pageUtil = new PageUtil();
		pageUtil.setNombreElementParPage(mesureBrutsPage.getNumberOfElements());
		pageUtil.setNombrePage(mesureBrutsPage.getTotalPages());
		pageUtil.setNumeroPage(mesureBrutsPage.getNumber() + 1);
		pageUtil.setNombreTotalElement(mesureBrutsPage.getTotalElements());

		mesureBruts.setPageUtil(pageUtil);

		return mesureBruts;
	}
	
	@Override
	public List<MesureBrut> brutesByDate(Date dateDebut, Date dateFin) {
		return mesureBrutDao.findByDateBetween(dateDebut, dateFin);
	}


}
