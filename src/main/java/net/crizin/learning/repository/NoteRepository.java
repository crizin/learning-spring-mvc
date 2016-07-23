package net.crizin.learning.repository;

import net.crizin.learning.entity.Member;
import net.crizin.learning.entity.Note;
import net.crizin.learning.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface NoteRepository extends Repository<Note, Integer> {
	<S extends Note> S save(S entity);

	void delete(Note entity);

	Optional<Note> findOne(Integer id);

	Page<Note> findAll(Pageable pageable);

	Page<Note> findByMember(Member member, Pageable pageable);

	Page<Note> findByTags(Tag tag, Pageable pageable);
}