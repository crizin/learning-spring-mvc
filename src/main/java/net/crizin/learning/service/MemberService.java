package net.crizin.learning.service;

import net.crizin.learning.entity.Member;
import net.crizin.learning.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class MemberService {
	private final PasswordEncoder passwordEncoder;
	private final MemberRepository memberRepository;

	@Autowired
	public MemberService(PasswordEncoder passwordEncoder, MemberRepository memberRepository) {
		this.passwordEncoder = passwordEncoder;
		this.memberRepository = memberRepository;
	}

	@Transactional
	public Member createMember(String userName, String password) {
		Member member = new Member();
		member.setUserName(userName);
		member.setPassword(passwordEncoder.encode(password));
		member.setCreatedAt(LocalDateTime.now());

		return memberRepository.saveAndFlush(member);
	}
}