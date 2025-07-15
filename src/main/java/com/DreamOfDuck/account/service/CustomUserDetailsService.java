package com.DreamOfDuck.account.service;

import com.DreamOfDuck.account.entity.CustomUserDetails;
import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.repository.MemberRepository;
import com.DreamOfDuck.global.exception.CustomException;
import com.DreamOfDuck.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member principal = memberRepository.findByEmail(email)
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_EXIST));
        return new CustomUserDetails(principal);
    }
}
