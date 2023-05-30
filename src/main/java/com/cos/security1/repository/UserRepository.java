package com.cos.security1.repository;

import com.cos.security1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

//CRUD 함수를 JapRepository가 들고 있음.
//@Repository라는 어노테이션이 없어도 IOC되요. 이유는 JpaRepository를 상속했기 때문에 빈으로 자동으로 등록이 된다.
public interface UserRepository extends JpaRepository<User,Integer> {
    //findBy까지는 규칙 -> Username문법
    //select * from user where username = 1? -> 1?에 username가 박힌다.
    public User findByUsername(String username);

//   예시)select * from user where email => ?
//    public User findByEmail(); // 더 궁금하면 JPA Query methods 찾아봐라
}
