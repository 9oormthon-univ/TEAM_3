package com.allergenie.server.service;

import com.allergenie.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuthUserService {

    private final UserRepository userRepository;

    //    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        com.allergenie.server.domain.User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("회원이 존재하지 않습니다."));
        System.out.println(user.getEmail()+" loadUserByUsername");
        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .build();
    }
}
