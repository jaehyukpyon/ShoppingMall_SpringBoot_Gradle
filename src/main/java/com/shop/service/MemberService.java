package com.shop.service;

import com.shop.entity.Member;
import com.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@Log
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("********** UserDetailsService's loadUserByUsername starts... **********");

        Member member = memberRepository.findByEmail(email);

        if (member == null) {
            throw new UsernameNotFoundException(email);
        }

        return User.builder()
                    .username(member.getEmail())
                    .password(member.getPassword())
                    .roles(member.getRole().toString())
                .build();
    }

    public Member saveMember(Member member){
        System.out.println("\r\nMemberService's saveMember(Member member) called...");

        validateDuplicateMember(member);
        Member resultMember = memberRepository.save(member);
        System.out.println("\r\nMemberService's saveMember(Member member) ended...");

        return resultMember;
    }

    public void saveMemberForKakaoRegistration(Member member) {
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        memberRepository.save(member);
    }

    private void validateDuplicateMember(Member member) {
        System.out.println("\r\nMemberService's validateDuplicateMember(Member member) called...");

        Member findMember = memberRepository.findByEmail(member.getEmail());

        if (findMember != null) {
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }

        System.out.println("\r\nMemberService's validateDuplicateMember(Member member) ended...");
    }

    public boolean isDuplicateMemberForKakaoRegistration(String kakaoEmail) {
        Member findMember = memberRepository.findByEmail(kakaoEmail);

        return findMember != null ? true : false;
    }

}
