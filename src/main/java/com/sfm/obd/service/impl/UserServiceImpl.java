package com.sfm.obd.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.sfm.obd.dao.PasswordResetTokenDao;
import com.sfm.obd.entry.EmailService;
import com.sfm.obd.model.PasswordResetToken;
import com.sfm.obd.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.sfm.obd.dao.TraceDao;
import com.sfm.obd.dao.UserDao;
import com.sfm.obd.dto.EntityPage;
import com.sfm.obd.dto.PageUtil;
import com.sfm.obd.dto.UserDto;
import com.sfm.obd.enumer.UserRoles;
import com.sfm.obd.exception.EntityNotFoundException;
import com.sfm.obd.exception.ErrorException;
import com.sfm.obd.exception.NotAuthorizedException;
import com.sfm.obd.model.Entreprise;
import com.sfm.obd.model.Trace;
import com.sfm.obd.model.Utilisateur;
import com.sfm.obd.service.UserService;

import javax.mail.MessagingException;

@Service(value = "userService")
public class UserServiceImpl implements UserDetailsService, UserService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private PasswordResetTokenDao passwordResetTokenDao;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private EmailService emailService;

	@Autowired
	private TraceDao traceDao;

	@Autowired
	private BCryptPasswordEncoder bcryptEncoder;

	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Utilisateur user = userDao.findByEmail(email);
		if (user == null) {
			throw new EntityNotFoundException("Email ou mot de passe incorrect");
		}
		if (!user.isEnable()) {
			throw new EntityNotFoundException("Utilisateur désactivé, veuillez contacter votre administrateur");
		}
		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
				getAuthority());
	}

	private List<SimpleGrantedAuthority> getAuthority() {
		return Arrays.asList(new SimpleGrantedAuthority("Administrateur"));
	}

	@Override
	public List<Utilisateur> findAll() {
		return userDao.findAll();
	}
	
	@Override
	public List<Utilisateur> findByRole(UserRoles role) {
		return userDao.findByRole(role);
	}

	@Override
	public void delete(Utilisateur userConnected,long id) {
		
		Utilisateur user =  userDao.findById(id)
				.orElseGet(() -> userDao.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable")));
		
		// Préparation de l'action pour la traçabilité
		String action = "Suppression utilisateur : " + System.lineSeparator() + user.toString();
		
		switch (userConnected.getRole()) {
		case SuperAdministrateur:
			// Il a le droit
			break;
			
		case Administrateur:
			
			//S'assurer que c'est un utilisateur de la même entreprise et que c'est pas son profil
			if (user.getEntreprise().getId() != userConnected.getEntreprise().getId() 
			|| user.getId() == userConnected.getId()) {
				throw new ErrorException("Vous n'êtes pas autorisés à effectuer cette action");
			}
			break;
			
		case Utilisateur:
			throw new ErrorException("Vous n'êtes pas autorisés à effectuer cette action");

		default:
			break;
		}
		
		userDao.deleteById(id);
		
		// Sauvegarder la trace
		traceDao.save(new Trace(new Date(), action, userConnected));
	}

	@Override
	public Utilisateur findByEmail(String email) {
		 return userDao.findByEmail(email);
	}

	@Override
	public Utilisateur findById(Utilisateur userConnected, long id) {
		
		Utilisateur user =  userDao.findById(id)
				.orElseGet(() -> userDao.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable")));
		
		switch (userConnected.getRole()) {
		case SuperAdministrateur:
			// Il a le droit de tout voir
			break;
			
		case Administrateur:
			//S'assurer que c'est un utilisateur de la même entreprise
			if (user.getEntreprise() == null) 
				throw new NotAuthorizedException();
			if (user.getEntreprise().getId() != userConnected.getEntreprise().getId()) {
				throw new NotAuthorizedException();
			}
			break;
			
		case Utilisateur:
			//S'assurer que c'est son propre profil
			if (user.getId() != userConnected.getId()) {
				throw new ErrorException("Vous n'êtes pas autorisés à effectuer cette action");
			}	
			break;

		default:
			break;
		}
		
		return user;
	}

	@Override
	public long countByRole(UserRoles role) {
		return userDao.countByRole(role);
	}

	@Override
	public Utilisateur save(UserDto userDto, Utilisateur userConnected) {
		
		Utilisateur user = null;
		
		// Préparation de l'action pour la traçabilité
		String action;
		if(userDto.getId() == 0) {
    		action = "Ajout utilisateur : " + System.lineSeparator();
    	} else {
    		action = "Modification utilisateur : " +  System.lineSeparator();
    	}
		
		switch (userConnected.getRole()) {
		case SuperAdministrateur:
			if (userDto.getId() == 0) {
				// Nouvel utilisateur
				throw new ErrorException("Vous n'êtes pas autorisés à effectuer cette action");
			} else {
				// Utilisateur existant
				user = userDao.findById(userDto.getId()).get();
				
				// Traces de l'ancien
				action += user.toString() + " ==> " + System.lineSeparator();
				
				String password = user.getPassword();
				UserRoles role = user.getRole();

				user = new Utilisateur(userDto);
				user.setRole(role);
				user.setPassword(password);
				
				// Traces du nouveau
				action += user.toString();
				
			}
			break;
			
		case Administrateur: // Il peut modifier son propre profil et celui des utilisateurs de son entreprise
			if (userDto.getId() == 0) {
				// Nouvel utilisateur
				user = new Utilisateur(userDto);
				user.setRole(UserRoles.Utilisateur);
				user.setEntreprise(userConnected.getEntreprise());
				user.setPassword(bcryptEncoder.encode(userDto.getPassword()));
				
				// Traces
				action += user.toString();
				
			} else {
				// Utilisateur existant
				user = userDao.findById(userDto.getId()).get();
				
				// Traces de l'ancien
				action += user.toString() + " ==> " + System.lineSeparator();
				
				String password = user.getPassword();
				UserRoles role = user.getRole();
				
				//S'assurer que c'est un utilisateur de la même entreprise
				if (user.getEntreprise().getId() != userConnected.getEntreprise().getId()) {
					throw new ErrorException("Vous n'êtes pas autorisés à effectuer cette action");
				}
				
				user = new Utilisateur(userDto);
				user.setRole(role);
				user.setEntreprise(userConnected.getEntreprise());
				user.setPassword(password);
				
				// Traces du nouveau
				action += user.toString();
				
			}
			break;
			
		case Utilisateur: // Il ne peut modifier que son propre profil
			if (userDto.getId() == 0) {
				// Nouvel utilisateur
				throw new ErrorException("Vous n'êtes pas autorisés à effectuer cette action");
			} else {
				// Utilisateur existant
				user = userDao.findById(userDto.getId()).get();
				
				// Traces de l'ancien
				action += user.toString() + " ==> " + System.lineSeparator();
				
				String password = user.getPassword();
				UserRoles role = user.getRole();
				Entreprise entreprise = user.getEntreprise();
				
				//S'assurer que c'est son propre profil
				if (user.getId() != userConnected.getId()) {
					throw new ErrorException("Vous n'êtes pas autorisés à effectuer cette action");
				}
				
				user = new Utilisateur(userDto);
				user.setRole(role);
				user.setEntreprise(entreprise);
				user.setPassword(password);
				
				// Traces du nouveau
				action += user.toString();
			}
			break;

		default:
			break;
		}
		
		user =  userDao.save(user);
		
		// Sauvegarder la trace
		traceDao.save(new Trace(new Date(), action, userConnected));
		
		return user;
	}

	@Override
	public Utilisateur changePassword(Utilisateur userConnected, Utilisateur userToChange) {
		
		// Préparation de l'action pour la traçabilité
		String action = "Changement de mot de passe utilisateur : " + System.lineSeparator();
			
			Utilisateur user = userDao.findById(userToChange.getId()).get();
			if (user == null)
				throw new EntityNotFoundException("Utilisateur introuvable");
			
			// Traces
			action += user.toString();
			
			switch (userConnected.getRole()) {
			case SuperAdministrateur:
				// Il a le droit
				break;
				
			case Administrateur:				
				//S'assurer que c'est un utilisateur de la même entreprise
				if (user.getEntreprise().getId() != userConnected.getEntreprise().getId()) {
					throw new ErrorException("Vous n'êtes pas autorisés à effectuer cette action");
				}
				break;
				
			case Utilisateur:
				// Il peut changer uniquement son propre mot de passe
				if (user.getId() != userConnected.getId()) {
					throw new ErrorException("Vous n'êtes pas autorisés à effectuer cette action");
				}

			default:
				break;
			}
				
			user.setPassword(bcryptEncoder.encode(userToChange.getPassword()));
			user =  userDao.save(user);
			
			// Sauvegarder la trace
			traceDao.save(new Trace(new Date(), action, userConnected));
			
			return user;
		
	}

	@Override
	public Utilisateur enable(Utilisateur userConnected, Utilisateur userToChange) {
		
		// Préparation de l'action pour la traçabilité
		String action = "Activation / Désactivation utilisateur : " + System.lineSeparator();
		
			
			Utilisateur user = userDao.findById(userToChange.getId()).get();
			if (user == null)
				throw new EntityNotFoundException("Utilisateur introuvable");
			
			// Traces
			action += user.toString() + " ==> " + System.lineSeparator();
			
			switch (userConnected.getRole()) {
			case SuperAdministrateur:
				// Il a le droit
				break;
				
			case Administrateur:				
				//S'assurer que c'est un utilisateur de la même entreprise
				if (user.getEntreprise().getId() != userConnected.getEntreprise().getId()) {
					throw new ErrorException("Vous n'êtes pas autorisés à effectuer cette action");
				}
				break;
				
			case Utilisateur:
				// Non autorisé
				throw new ErrorException("Vous n'êtes pas autorisés à effectuer cette action");

			default:
				break;
			}

			user.setEnable(userToChange.isEnable());
			user = userDao.save(user);
			
			// Traces
			action += user.toString();
			
			// Sauvegarder la trace
			traceDao.save(new Trace(new Date(), action, userConnected));
			
			return user;

	}

	@Override
	public EntityPage<Utilisateur> findByNomContaining(Utilisateur userConnected, String keyWord, Pageable pageable) {
		
		Page<Utilisateur> usersPage;

		if (userConnected.getRole() == UserRoles.SuperAdministrateur) {
			usersPage = userDao.findByKeyword(keyWord, pageable);
		} else if (userConnected.getRole() == UserRoles.Administrateur) {
			usersPage = userDao.findByKeywordAndEntreprise(keyWord, userConnected.getEntreprise().getId(), pageable);
		} else {
			usersPage = userDao.findById(userConnected.getId(), pageable);
		}

		EntityPage<Utilisateur> users = new EntityPage<Utilisateur>();

		users.setList(usersPage.getContent());

		PageUtil pageUtil = new PageUtil();
		pageUtil.setNombreElementParPage(usersPage.getNumberOfElements());
		pageUtil.setNombrePage(usersPage.getTotalPages());
		pageUtil.setNumeroPage(usersPage.getNumber() + 1);
		pageUtil.setNombreTotalElement(usersPage.getTotalElements());

		users.setPageUtil(pageUtil);

		return users;
	}

	@Override
	public EntityPage<Utilisateur> findByRoleAndKeyword(Utilisateur userConnected, UserRoles role, String keyWord, Pageable pageable) {
		
		Page<Utilisateur> usersPage;
		
		if (userConnected.getRole() == UserRoles.SuperAdministrateur) {
			usersPage = userDao.findByRoleAndKeyword(keyWord, role, pageable);
		} else if (userConnected.getRole() == UserRoles.Administrateur) {
			usersPage = userDao.findByRoleAndKeywordAndEntreprise(keyWord, userConnected.getEntreprise().getId(), role, pageable);
		} else {
			usersPage = userDao.findById(userConnected.getId(), pageable);
		}

		EntityPage<Utilisateur> users = new EntityPage<Utilisateur>();

		users.setList(usersPage.getContent());

		PageUtil pageUtil = new PageUtil();
		pageUtil.setNombreElementParPage(usersPage.getNumberOfElements());
		pageUtil.setNombrePage(usersPage.getTotalPages());
		pageUtil.setNumeroPage(usersPage.getNumber() + 1);
		pageUtil.setNombreTotalElement(usersPage.getTotalElements());

		users.setPageUtil(pageUtil);

		return users;
	}

	@Override
	public boolean passwordResetRequest(String email) throws MessagingException {
		boolean isReset = false;

		Utilisateur userEntity = userDao.findByEmail(email);
		if (userEntity == null)
			return isReset;

		String token = jwtTokenUtil.generatePasswordResetToken(userEntity);

		PasswordResetToken passwordResetToken = new PasswordResetToken();
		passwordResetToken.setToken(token);
		passwordResetToken.setUser(userEntity);
		PasswordResetToken savedPasswordResetToken = passwordResetTokenDao.save(passwordResetToken);

		if (savedPasswordResetToken != null)
			isReset = true;

		// Send the email
		emailService.sendResetPasswordMessage(userEntity.getEmail(), "Confirmation du changement mot de passe!", "Pour terminer le process du changement du mot de passe, vous devez cliquer sur ce lien : "
				+ "http://cfl.sfmtechnologies.com/confirm-reset?token="+token,userEntity.getFirstname());

		return isReset;
	}

	@Override
	public boolean passwordReset(String token, String password) {
		boolean isReset = false;

		if (jwtTokenUtil.isTokenExpired(token))
			return isReset;

		PasswordResetToken passwordResetTokenEntity = passwordResetTokenDao.findByToken(token);

		if (passwordResetTokenEntity == null)
			return isReset;

		// Create new Encoded Password
		String encodedPassword = bcryptEncoder.encode(password);

		// change password
		Utilisateur userEntity = passwordResetTokenEntity.getUser();
		userEntity.setPassword(encodedPassword);
		Utilisateur savedUserEntity = userDao.save(userEntity);

		// check password is change or not
		if (savedUserEntity != null && savedUserEntity.getPassword().equalsIgnoreCase(encodedPassword)) {
			isReset = true;
		}

		// delete token from database
		passwordResetTokenDao.delete(passwordResetTokenEntity);

		return isReset;
	}
}
