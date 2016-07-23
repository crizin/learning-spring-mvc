package net.crizin.learning.repository;

import net.crizin.learning.entity.Member;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface MemberRepository extends Repository<Member, Integer> {
	<S extends Member> S saveAndFlush(S entity);

	Optional<Member> findByUserName(String userName);
}