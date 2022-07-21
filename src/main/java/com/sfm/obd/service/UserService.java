package com.sfm.obd.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.sfm.obd.dto.EntityPage;
import com.sfm.obd.dto.UserDto;
import com.sfm.obd.enumer.UserRoles;
import com.sfm.obd.model.Utilisateur;

import javax.mail.MessagingException;

public interface UserService {

	Utilisateur save(UserDto userDto, Utilisateur userConnected);
    List<Utilisateur> findAll();
    void delete(Utilisateur userConnected, long id);
    Utilisateur findByEmail(String email);
    
    Utilisateur changePassword(Utilisateur userConnected, Utilisateur userToChange);
    Utilisateur enable(Utilisateur userConnected, Utilisateur user);

    Utilisateur findById(Utilisateur userConnected, long id);
    long countByRole(UserRoles role);
    
    List<Utilisateur> findByRole(UserRoles role);
    
    EntityPage<Utilisateur> findByNomContaining(Utilisateur userConnected, String nom, Pageable pageable);
    EntityPage<Utilisateur> findByRoleAndKeyword(Utilisateur userConnected, UserRoles role, String keyword, Pageable pageable);

    boolean passwordResetRequest(String email) throws MessagingException;
    boolean passwordReset(String token, String password);
}
