package net.crizin.learning.service;

import net.crizin.learning.entity.Member;
import net.crizin.learning.entity.Note;
import net.crizin.learning.entity.Tag;
import net.crizin.learning.repository.NoteRepository;
import net.crizin.learning.repository.TagRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NoteService {
	private static final Logger logger = LoggerFactory.getLogger(NoteService.class);

	private static final int MAX_TAG_COUNT = 10;

	@Value("#{config['attachment.path']}")
	private String attachmentPath;
	private final NoteRepository noteRepository;
	private final TagRepository tagRepository;

	@Autowired
	public NoteService(NoteRepository noteRepository, TagRepository tagRepository) {
		this.noteRepository = noteRepository;
		this.tagRepository = tagRepository;
	}

	@Transactional
	public Note upsertNote(Note note) {
		return noteRepository.save(note);
	}

	@Transactional
	public Optional<Note> getNote(int noteId) {
		return noteRepository.findOne(noteId);
	}

	@Transactional
	public Optional<Tag> getTag(String tagName) {
		return tagRepository.findByName(tagName);
	}

	@Transactional
	public Page<Note> getAllNotes(Pageable pageable) {
		return noteRepository.findAll(pageable);
	}

	@Transactional
	public Page<Note> getNotesByMember(Member member, Pageable pageable) {
		return noteRepository.findByMember(member, pageable);
	}

	@Transactional
	public Page<Note> getNotesByTag(Tag tag, Pageable pageable) {
		return noteRepository.findByTags(tag, pageable);
	}

	@Transactional
	public void deleteNote(Note note) {
		noteRepository.delete(note);
	}

	@Transactional
	public Set<Tag> convertTags(Set<String> tags) {
		Set<String> filteredTags = tags.stream()
				.map(StringUtils::trimToNull)
				.filter(Objects::nonNull)
				.sorted()
				.limit(MAX_TAG_COUNT)
				.collect(Collectors.toSet());

		Set<Tag> existingTags = tagRepository.findByNameIn(filteredTags);

		Set<String> existingTagNames = existingTags
				.stream()
				.map(Tag::getName)
				.collect(Collectors.toSet());

		Set<Tag> newTags = filteredTags.stream()
				.filter(tag -> !existingTagNames.contains(tag))
				.map(Tag::new)
				.collect(Collectors.toCollection(TreeSet::new));

		tagRepository.save(newTags);

		newTags.addAll(existingTags);

		return newTags;
	}

	public String saveFile(MultipartFile file) {
		String fileName = DigestUtils.md5DigestAsHex(UUID.randomUUID().toString().getBytes());

		try {
			Files.copy(file.getInputStream(), Paths.get(attachmentPath, fileName));
			return fileName;
		} catch (IOException e) {
			return null;
		}
	}

	public void deleteFile(String imagePath) {
		try {
			Files.delete(Paths.get(attachmentPath, imagePath));
		} catch (IOException e) {
			logger.error("Can't delete attachment file [imagePath=" + imagePath + "]", e);
		}
	}

	public Optional<StreamingResponseBody> responseAttachment(int noteId, String imagePath) {
		if (StringUtils.isBlank(imagePath)) {
			return Optional.empty();
		}

		Optional<Note> noteOptional = getNote(noteId);

		if (!noteOptional.isPresent()) {
			return Optional.empty();
		}

		if (!imagePath.equals(noteOptional.get().getImagePath())) {
			return Optional.empty();
		}

		return Optional.of(outputStream -> Files.copy(Paths.get(attachmentPath, imagePath), outputStream));
	}
}