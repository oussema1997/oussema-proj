package com.sfm.obd.dao;

import com.sfm.obd.model.PasswordResetToken;
import com.sfm.obd.myrepo.MyRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenDao extends MyRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String token);
}
