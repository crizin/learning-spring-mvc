package net.crizin.learning.repository;

import net.crizin.learning.entity.Tag;
import org.springframework.data.repository.Repository;

import java.util.Optional;
import java.util.Set;

public interface TagRepository extends Repository<Tag, Integer> {
	<S extends Tag> Iterable<S> save(Iterable<S> entities);

	Optional<Tag> findByName(String name);

	Set<Tag> findByNameIn(Iterable<String> names);
}