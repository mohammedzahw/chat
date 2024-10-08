package com.example.chat.registration.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.time.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.chat.exception.CustomException;
import com.example.chat.mapper.LocalUserMapper;
import com.example.chat.registration.dto.SignUpRequestDto;
import com.example.chat.registration.model.LocalUser;
import com.example.chat.registration.model.Role;
import com.example.chat.registration.oAuth2.OAuth2UserDetails;
import com.example.chat.security.TokenUtil;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;

@Service

public class SignUpService {
    private LocalUserMapper localUserMapper;
    private final EmailService emailService;

    private final LocalUserService localUserService;

    private final RoleService roleService;

    private final PasswordEncoder passwordEncoder;

    private final TokenUtil tokenUtil;

    public SignUpService(EmailService emailService, LocalUserService localUserService, RoleService roleService,
            PasswordEncoder passwordEncoder, TokenUtil tokenUtil) {
        this.emailService = emailService;
        this.localUserService = localUserService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.tokenUtil = tokenUtil;
    }
    /******************************************************************************************************************/

    public LocalUser saveUser(SignUpRequestDto request) throws MessagingException, IOException, SQLException {
        try {

            request.setPassword(passwordEncoder.encode(request.getPassword()));
            LocalUser user = localUserMapper.toEntity(request);
            user.setActive(false);

            Role role = roleService.getByRole("ROLE_USER");
            localUserService.saveUser(user);
            if (role == null) {
                role = new Role();
                role.setRole("ROLE_USER");
            }
            user.setRoles(List.of(role));

            localUserService.saveUser(user);

            return user;

        } catch (Exception e) {

            throw new CustomException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    // /******************************************************************************************************************/

    public void verifyEmail(String token) throws SQLException, IOException {

        if (tokenUtil.isTokenExpired(token)) {
            throw new CustomException("Token is expired", HttpStatus.BAD_REQUEST);
        }
        // String email = tokenUtil.getUserIdFromToken(token)
        LocalUser User = localUserService.getLocalUserById(tokenUtil.getUserIdFromToken(token));

        User.setActive(true);

        localUserService.saveUser(User);

        // return ResponseEntity.ok("Account is verified, you can login now!");
    }

    /******************************************************************************************************************/
    public void sendRegistrationVerificationCode(String email, HttpServletRequest request,
            String verficationToken) {
        try {
            String url = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath()
                    + "/api/verifyEmail/" + verficationToken;

            System.out.println("url : " + url);
            String subject = "Email Verification";
            String senderName = "User Registration Portal Service";
            String content = "<p> Hi, " + email + ", </p>" +
                    "<p>Thank you for registering with us," + "" +
                    "Please, follow the link below to complete your registration.</p>" +
                    "<a href=\"" + url + "\">Verify your email to activate your account</a>" +
                    "<p> Thank you <br> Users Registration Portal Service";
            emailService.sendEmail(email, content, subject, senderName);

            // return ResponseEntity.ok("Account is not verfied ,please check your email to
            // verify it!");
        } catch (Exception e) {
            throw new CustomException("Error while sending Email", HttpStatus.BAD_REQUEST);
        }
    }

    /**************************************************************************************************************/
    /**************************************************************************************************************/
    public LocalUser registerOuth2(OAuth2UserDetails oAuth2UserDetails)
            throws IOException, SQLException {
        LocalUser user = new LocalUser();

        user.setEmail(oAuth2UserDetails.getEmail());
        user.setName(oAuth2UserDetails.getFirstName() + oAuth2UserDetails.getLastName());

        Role role = roleService.getByRole("ROLE_USER");
        user.setRoles(List.of(role));
        user.setPassword(passwordEncoder.encode("password@M.reda.49"));
  
        // user.setProfilePicture(downloadImage(oAuth2UserDetails.getPicture()));

        return user;
    }

    /******************************************************************************************************************/
}
