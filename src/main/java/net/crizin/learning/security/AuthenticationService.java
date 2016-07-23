package net.crizin.learning.security;

import net.crizin.learning.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class AuthenticationService implements UserDetailsService {
	private final MemberRepository memberRepository;

	public AuthenticationService(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		return memberRepository.findByUserName(userName)
				.map(AuthenticationUser::new)
				.orElseThrow(() -> new UsernameNotFoundException(userName));
	}
}