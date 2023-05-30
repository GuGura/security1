package com.cos.security1.config;

import com.cos.security1.config.oauth.PrincipalOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity //스프링 시큐리티 필터가 스프링 필터체인에 등록이 된다.
//securedEnabled = secured 어노테이션 활성화 ->controller 매핑되는 엔드포인트 간단하게 권한을 걸 수 있다.
//prePostEnabled = preAuthorize,PostAuthorize 어노테이션 활성화 -> pre/PostEnabled이기 때문
//antMatchers로 글로벌하게 제한을 걸고 메소드 한두개만 따로 걸고 싶을때 주로 securedEnabled을 사용한다. prePost는 잘안쓰지만 알아는 두자
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig {

    private final PrincipalOAuth2UserService principalOAuth2UserService;



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.csrf().disable() //crsf(크로스 사이트 요청 위조)를 비활성화
                .authorizeRequests() //시큐리티 처리에 HttpServletRequest를 이용한다는 것을 의미
                .antMatchers("/user/**").authenticated() ///user/** 로 매핑되면 인증이 필요하다는 것을 의미(인증만 되면 들어갈 수 있다.)
                // /manager/**로 매핑되면 인증(access)뿐만 아니라 hasRole('ROLE_ADMIN') , hasRole('ROLE_MANAGER') 권한이 있는 사람만 접근 할 수 있다.
                .antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
                // /admin/**로 매핑되면 인증(access)뿐만 아니라   hasRole('ROLE_ADMIN')권한이 있는 사람만 접근가능하다.
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
                .anyRequest().permitAll() //그리고 위에 권한을 걸어놓은 매핑이 아니라면 모두 허용한다.
                .and() //그리고
                .formLogin() //일반적인 로그인방식을 사용하겠다는 의미(즉 로그인 폼 페이지와 로그인 성공 실패 등을 사용하겠다)
                .loginPage("/loginForm") //로그인이 안되어있는 상태에서 접근없는 페이지로 이동하면 /loginForm으로 보내라
                //             .usernameParameter("username") // PrincipalDetailsService의 loadUserByUsername() 메서드 매개변수 문자열이름과 loginform.html 의 input name 문자열과 맞춰야한다. 그 설정을 여기서 하는것
                // /loginForm 주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인을 해준다. 우리가 controller에 /login을 만들지 않아도 된다.
                .loginProcessingUrl("/login")
                //여기서 defaultSuccessUrl의 추가 설명
                //니가 만약에 그냥 로그인을 하면 / 로 이동시켜주는데 만약 /user url을 가고싶었지만 권한때문에 로그인창으로 되돌아왔다
                //이상태에서 로그인을하면 /가 아닌 /user로 바로 보내줌줌                .defaultSuccessUrl("/") //로그인이 성공되면 /로 이동해라
                .and()
                .oauth2Login() //oauthLogin 설정 시작
                .loginPage("/loginForm") //로그인이 안되어있는 상태에서 접근없는 페이지로 이동하면 /loginForm으로 보내라
                .userInfoEndpoint()  // oauth2Login 성공 이후의 설정 시작
                //구글로그인이 완료된 뒤의 후처리가 필요함(manager/ admin 권한처리)
                //코드받기(인증) -> 엑세스토큰받기(권한) -> 사용자프로필 정보를 가져와서
                //->case 1: 그 정보를 토대로 회원가입을 자동으로 진행시키기도함 / case 2: (이메일,전화번호,이름,아이디) 쇼핑몰 -> (집주소),백화점몰 -> (VIP등급,일반등급)
                //TIP. 구글로그인이 완료되면 코드X(엑세스토큰 + 사용자프로필정보 O)
                .userService(principalOAuth2UserService) //principalOAuth2UserService서 처리하겠다.
                .and().and().build(); // 그리고 빌드해라
    }
}
