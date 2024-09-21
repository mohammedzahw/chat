package com.example.chat.registration.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.chat.exception.CustomException;
import com.example.chat.registration.dto.LoginRequestDto;
import com.example.chat.registration.model.LocalUser;
import com.example.chat.registration.oAuth2.OAuth2UserDetails;
import com.example.chat.registration.oAuth2.OAuth2UserGitHub;
import com.example.chat.registration.oAuth2.OAuth2UserGoogle;
import com.example.chat.security.TokenUtil;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class LoginService {

    private final LocalUserService localUserService;

    private final PasswordEncoder passwordEncoder;

    private final TokenUtil tokenUtil;

    private final SignUpService signUpService;

    private final EmailService emailService;

    public LoginService(LocalUserService localUserService, PasswordEncoder passwordEncoder, TokenUtil tokenUtil,
            SignUpService signUpService, EmailService emailService) {
        this.localUserService = localUserService;
        this.passwordEncoder = passwordEncoder;
        this.tokenUtil = tokenUtil;
        this.signUpService = signUpService;
        this.emailService = emailService;
    }

    /********************************************************************************* */
    public String verifyLogin(LoginRequestDto loginRequest, HttpServletRequest request)
            throws SQLException, IOException {

        LocalUser user = localUserService.getLocalUserByEmail(loginRequest.getEmail());

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new CustomException("Wrong Password!", HttpStatus.BAD_REQUEST);
        }
        if (!user.getActive()) {
            String token = tokenUtil.generateToken(loginRequest.getEmail(), user.getId(), 1000);
            signUpService.sendRegistrationVerificationCode(loginRequest.getEmail(),
                    request,
                    token);
            throw new CustomException("Please verify your email first, link sent to your email", HttpStatus.BAD_REQUEST);
        }
        String token = tokenUtil.generateToken(loginRequest.getEmail(), user.getId(), 3000000);
        localUserService.saveUser(user);

        return token;

    }

    /********************************************************************************************************************/
    public void savePassword(String email, String password)
            throws SQLException, IOException, MessagingException {
        LocalUser user = localUserService.getLocalUserByEmail(email);

        user.setPassword(passwordEncoder.encode(password)); // encoded password);

        localUserService.saveUser(user);


    }

    /********************************************************************************************************************/

    public void sendResetpasswordEmail(String email, HttpServletRequest request, String token) {

        try {
            String url = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath()
                    + "/api/check-token/" + token;
            System.out.println("url : " + url);
            String subject = "Reset Password Verification";
            String senderName = "User Registration Portal Service";
            String content = "<p> Hi, " + email + ", </p>" +
                    "<p>Thank you for registering with us," + "" +
                    "Please, follow the link below to complete your registration.</p>" +
                    "<a href=\"" + url + "\">Reset password</a>" +
                    "<p> Thank you <br> Reset Password Portal Service";
            emailService.sendEmail(email, content, subject, senderName);

            // return new ResponseEntity<>("Please, check your email to reset your
            // password", HttpStatus.OK);
        } catch (Exception e) {
            throw new CustomException("Error while sending email", HttpStatus.BAD_REQUEST);

        }
    }
    /********************************************************************************************************************/

    public ResponseEntity<?> loginOuth2(Map<String, Object> principal, String registrationId)
            throws IOException, SQLException {
        OAuth2UserDetails oAuth2UserDetails;
        try {

            if (registrationId.equals("google")) {
                oAuth2UserDetails = new OAuth2UserGoogle(principal);
            } else if (registrationId.equals("github")) {

                oAuth2UserDetails = new OAuth2UserGitHub(principal);
            } else {
                throw new CustomException("Provider not supported!", HttpStatus.BAD_REQUEST);
            }
            // System.out.println("user name : " + oAuth2UserDetails.getEmail());
            LocalUser user = localUserService.getLocalUserByEmail(oAuth2UserDetails.getEmail());

            if (user == null) {
                user = signUpService.registerOuth2(oAuth2UserDetails);
            }

            localUserService.saveUser(user);
            String token = tokenUtil.generateToken(user.getEmail(), user.getId(), 3000000);

            return new ResponseEntity<>(token, HttpStatus.OK);
        } catch (CustomException e) {
            // System.out.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // System.out.println(e.getMessage());
           return  new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /********************************************************************************************************************/

}
