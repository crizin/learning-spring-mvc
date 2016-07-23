package net.crizin.learning.controller;

import net.crizin.learning.entity.Member;
import net.crizin.learning.entity.Note;
import net.crizin.learning.entity.Tag;
import net.crizin.learning.exception.UnauthorizedOperation;
import net.crizin.learning.exception.UnknownResourceException;
import net.crizin.learning.service.NoteService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class DefaultController extends AbstractController {
	private final NoteService noteService;

	@Autowired
	public DefaultController(NoteService noteService) {
		this.noteService = noteService;
	}

	@GetMapping("/")
	public String index(Model model, @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
		model.addAttribute("notes", noteService.getAllNotes(pageable));

		return "index";
	}

	@GetMapping("/note")
	public String list(HttpServletRequest request, Model model, @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
		Member currentMember = getCurrentMember(request);

		model.addAttribute("notes", noteService.getNotesByMember(currentMember, pageable));

		return "note/list";
	}

	@GetMapping("/note/write")
	public String write(Model model) {
		model.addAttribute("note", new Note());

		return "note/write";
	}

	@GetMapping("/note/{noteId}/edit")
	public String edit(HttpServletRequest request, Model model, @PathVariable int noteId) {
		Member currentMember = getCurrentMember(request);

		Note note = noteService.getNote(noteId)
				.orElseThrow(() -> new UnknownResourceException("Not found note [id=" + noteId + "]."));

		if (!note.isWriter(currentMember)) {
			throw new UnauthorizedOperation();
		}

		model.addAttribute("note", note);

		return "note/write";
	}

	@GetMapping("/note/{noteId}")
	public String view(HttpServletRequest request, Model model, @PathVariable int noteId) {
		Member currentMember = getCurrentMember(request);

		Note note = noteService.getNote(noteId)
				.orElseThrow(() -> new UnknownResourceException("Not found note [id=" + noteId + "]."));

		model.addAttribute("note", note);
		model.addAttribute("isMyNote", note.isWriter(currentMember));

		return "note/view";
	}

	@GetMapping("/note/{noteId}/attachment/{imagePath}")
	public void viewAttachment(HttpServletRequest request, HttpServletResponse response, @PathVariable int noteId, @PathVariable String imagePath) {
		Member currentMember = getCurrentMember(request);

		if (!noteService.responseAttachment(response, currentMember, noteId, imagePath)) {
			throw new UnknownResourceException("Not found attachment [id=" + noteId + ", imagePath=" + imagePath + "].");
		}
	}

	@PostMapping("/note")
	public String postNote(HttpServletRequest request, RedirectAttributes redirectAttributes,
						   @RequestParam int id, @RequestParam String title, @RequestParam String content,
						   @RequestParam MultipartFile file, @RequestParam(defaultValue = "0") boolean removeFile,
						   @RequestParam(name = "tags[]", required = false) String[] tags) {

		Note note;
		Member currentMember = getCurrentMember(request);

		if (id == 0) {
			note = new Note();
			note.setMember(getCurrentMember(request));
			note.setCreatedAt(LocalDateTime.now());
		} else {
			note = noteService.getNote(id)
					.orElseThrow(() -> new UnknownResourceException("Not found note [id=" + id + "]."));

			if (!note.isWriter(currentMember)) {
				throw new UnauthorizedOperation();
			}

			note.setUpdatedAt(LocalDateTime.now());
		}

		note.setTitle(StringUtils.trimToNull(title));
		note.setContent(StringUtils.trimToNull(content));

		if (tags == null) {
			note.setTags(null);
		} else {
			note.setTags(noteService.convertTags(Arrays.stream(tags).collect(Collectors.toSet())));
		}

		if (removeFile) {
			noteService.deleteFile(note.getImagePath());
			note.setImagePath(null);
		}

		if (!file.isEmpty()) {
			if (note.getImagePath() != null) {
				noteService.deleteFile(note.getImagePath());
			}

			String imagePath = noteService.saveFile(file);

			if (imagePath == null) {
				redirectAttributes.addFlashAttribute("note", note);
				redirectAttributes.addFlashAttribute("error", "Failed to upload file.");
				return "redirect:/note/write";
			}

			note.setImagePath(imagePath);
		}

		noteService.upsertNote(note);

		return "redirect:/note/" + note.getId();
	}

	@ResponseBody
	@DeleteMapping("/note/{noteId}")
	public Map<String, Object> deleteNote(HttpServletRequest request, @PathVariable int noteId) {
		Member currentMember = getCurrentMember(request);

		Optional<Note> noteOptional = noteService.getNote(noteId);

		if (!noteOptional.isPresent()) {
			return respondJsonError("Not found note [id=" + noteId + "].");
		}

		Note note = noteOptional.get();

		if (!note.isWriter(currentMember)) {
			return respondJsonError("Invalid request.");
		}

		noteService.deleteNote(note);

		return respondJsonOk();
	}

	@GetMapping("/tag/{tagName}")
	public String listByTag(Model model, @PathVariable String tagName, @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
		Tag tag = noteService.getTag(tagName)
				.orElseThrow(() -> new UnknownResourceException("Not found tag [name=" + tagName + "]."));

		model.addAttribute("tag", tag);
		model.addAttribute("notes", noteService.getNotesByTag(tag, pageable));

		return "note/list-by-tag";
	}
}