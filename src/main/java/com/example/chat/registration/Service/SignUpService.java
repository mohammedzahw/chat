package com.example.chat.registration.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.chat.exception.CustomException;
import com.example.chat.mapper.LocalUserMapper;
import com.example.chat.registration.dto.SignUpRequestDto;
import com.example.chat.registration.model.LocalUser;
import com.example.chat.registration.model.Role;
import com.example.chat.security.TokenUtil;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service

public class SignUpService {

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

    public void saveUser(SignUpRequestDto request) throws MessagingException, IOException, SQLException {
        try {

            request.setPassword(passwordEncoder.encode(request.getPassword()));
            LocalUser user = LocalUserMapper.INSTANCE.toEntity(request);
            user.setActive(false);

            Role role = roleService.getByRole("ROLE_USER");
            localUserService.saveUser(user);
            if (role == null) {
                role = new Role();
                role.setRole("ROLE_USER");
            }
            user.setRoles(List.of(role));

            localUserService.saveUser(user);

        } catch (Exception e) {

            throw new CustomException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    // /******************************************************************************************************************/

    public void verifyEmail(String token, HttpServletResponse response) throws SQLException, IOException {

        if (tokenUtil.isTokenExpired(token)) {
            throw new CustomException("Token is expired", HttpStatus.BAD_REQUEST);
        }

        LocalUser User = localUserService.getLocalUserById(tokenUtil.getUserId());

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
    // public User registerOuth2(OAuth2UserDetails oAuth2UserDetails) throws
    // IOException, SerialException, SQLException {
    // User user = new User();

    // user.setEmail(oAuth2UserDetails.getEmail());
    // user.setFirstName(oAuth2UserDetails.getFirstName());
    // user.setLastName(oAuth2UserDetails.getLastName());
    // user.setEnabled(true);
    // Role role = roleRepository.findByRole("ROLE_USER").orElse(null);
    // user.setRoles(List.of(role));
    // user.setLastLogin(LocalDateTime.now());
    // user.setPassword(passwordEncoder.encode("password@M.reda.49"));
    // user.setRegistrationDate(LocalDateTime.now());
    // // user.setProfilePicture(downloadImage(oAuth2UserDetails.getPicture()));

    // return user;
    // }

    /******************************************************************************************************************/
    // public byte[] downloadImage(String imageUrl) throws IOException,
    // SerialException, SQLException {
    // RestTemplate restTemplate = new RestTemplate();

    // // Make a request to the image URL
    // ResponseEntity<byte[]> response = restTemplate.getForEntity(imageUrl,
    // byte[].class);

    // return response.getBody();

    // }
}

/******************************************************************************************************************/
