package com.cos.security1.controller;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller //View를 리턴하겠다!!
@RequiredArgsConstructor
public class IndexController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/test/login")
    public @ResponseBody
    String loginTest(Authentication authentication,
                     @AuthenticationPrincipal PrincipalDetails PrincipalDetails) {
        //DI의존성 추가 @AuthenticationPrincipal를 통해 세션정보에 접근 가능하다.
        //@AuthenticationPrincipal는 UserDetails타입을 가지고 있다.
        System.out.println("/test/login ===========================");
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("authentication: " + principalDetails.getUser()); //

        System.out.println("userDetails :" + PrincipalDetails.getUser()); //username
        //Authentication authentication / @AuthenticationPrincipal PrincipalDetails PrincipalDetails는 둘다 같은 정보를 가지고 있다.
        return "세션 정보 확인하기";
    }

    @GetMapping("/test/oauth/login")
    public @ResponseBody
    String testOAuthLogin(Authentication authentication,
                          @AuthenticationPrincipal OAuth2User oauth) { //DI(의존성 주입)
        System.out.println("/test/oauth/login ===========================");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        System.out.println("authentication: " + oAuth2User.getAttributes().toString());

        System.out.println("oauth: " + oauth.getAttributes());

        return "OAuth 세션 정보 확인하기";
    }

    //localhost:8090/
    //localhost:8090
    @GetMapping({"", "/"})
    public String index() {
        // 머스테치 기본폴더 src/main/resources/
        // 뷰리졸버 설정 : templates(prefix),.mustache(suffix) 생략가능!!
        return "index"; // src/main/resources/templates/index.mustache
    }

    //OAuth 로그인을 해도 PrincipalDetails
    //일반 로그인을 해도 PrincipalDetails
    //@AuthenticationPrincipal 해당 어노테이션은 각각의 PrincipalDetailsService의 new PrincipalDetails을 리턴할 때 해당 어노테이션에 저장된다.
    @GetMapping("/user")
    public @ResponseBody
    String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        System.out.println("principalDetails: " + principalDetails.getUser());
        return "user";
    }

    @GetMapping("/admin")
    public @ResponseBody
    String admin() {
        return "admin";
    }

    @GetMapping("/manager")
    public @ResponseBody
    String manager() {
        return "manager";
    }

    // 스프링시큐리티 해당주소를 낚아채버리네요!!
    @GetMapping("/loginForm")
    public String loginForm() {
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "joinForm";
    }

    @PostMapping("/join")
    public String join(User user) {
        System.out.println(user);
        user.setRole("ROLE_USER");
        String rawPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encPassword);
        userRepository.save(user);
        //회원가입 잘됨. 다만 비밀번호:1234-> 시큐리티로 로그인불가
        //왜 why? 패스워드가 암호화가 안되었기 때문
        return "redirect:/loginForm";
    }

    @Secured("ROLE_ADMIN") // 간단하게 권한을 거는 것
    @GetMapping("/info")
    public @ResponseBody
    String info() {
        return "개인정보";
    }

    //PreAuthorize -> 메서드가 실행되기 직전에 실행됨됨 형식은 hasRole로 적어야함 OR 가능하다는점
    // 즉 하나만 권한을 하나만 쓰고 싶으면 SECURED를 여러개를 사용하고 싶으면 PreAuthorize를 사용하면된다.
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
//    @PostAuthorize() 애는 메서드가 실행이 되고 난 뒤에 뭔가를 하는 애다.
    @GetMapping("/data")
    public @ResponseBody
    String data() {
        return "데이터정보";
    }
}
