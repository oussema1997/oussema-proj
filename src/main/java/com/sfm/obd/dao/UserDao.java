package com.sfm.obd.dao;

import java.util.List;

import com.sfm.obd.model.EntrepriseCliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sfm.obd.enumer.UserRoles;
import com.sfm.obd.model.Entreprise;
import com.sfm.obd.model.Utilisateur;
import com.sfm.obd.myrepo.MyRepository;

@Repository
public interface UserDao extends MyRepository<Utilisateur, Long> {
    Utilisateur findByEmail(String email);
    long countByRole(UserRoles role);
    List<Utilisateur> findByRole(UserRoles role);
    List<Utilisateur> findByRoleAndEntrepriseId(UserRoles role, long id);
    Page<Utilisateur> findById(long id, Pageable pageable);
    List<Utilisateur> findByEntreprise(Entreprise entreprise);

	//List<Utilisateur> findByRole(UserRoles userRole);
    
    @Query("SELECT u from Utilisateur u WHERE "
			+ "u.role =:role AND ("
			+ "u.lastname LIKE CONCAT('%',:keyword,'%') "
			+ "OR u.firstname LIKE CONCAT('%',:keyword,'%') "
			+ "OR u.entreprise.nom LIKE CONCAT('%',:keyword,'%') "
			+ "OR u.email LIKE CONCAT('%',:keyword,'%') "
			+ ")")
	Page<Utilisateur> findByRoleAndKeyword(
			@Param("keyword") String keyword,
			@Param("role") UserRoles role,
			Pageable pageable);
    
    @Query("SELECT u from Utilisateur u WHERE "
			+ "u.role =:role "
			+ "AND u.entreprise.id = :entrepriseId "
			+ "AND ( "
			+ "u.lastname LIKE CONCAT('%',:keyword,'%') "
			+ "OR u.firstname LIKE CONCAT('%',:keyword,'%') "
			+ "OR u.entreprise.nom LIKE CONCAT('%',:keyword,'%') "
			+ "OR u.email LIKE CONCAT('%',:keyword,'%') "
			+ ")")
	Page<Utilisateur> findByRoleAndKeywordAndEntreprise(
			@Param("keyword") String keyword,
			@Param("entrepriseId") long entrepriseId,
			@Param("role") UserRoles role,
			Pageable pageable);
    
    @Query("SELECT u from Utilisateur u WHERE "
			+ "u.lastname LIKE CONCAT('%',:keyword,'%') "
			+ "OR u.firstname LIKE CONCAT('%',:keyword,'%') "
			+ "OR u.email LIKE CONCAT('%',:keyword,'%') ")
	Page<Utilisateur> findByKeyword(
			@Param("keyword") String keyword,
			Pageable pageable);
    
    @Query("SELECT u from Utilisateur u WHERE "
    		+ "(u.entreprise.id = :entrepriseId) "
    		+ "AND ( "
			+ "u.lastname LIKE CONCAT('%',:keyword,'%') "
			+ "OR u.firstname LIKE CONCAT('%',:keyword,'%') "
			+ "OR u.entreprise.nom LIKE CONCAT('%',:keyword,'%') "
			+ "OR u.email LIKE CONCAT('%',:keyword,'%') "
			+ ")")
	Page<Utilisateur> findByKeywordAndEntreprise(
			@Param("keyword") String keyword,
			@Param("entrepriseId") long entrepriseId,
			Pageable pageable);
    
    @Query("SELECT u from Utilisateur u WHERE "
    		+ "(u.entreprise.id = :entrepriseId) "
    		+ "AND ( "
			+ "u.lastname LIKE CONCAT('%',:keyword,'%') "
			+ "OR u.firstname LIKE CONCAT('%',:keyword,'%') "
			+ "OR u.phone LIKE CONCAT('%',:keyword,'%') "
			+ "OR u.email LIKE CONCAT('%',:keyword,'%') "
			+ ")")
	List<Utilisateur> findByKeywordAndEntreprise(
			@Param("keyword") String keyword,
			@Param("entrepriseId") long entrepriseId
			);
    
}
