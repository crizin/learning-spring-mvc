package net.crizin.learning;

import net.crizin.learning.config.MvcConfig;
import net.crizin.learning.config.RootConfig;
import net.crizin.learning.entity.Member;
import net.crizin.learning.entity.Note;
import net.crizin.learning.service.MemberService;
import net.crizin.learning.service.NoteService;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootConfig.class, MvcConfig.class})
public class AbstractControllerTest {
	protected final String userName = "testUserName";
	protected final String password = "testPassword";

	@Autowired
	protected WebApplicationContext webApplicationContext;
	@Autowired
	protected Filter springSecurityFilterChain;
	@Autowired
	protected MemberService memberService;
	@Autowired
	protected NoteService noteService;

	protected MockMvc mockMvc;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders
				.webAppContextSetup(webApplicationContext)
				.addFilter(springSecurityFilterChain)
				.build();
	}

	protected Member createDummyMember() {
		return memberService.createMember(UUID.randomUUID().toString(), UUID.randomUUID().toString());
	}

	protected Note createDummyNote(Member member) {
		Note note = new Note();
		note.setMember(member);
		note.setTitle("Dummy title");
		note.setContent("Dummy content");
		note.setTags(noteService.convertTags(Stream.of("Tag1", "Tag2", "Tag3").collect(Collectors.toSet())));
		note.setImagePath(DigestUtils.md5DigestAsHex(UUID.randomUUID().toString().getBytes()));
		note.setCreatedAt(LocalDateTime.now());
		return note;
	}
}