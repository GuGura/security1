package com.cos.security1.config.auth;

import com.cos.security1.model.User;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

// 시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행시킨다.
// 로그인을 진행이 완료가 되면 Security session 을 만들어줍니다.(Security ContextHolder <- 이 키값에 세션 정보를 저장한다.)
// 시큐리티가 가지고 있는 session에 들어갈 수 있는 오브젝트는 정해져있다.
// 그 정해진 오브젝트 -> Authentication 타입의 객체
// Authentication 안에 User정보가 있어야 됨.
// User오브젝트타입 -> UserDetails 타입 객체

// Security Session에는 -> Authentication 객체만 들어갈 수 있다.
// Authentication 객체에 유저정보를 저장할 때 => UserDetails 타입이여야만 들어갈 수 있다.
// Security Session -> Authentication -> UserDetails(PrincipalDetails)
// 자 여기서 UserDetails를 만들었으니 Authentication도 만들어야한다. 그리고 지금메모리에 안올리는 이유는 나중에 강제로 띄울거기 때문
@Data
public class PrincipalDetails implements UserDetails, OAuth2User {

    private User user; //컴포지션
    private Map<String,Object> attributes;

    //일반로그인
    public PrincipalDetails(User user){
        this.user = user;
    }
    //OAuth 로그인
    public PrincipalDetails(User user,Map<String,Object> attributes){
        this.user = user;
        this.attributes = attributes;
    }


    /** 해당 User의 권한을 리턴하는 곳!!
     *  1. Collection<? extends GrantedAuthority> 타입의 객체를 만든다.
     *  ArrayList에 값을 추가할려면 당연히 GrantedAuthority타입이여야하고
     *  2.GrantedAuthority타입을 넣으면 인터페이스를 구현하라고 당연히 나온다.
     *  그럼 구현을 하기 위해 오버라이드를 해보자
     *  3. 뭔가 하나 메서드가 나왔는데 리턴타입이 String이다.
     *  여기에 User객체에 Role이 String타입으로 들어가 있다.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });
        return collect;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    //이 계정 만료됬냐?
    @Override
    public boolean isAccountNonExpired() {
        return true; // 아니
    }

    // 이 계정 잠겼니?
    @Override
    public boolean isAccountNonLocked() {
        return true; //아니
    }

    // 이 계정의 비밀번호 너무 오래사용한거 아니냐?
    @Override
    public boolean isCredentialsNonExpired() {
        return true; //아니
    }

    // 이 계정이 활성화 되어있니?
    @Override
    public boolean isEnabled() {

        // 우리 사이트!! 1년동안 회원이 로그인을 안하면!! 휴먼계정으로 하기로 함
        // 현재시간 - 로그인시간 => 1년을 초과하면 리턴을 false 로
        //user.getLoginDate();
        return true; //예스
    }
    @Override
    public <A> A getAttribute(String name) {
        return null;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
    @Override
    public String getName() {
        return null;//(String)attributes.get("sub");
    }
}
