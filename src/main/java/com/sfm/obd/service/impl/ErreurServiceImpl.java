package com.sfm.obd.service.impl;

import com.sfm.obd.dao.ErreurDao;
import com.sfm.obd.model.Erreur;
import com.sfm.obd.service.ErreurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "erreurService")
public class ErreurServiceImpl implements ErreurService {

    @Autowired
    private ErreurDao erreurDao;

    @Override
    public Erreur save(Erreur erreur) {
        return erreurDao.save(erreur);
    }
}
