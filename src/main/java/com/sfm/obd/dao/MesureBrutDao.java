package com.sfm.obd.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.sfm.obd.model.MesureBrut;
import com.sfm.obd.myrepo.MyRepository;

@Repository
public interface MesureBrutDao extends MyRepository<MesureBrut, Long> {
	Page<MesureBrut> findByMesureContaining(String keyword, Pageable pageable);
	List<MesureBrut> findByDateBetween(Date debut, Date fin);
}
