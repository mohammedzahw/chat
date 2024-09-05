package com.example.chat.registration.Controller;

import java.io.IOException;
import java.sql.SQLException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.chat.exception.CustomException;
import com.example.chat.registration.Service.LocalUserService;
import com.example.chat.registration.Service.LoginService;
import com.example.chat.registration.Service.SignUpService;
import com.example.chat.registration.dto.ChangePasswordRequestDto;
import com.example.chat.registration.dto.LoginRequestDto;
import com.example.chat.registration.model.LocalUser;
import com.example.chat.security.TokenUtil;
import com.example.chat.shared.Validator;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController

@RequestMapping("api/")
public class LoginController {

    private final SignUpService signUpService;

    private final LocalUserService localUserService;

    private final TokenUtil tokenUtil;

    private final LoginService loginService;

    public LoginController(SignUpService signUpService, LocalUserService localUserService, TokenUtil tokenUtil,
            LoginService loginService) {
        this.signUpService = signUpService;
        this.localUserService = localUserService;
        this.tokenUtil = tokenUtil;
        this.loginService = loginService;
    }

    /***************************************************************************************************************/
    @PostMapping("/login/custom")
    public ResponseEntity<?> loginCustom(@RequestBody @Valid LoginRequestDto loginRequest, BindingResult result,
            HttpServletRequest request)
            throws MessagingException, SQLException, IOException {
        if (result.hasErrors()) {
            return Validator.validate(result);
        }
        String token = loginService.verifyLogin(loginRequest, request);

        return new ResponseEntity<>(token, HttpStatus.OK);

    }

    /****************************************************************************************************************/

    @PostMapping("/forget-password")

    public ResponseEntity<?> enterEmail(@RequestBody @Valid ChangePasswordRequestDto changePasswordRequest,
            BindingResult result,
            HttpServletRequest request)
            throws MessagingException, SQLException, IOException {

        if (result.hasErrors()) {
            return Validator.validate(result);
        }

        LocalUser LocalUser = localUserService.getLocalUserByEmail(changePasswordRequest.getEmail());
        if (!LocalUser.getActive()) {
            signUpService.sendRegistrationVerificationCode(changePasswordRequest.getEmail(), request,
                    tokenUtil.generateToken(changePasswordRequest.getEmail(), LocalUser.getId(), 900));

            return new ResponseEntity<>("Activation link sent to your email", HttpStatus.OK);
        }
        loginService.sendResetpasswordEmail(changePasswordRequest.getEmail(), request,
                tokenUtil.generateToken(changePasswordRequest.getEmail() + "," + changePasswordRequest.getPassword(),
                        LocalUser.getId(), 900));

        return new ResponseEntity<>("Password reset link sent to your email", HttpStatus.OK);
    }

    /*************************************************************************************************************/
    @SuppressWarnings("unused")
    @GetMapping("/check-token/{token}")
    public ResponseEntity<?> savePassword(@PathVariable("token") String token, HttpServletResponse response)
            throws SQLException, IOException, MessagingException {

        String email = tokenUtil.getUserName(token).split(",")[0];

        String password = tokenUtil.getUserName(token).split(",")[1];

        LocalUser LocalUser = localUserService.getLocalUserByEmail(email);

        if (tokenUtil.isTokenExpired(token)) {
            // response.sendRedirect("https://localhost:8080//reset-password/?token=invalid");
            throw new CustomException("Token is expired", HttpStatus.BAD_REQUEST);
        }
        loginService.savePassword(email, password);

        return new ResponseEntity<>("Password Changed Successfully", HttpStatus.OK);
    }
}
