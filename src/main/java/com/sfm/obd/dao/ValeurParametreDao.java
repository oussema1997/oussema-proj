package com.sfm.obd.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sfm.obd.model.Boitier;
import com.sfm.obd.model.Parametre;
import com.sfm.obd.model.ValeurParametre;
import com.sfm.obd.myrepo.MyRepository;

@Repository
public interface ValeurParametreDao extends MyRepository<ValeurParametre, Long> {
	List<ValeurParametre> findByBoitier(Boitier boitier);
	ValeurParametre findByParametreAndBoitier(Parametre parametre, Boitier boitier);
	ValeurParametre findByParametreIdAndBoitier(long paramId, Boitier boitier);
}
