package vn.hoidanit.jobhunter.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.DTO.LoginDTO;
import vn.hoidanit.jobhunter.domain.DTO.RestLoginDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;
import vn.hoidanit.jobhunter.util.error.SecurityUtil;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final SecurityUtil securityUtil;
    private final UserService userService;

        @Value("${tuna.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;
    
    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder
     , SecurityUtil securityUtil,UserService userService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
    }


    @PostMapping("/auth/login")
    public ResponseEntity<RestLoginDTO> login(@Valid @RequestBody LoginDTO loginDTO){
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());
    // xac thuc ng dung
    Authentication authentication=authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    //create token
        SecurityContextHolder.getContext().setAuthentication(authentication);

    RestLoginDTO res=new RestLoginDTO();
        User curUserDB=this.userService.handleGetUserByUsername(loginDTO.getUsername());
        if (curUserDB!=null) {
            RestLoginDTO.UserLogin userLogin=new RestLoginDTO.UserLogin();
            userLogin.setId(curUserDB.getId());
            userLogin.setEmail(curUserDB.getEmail());
            userLogin.setName(curUserDB.getName());
            res.setUser(userLogin);
            
        }   
           String access_token= this.securityUtil.createAccessToken(authentication.getName(),res.getUser());
        res.setAccessToken(access_token);  


        //create refresh token
        String refresh_token=this.securityUtil.refreshToken(loginDTO.getUsername(),res);

        //update user
        this.userService.updateUserToken(refresh_token,loginDTO.getUsername());

        //set cookie
        ResponseCookie resCookies=ResponseCookie.
        from("refresh_token", refresh_token).
        httpOnly(true).
        secure(true).
        path("/").
        maxAge(refreshTokenExpiration).
        build();
    return ResponseEntity.ok()
    .header(org.springframework.http.HttpHeaders.SET_COOKIE,resCookies.toString())
    .body(res);
    }



    @GetMapping("/auth/account")
    public ResponseEntity<RestLoginDTO.UserGetAccount> getAccount() {
        String email=SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        User curUser=this.userService.handleGetUserByUsername(email);
         RestLoginDTO.UserLogin userLogin=new RestLoginDTO.UserLogin();
         RestLoginDTO.UserGetAccount userGetAccount=new RestLoginDTO.UserGetAccount();

            if (curUser!=null) {
                userLogin.setId(curUser.getId());
                userLogin.setEmail(curUser.getEmail());
                userLogin.setName(curUser.getName());
                userGetAccount.setUser(userLogin);
            }
        return ResponseEntity.ok().body(userGetAccount);
    }


        @GetMapping("/auth/refresh")
    public ResponseEntity<RestLoginDTO> getRefreshToken(@CookieValue(name="refresh_token",defaultValue = "") String refresh_token) throws IdInvalidException{
        if (refresh_token.equals("")) {
            throw new IdInvalidException("ko co refresh token");
        }
        
        //Check valid
        Jwt decodedToken=this.securityUtil.checkValidToken(refresh_token);
        String email=decodedToken.getSubject();

        //check usertrt by token + email
        User curUser=this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);
        if (curUser==null) {
            throw new IdInvalidException("Refresh Token ko hop le");
        }
        
        // issue new token/ set refresh token as cookies
        RestLoginDTO res=new RestLoginDTO();
        User curUserDB=this.userService.handleGetUserByUsername(email);
        if (curUserDB!=null) {
            RestLoginDTO.UserLogin userLogin=new RestLoginDTO.UserLogin();
            userLogin.setId(curUserDB.getId());
            userLogin.setEmail(curUserDB.getEmail());
            userLogin.setName(curUserDB.getName());
            res.setUser(userLogin);
            
        }   
           String access_token= this.securityUtil.createAccessToken(email,res.getUser());
        res.setAccessToken(access_token);  


        //create refresh token
        String new_refresh_token=this.securityUtil.refreshToken(email,res);

        //update user
        this.userService.updateUserToken(new_refresh_token,email);

        //set cookie
        ResponseCookie resCookies=ResponseCookie.
        from("refresh_token", new_refresh_token).
        httpOnly(true).
        secure(true).
        path("/").
        maxAge(refreshTokenExpiration).
        build();
    return ResponseEntity.ok()
    .header(org.springframework.http.HttpHeaders.SET_COOKIE,resCookies.toString())
    .body(res);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout() throws IdInvalidException{
        
        String email= SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() :null;
        if (email.equals("")) {
            throw new IdInvalidException("Access token ko hợp lệ");
        }
        this.userService.updateUserToken(null, email);

        ResponseCookie deletResponseCookie =ResponseCookie.from(email, null)
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(0)
        .build();

        return ResponseEntity.ok().header(org.springframework.http.HttpHeaders.SET_COOKIE,deletResponseCookie.toString())
        .body(null);
    }
    
    
    

}
