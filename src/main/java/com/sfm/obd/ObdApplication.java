package com.sfm.obd;

import com.sfm.obd.dao.UserDao;
import com.sfm.obd.enumer.UserRoles;
import com.sfm.obd.model.Utilisateur;
import com.sfm.obd.myrepo.MyRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = MyRepositoryImpl.class)
public class ObdApplication {

    @Value("${defaultUser.lastname}")
    private String lastname;

    @Value("${defaultUser.firstname}")
    private String firstname;

    @Value("${defaultUser.email}")
    private String email;

    @Value("${defaultUser.phone}")
    private String phone;

    @Value("${defaultUser.password}")
    private String password;

    @Autowired
    private UserDao userDao;

    @Autowired
    private BCryptPasswordEncoder bcryptEncoder;

    public static void main(String[] args) {
        SpringApplication.run(ObdApplication.class, args);
    }

    @PostConstruct
    public void init() {
        if (userDao.countByRole(UserRoles.SuperAdministrateur) == 0) {

            Utilisateur admin = new Utilisateur();

            admin.setPassword(bcryptEncoder.encode(password));
            admin.setRole(UserRoles.SuperAdministrateur);
            admin.setEnable(true);

            admin.setLastname(lastname);
            admin.setFirstname(firstname);
            admin.setEmail(email);
            admin.setPhone(phone);

            userDao.save(admin);

        }
    }

}
