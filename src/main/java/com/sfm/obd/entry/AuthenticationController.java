package com.sfm.obd.entry;

import java.util.Date;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sfm.obd.dto.ApiResponse;
import com.sfm.obd.dto.AuthOtp;
import com.sfm.obd.dto.AuthToken;
import com.sfm.obd.dto.LoginUser;
import com.sfm.obd.dto.RequestPasswordChange;
import com.sfm.obd.dto.RequestPasswordReset;
import com.sfm.obd.exception.EntityNotFoundException;
import com.sfm.obd.model.Trace;
import com.sfm.obd.model.Utilisateur;
import com.sfm.obd.security.JwtTokenUtil;
import com.sfm.obd.service.TraceService;
import com.sfm.obd.service.UserService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    public OTPService otpService;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private TraceService traceService;

//    private LoadingCache<String, Integer> oneTimePasswordCache;

    @RequestMapping(value = "/login_request", method = RequestMethod.POST)
    public ResponseEntity<?> register(@RequestBody LoginUser loginUser) throws AuthenticationException, MessagingException {

        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUser.getEmail(),
                        loginUser.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        int otp = otpService.generateOTP(username);

        //Generate The mail to send OTP
        final Utilisateur user = userService.findByEmail(loginUser.getEmail());
        emailService.sendOtpMessage(loginUser.getEmail(), "CFL-Code Sécurité", String.valueOf(otp),user.getFirstname());
        if(user.isEnable()) {

            return new ResponseEntity<Object>(
            		new ApiResponse("", "OK : Code envoyé",HttpStatus.OK.value()),
                    HttpStatus.OK
            );
        } else
            throw new EntityNotFoundException("Utilisateur désactivé, veuillez contacter votre administrateur");

    }


    @RequestMapping(value ="/validate_login", method = RequestMethod.POST)
    public ResponseEntity<?> validateAuthOtp(@RequestBody AuthOtp authOtp) throws AuthenticationException, MessagingException{

        final String SUCCESS = "Entered Otp is valid";
        final String FAIL = "Entered Otp is NOT valid. Please Retry!";
        final String EXPIRED = "Entered Otp is expired. You have to send an other OTP!";
        String email = authOtp.getEmail();
        final Utilisateur user = userService.findByEmail(email);
        if(user.isEnable() && user != null) {
            final String token = jwtTokenUtil.generateToken(user);

            //Validate the Otp
            if(authOtp.getOtp() >= 0){

                int serverOtp = otpService.getOtp(email);
                if(serverOtp > 0){
                    if(authOtp.getOtp() == serverOtp){
                        otpService.clearOTP(email);
                        
                     // Enregistrer une trace authentification
                    	traceService.save(new Trace(new Date(), "Authentification", user));

                        return new ResponseEntity<Object>(
                                new ApiResponse(new AuthToken(token,user), SUCCESS,HttpStatus.OK.value()),
                                HttpStatus.OK
                        );
                    }
                    else {
                        return new ResponseEntity<Object>(
                                new ApiResponse(null, FAIL,HttpStatus.BAD_REQUEST.value()),
                                HttpStatus.BAD_REQUEST
                        );

                    }
                }else {
                    return new ResponseEntity<Object>(
                            new ApiResponse(null, EXPIRED,HttpStatus.LOCKED.value()),
                            HttpStatus.LOCKED
                    );
                }
            }else {
                return new ResponseEntity<Object>(
                        new ApiResponse(null, FAIL,HttpStatus.BAD_REQUEST.value()),
                        HttpStatus.BAD_REQUEST
                );
            }
        }else{
            return new ResponseEntity<Object>(
                    new ApiResponse(null, FAIL,HttpStatus.BAD_REQUEST.value()),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
    
    
    @RequestMapping(value = "/password-reset-request", method = RequestMethod.POST)
	public ResponseEntity<?> passwordResetRequest(@RequestBody RequestPasswordReset requestPasswordReset) throws MessagingException {
		ResponseEntity<?> response = null;

		boolean isSendEmailForPasswordReset = userService.passwordResetRequest(requestPasswordReset.getEmail());

		if (isSendEmailForPasswordReset)
			response = new ResponseEntity<Object>( new ApiResponse(requestPasswordReset, "Email send to "+requestPasswordReset.getEmail(),HttpStatus.OK.value()),HttpStatus.OK);
		else
			response = new ResponseEntity<Object>( new ApiResponse(null, "Token is invalid or expired",HttpStatus.BAD_REQUEST.value()),HttpStatus.BAD_REQUEST);

		return response;
	}


	@RequestMapping(value = "/password-reset", method = RequestMethod.POST)
	public ResponseEntity<?> passwordReset(@RequestBody RequestPasswordChange passwordChange) {
		ResponseEntity<?> response = null;

		boolean isResetPassword = userService.passwordReset(passwordChange.getToken(), passwordChange.getPassword());

		if (isResetPassword)
			response = new ResponseEntity<Object>( new ApiResponse(passwordChange, "Password has been successfuly reseted...",HttpStatus.OK.value()),HttpStatus.OK);
		else
			response = new ResponseEntity<Object>( new ApiResponse(null, "Password Reset Error",HttpStatus.BAD_REQUEST.value()),HttpStatus.BAD_REQUEST);

		return response;
	}


}
