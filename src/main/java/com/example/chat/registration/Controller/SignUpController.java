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

import com.example.chat.registration.Service.LocalUserService;
import com.example.chat.registration.Service.SignUpService;
import com.example.chat.registration.dto.SignUpRequestDto;
import com.example.chat.registration.model.LocalUser;
import com.example.chat.registration.repository.LocalUserRepository;
import com.example.chat.security.TokenUtil;
import com.example.chat.shared.Validator;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController

@RequestMapping("api/")
public class SignUpController {

    private final SignUpService signUpService;

    private final LocalUserRepository localUserRepository;

    private final TokenUtil tokenUtil;

    public SignUpController(SignUpService signUpService, LocalUserRepository localUserRepository,  TokenUtil tokenUtil) {
        this.signUpService = signUpService;
        this.localUserRepository = localUserRepository;
        this.tokenUtil = tokenUtil;
    }

    /******************************************************************************************************************/

    /******************************************************************************************************************/

    @PostMapping(value = "/signup")

    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequestDto signUpRequest, BindingResult result,
            HttpServletRequest request) throws MessagingException, IOException, SQLException {
        // return new Response(HttpStatus.OK, "ok", signUpRequest.getEmail());
        if (result.hasErrors()) {
            return Validator.validate(result);
        }
        LocalUser LocalUser = localUserRepository.findByEmail(signUpRequest.getEmail()).orElse(null);
        if (LocalUser != null) {
            return new ResponseEntity<>("Email already exists , Please login", HttpStatus.BAD_REQUEST);
        }

        signUpService.saveUser(signUpRequest);

        String token = tokenUtil.generateToken(signUpRequest.getEmail(), 1000, 1000);

        signUpService.sendRegistrationVerificationCode(signUpRequest.getEmail(), request,
                token);

        return new ResponseEntity<>("Activation link sent to your email", HttpStatus.OK);
    }

    /******************************************************************************************************************/

    @GetMapping("/verifyEmail/{token}")
    public ResponseEntity<?> verifyEmail(@PathVariable("token") String verficationToken,
            HttpServletResponse response)
            throws SQLException, IOException {

         signUpService.verifyEmail(verficationToken, response);

        return new ResponseEntity<>("Account is verified, you can login now", HttpStatus.OK);
    }

}