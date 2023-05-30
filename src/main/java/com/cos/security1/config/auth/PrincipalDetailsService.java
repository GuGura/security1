package com.cos.security1.config.auth;

import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// 시큐리티 설정에서 loginProcessingUrl("/login")
// /login 요청이 오면 자동으로 UserDetailsService 타입으로 IoC되어 있는 loadUserByUsername 함수가 실행 -> 규칙이다.
// 여기서 ★★★존나게 중요한거★★★
// loadUserByUsername의 매개변수인 username 문자열과 loginform.html의 input의 name=username이 같아야 한다.
// 그리고 지금 loadUserByUsername의 매개변수에 username 문자열이 그냥 막 정할 수 있는게 아니라
// SecurityConfig보면 usernameParameter()메서드에 이름을 설정할 수 있는게 이거 기본값이 username이다.
// 다르게 설정하고 싶으면 이걸 바꾸고 나머지도 바꿔야한다.

// login 주석에서 이어서 한다.
@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    //아까전 다시 복습
    // Security Session -> Authentication -> UserDetails
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userEntity = userRepository.findByUsername(username);
        System.out.println(userEntity);
        if (username != null){
            return new PrincipalDetails(userEntity);
            //여기서 리턴되면
            // Security Session -> Authentication(내부 UserDetails) 이렇게된다
            //최종적으론 Security Session(내부 Authentication(내부 UserDetails)) 이렇게된다.
        }
        return null;
    }
}
