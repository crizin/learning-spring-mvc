package net.crizin.learning.controller;

import net.crizin.learning.AbstractControllerTest;
import net.crizin.learning.entity.Member;
import net.crizin.learning.entity.Note;
import net.crizin.learning.security.AuthenticationUser;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.ui.ModelMap;

import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class DefaultControllerTest extends AbstractControllerTest {
	@Test
	public void testIndex() throws Exception {
		MvcResult mvcResult = mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("notes"))
				.andReturn();

		ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
		@SuppressWarnings("unchecked")
		Page<Note> emptyNotes = (Page<Note>) modelMap.get("notes");
		assertEquals(0, emptyNotes.getTotalElements());

		Note note = noteService.upsertNote(createDummyNote(createDummyMember()));

		mvcResult = mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("notes"))
				.andReturn();

		modelMap = mvcResult.getModelAndView().getModelMap();
		@SuppressWarnings("unchecked")
		Page<Note> oneNote = (Page<Note>) modelMap.get("notes");
		assertEquals(1, oneNote.getTotalElements());
		assertEquals(note.getId(), oneNote.getContent().get(0).getId());
		assertEquals(note.getContent(), oneNote.getContent().get(0).getContent());
	}

	@Test
	public void testNote() throws Exception {
		Member member = memberService.createMember(userName, password);
		AuthenticationUser user = new AuthenticationUser(member);

		MvcResult mvcResult = mockMvc.perform(get("/note")
				.with(user(user)))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("notes"))
				.andReturn();

		ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
		@SuppressWarnings("unchecked")
		Page<Note> emptyNotes = (Page<Note>) modelMap.get("notes");
		assertEquals(0, emptyNotes.getTotalElements());

		Note note = noteService.upsertNote(createDummyNote(member));

		mvcResult = mockMvc.perform(get("/note")
				.with(user(user)))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("notes"))
				.andReturn();

		modelMap = mvcResult.getModelAndView().getModelMap();
		@SuppressWarnings("unchecked")
		Page<Note> oneNote = (Page<Note>) modelMap.get("notes");
		assertEquals(1, oneNote.getTotalElements());
		assertEquals(note.getId(), oneNote.getContent().get(0).getId());
		assertEquals(note.getContent(), oneNote.getContent().get(0).getContent());
	}

	@Test
	public void testWriteNote() throws Exception {
		Member member = memberService.createMember(userName, password);
		AuthenticationUser user = new AuthenticationUser(member);

		MvcResult mvcResult = mockMvc.perform(fileUpload("/note")
				.file(new MockMultipartFile("file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[]{0}))
				.with(csrf())
				.with(user(user))
				.param("id", "0")
				.param("title", "test title")
				.param("content", "test content")
				.param("tags[]", "Tag1")
				.param("tags[]", "Tag2")
				.param("tags[]", "Tag3"))
				.andExpect(status().isFound())
				.andExpect(redirectedUrlPattern("/note/*"))
				.andReturn();

		Note note = noteService.getNote(NumberUtils.toInt(mvcResult.getResponse().getRedirectedUrl().replaceFirst("^/note/", ""))).orElse(null);

		assertNotNull(note);
		assertEquals("test title", note.getTitle());
		assertEquals("test content", note.getContent());
		assertEquals("Tag1, Tag2, Tag3", note.getTagString());
		assertTrue(note.getImagePath().matches("^[a-f\\d]{32}$"));
	}

	@Test
	public void testEditNote() throws Exception {
		Member member = memberService.createMember(userName, password);
		AuthenticationUser user = new AuthenticationUser(member);

		Note note = noteService.upsertNote(createDummyNote(member));

		mockMvc.perform(fileUpload("/note")
				.file(new MockMultipartFile("file", "", MediaType.APPLICATION_OCTET_STREAM_VALUE, new byte[]{}))
				.with(csrf())
				.with(user(user))
				.param("id", String.valueOf(note.getId()))
				.param("title", "modified title")
				.param("content", "modified content")
				.param("tags[]", "TagA")
				.param("tags[]", "TagB")
				.param("tags[]", "TagC")
				.param("removeFile", "1"))
				.andExpect(status().isFound())
				.andExpect(redirectedUrlPattern("/note/*"))
				.andReturn();

		Note modifiedNote = noteService.getNote(note.getId()).orElse(null);

		assertNotNull(note);
		assertEquals("modified title", modifiedNote.getTitle());
		assertEquals("modified content", modifiedNote.getContent());
		assertEquals("TagA, TagB, TagC", modifiedNote.getTagString());
		assertNull(modifiedNote.getImagePath());
	}

	@Test
	public void testDeleteNote() throws Exception {
		Member member = memberService.createMember(userName, password);
		AuthenticationUser user = new AuthenticationUser(member);

		Note note = noteService.upsertNote(createDummyNote(member));

		MvcResult m = mockMvc.perform(delete(String.format("/note/%d", note.getId()))
				.with(csrf())
				.with(user(user)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(jsonPath("$.success").value(true))
				.andReturn();

		assertFalse(noteService.getNote(note.getId()).isPresent());

		mockMvc.perform(get(String.format("/note/%d", note.getId()))
				.with(user(user)))
				.andExpect(status().isNotFound());
	}
}