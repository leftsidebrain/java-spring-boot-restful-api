package JavaProgrammerZamanNow.Restfulapi.controller;

import JavaProgrammerZamanNow.Restfulapi.entity.Contact;
import JavaProgrammerZamanNow.Restfulapi.entity.User;
import JavaProgrammerZamanNow.Restfulapi.model.ContactResponse;
import JavaProgrammerZamanNow.Restfulapi.model.CreateContactRequest;
import JavaProgrammerZamanNow.Restfulapi.model.UpdateContactRequest;
import JavaProgrammerZamanNow.Restfulapi.model.WebResponse;
import JavaProgrammerZamanNow.Restfulapi.repository.ContactRepository;
import JavaProgrammerZamanNow.Restfulapi.repository.UserRepository;
import JavaProgrammerZamanNow.Restfulapi.security.BCrypt;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ContactControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		contactRepository.deleteAll();
		userRepository.deleteAll();

		User user = new User();
		user.setName("test");
		user.setUsername("test");
		user.setPassword(BCrypt.hashpw("112233", BCrypt.gensalt()));
		user.setToken("token");
		user.setTokenExpiredAt(System.currentTimeMillis() + 1000 * 60 * 60);
		userRepository.save(user);
	}


	@Test
	void createContactBadRequest() throws Exception {

		CreateContactRequest request = new CreateContactRequest();
		request.setFirstName("");
		request.setEmail("salah");
		mockMvc.perform(
				post("/api/contacts")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request))
						.header("X-API-TOKEN", "token")
		).andExpectAll(
				status().isBadRequest()
		).andDo(result -> {
			WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
			});
			assertNotNull(response.getErrors());
		});
	}

	@Test
	void createContactSuccess() throws Exception {

		CreateContactRequest request = new CreateContactRequest();
		request.setFirstName("test");
		request.setEmail("test@mail.com");
		request.setLastName("ing");
		request.setPhone("08123456789");

		mockMvc.perform(
				post("/api/contacts")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request))
						.header("X-API-TOKEN", "token")
		).andExpectAll(
				status().isOk()
		).andDo(result -> {
			WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
			});
			assertNull(response.getErrors());
			assertEquals("test", response.getData().getFirstName());
			assertEquals("test@mail.com", response.getData().getEmail());
			assertEquals("ing", response.getData().getLastName());
			assertEquals("08123456789", response.getData().getPhone());

			assertTrue(contactRepository.existsById(response.getData().getId()));
		});
	}

	@Test
	void getContactNotFound() throws Exception {
		String id = "124435";
		mockMvc.perform(
				get("/api/contacts/" + id)
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.header("X-API-TOKEN", "token")
		).andExpectAll(
				status().isNotFound()
		).andDo(result -> {
			WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
			});
			assertNotNull(response.getErrors());
		});
	}

	@Test
	void getContactSuccess() throws Exception {
		User user = userRepository.findById("test").orElseThrow();

		Contact contact = new Contact();
		contact.setId(UUID.randomUUID().toString());
		contact.setFirstName("test");
		contact.setEmail("test@mail.com");
		contact.setLastName("ing");
		contact.setPhone("08123456789");
		contact.setUser(user);
		contactRepository.save(contact);

		mockMvc.perform(
				get("/api/contacts/" + contact.getId())
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)

						.header("X-API-TOKEN", "token")
		).andExpectAll(
				status().isOk()
		).andDo(result -> {
			WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
			});
			assertNull(response.getErrors());
			assertEquals(contact.getFirstName(), response.getData().getFirstName());
			assertEquals(contact.getId(), response.getData().getId());
			assertEquals(contact.getEmail(), response.getData().getEmail());
			assertEquals(contact.getLastName(), response.getData().getLastName());
			assertEquals(contact.getPhone(), response.getData().getPhone());

			assertTrue(contactRepository.existsById(response.getData().getId()));
		});
	}

	@Test
	void updateContactBadRequest() throws Exception {

		UpdateContactRequest request = new UpdateContactRequest();
		request.setFirstName("");
		request.setEmail("salah");
		mockMvc.perform(
				put("/api/contacts")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request))
						.header("X-API-TOKEN", "token")
		).andExpectAll(
				status().isBadRequest()
		).andDo(result -> {
			WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
			});
			assertNotNull(response.getErrors());
		});
	}

	@Test
	void updateContactSuccess() throws Exception {
		User user = userRepository.findById("test").orElseThrow();

		Contact contact = new Contact();
		contact.setId(UUID.randomUUID().toString());
		contact.setFirstName("test");
		contact.setLastName("ing");
		contact.setEmail("test@mail.com");
		contact.setPhone("08123456789");
		contact.setUser(user);
		contactRepository.save(contact);

		CreateContactRequest request = new CreateContactRequest();
		request.setFirstName("testEdit");
		request.setEmail("testEdit@mail.com");
		request.setLastName("ingEdit");
		request.setPhone("0812345678910");

		mockMvc.perform(
				put("/api/contacts/" + contact.getId())
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request))
						.header("X-API-TOKEN", "token")
		).andExpectAll(
				status().isOk()
		).andDo(result -> {
			WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
			});
			assertNull(response.getErrors());
			assertEquals(request.getFirstName(), response.getData().getFirstName());
			assertEquals(request.getEmail(), response.getData().getEmail());
			assertEquals(request.getLastName(), response.getData().getLastName());
			assertEquals(request.getPhone(), response.getData().getPhone());

			assertTrue(contactRepository.existsById(response.getData().getId()));
		});
	}

	@Test
	void deleteContactNotFound() throws Exception {
		String id = "124435";
		mockMvc.perform(
				delete("/api/contacts/" + id)
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.header("X-API-TOKEN", "token")
		).andExpectAll(
				status().isNotFound()
		).andDo(result -> {
			WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
			});
			assertNotNull(response.getErrors());
		});
	}

	@Test
	void deleteContactSuccess() throws Exception {
		User user = userRepository.findById("test").orElseThrow();

		Contact contact = new Contact();
		contact.setId(UUID.randomUUID().toString());
		contact.setFirstName("test");
		contact.setEmail("test@mail.com");
		contact.setLastName("ing");
		contact.setPhone("08123456789");
		contact.setUser(user);
		contactRepository.save(contact);

		mockMvc.perform(
				delete("/api/contacts/" + contact.getId())
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.header("X-API-TOKEN", "token")
		).andExpectAll(
				status().isOk()
		).andDo(result -> {
			WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
			});
			assertNull(response.getErrors());
			assertEquals("OK", response.getData());
		});
	}

	@Test
	void searchContactNotFound() throws Exception {

		mockMvc.perform(
				get("/api/contacts")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.header("X-API-TOKEN", "token")
		).andExpectAll(
				status().isOk()
		).andDo(result -> {
			WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
			});
			assertNull(response.getErrors());
			assertEquals(0, response.getData().size());
			assertEquals(0, response.getPaging().getTotalPage());
			assertEquals(0, response.getPaging().getCurrentPage());
			assertEquals(10, response.getPaging().getSize());
		});
	}

	@Test
	void searchSuccess() throws Exception {
		User user = userRepository.findById("test").orElseThrow();

		for (int i = 0; i < 100; i++) {
			Contact contact = new Contact();
			contact.setId(UUID.randomUUID().toString());
			contact.setFirstName("ruddi " + i);
			contact.setEmail("ruddi@mail.com");
			contact.setLastName("khai");
			contact.setPhone("08123456789");
			contact.setUser(user);
			contactRepository.save(contact);
		}

		mockMvc.perform(
				get("/api/contacts")
						.queryParam("name", "ruddi")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.header("X-API-TOKEN", "token")
		).andExpectAll(
				status().isOk()
		).andDo(result -> {
			WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
			});
			assertNull(response.getErrors());
			assertEquals(10, response.getData().size());
			assertEquals(10, response.getPaging().getTotalPage());
			assertEquals(0, response.getPaging().getCurrentPage());
			assertEquals(10, response.getPaging().getSize());
		});

		mockMvc.perform(
				get("/api/contacts")
						.queryParam("name", "khai")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.header("X-API-TOKEN", "token")
		).andExpectAll(
				status().isOk()
		).andDo(result -> {
			WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
			});
			assertNull(response.getErrors());
			assertEquals(10, response.getData().size());
			assertEquals(10, response.getPaging().getTotalPage());
			assertEquals(0, response.getPaging().getCurrentPage());
			assertEquals(10, response.getPaging().getSize());
		});

		mockMvc.perform(
				get("/api/contacts")
						.queryParam("email", "@mail.com")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.header("X-API-TOKEN", "token")
		).andExpectAll(
				status().isOk()
		).andDo(result -> {
			WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
			});
			assertNull(response.getErrors());
			assertEquals(10, response.getData().size());
			assertEquals(10, response.getPaging().getTotalPage());
			assertEquals(0, response.getPaging().getCurrentPage());
			assertEquals(10, response.getPaging().getSize());
		});

		mockMvc.perform(
				get("/api/contacts")
						.queryParam("phone", "081")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.header("X-API-TOKEN", "token")
		).andExpectAll(
				status().isOk()
		).andDo(result -> {
			WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
			});
			assertNull(response.getErrors());
			assertEquals(10, response.getData().size());
			assertEquals(10, response.getPaging().getTotalPage());
			assertEquals(0, response.getPaging().getCurrentPage());
			assertEquals(10, response.getPaging().getSize());
		});


		mockMvc.perform(
				get("/api/contacts")
						.queryParam("phone", "081")
						.queryParam("page", "1000")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.header("X-API-TOKEN", "token")
		).andExpectAll(
				status().isOk()
		).andDo(result -> {
			WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
			});
			assertNull(response.getErrors());
			assertEquals(0, response.getData().size());
			assertEquals(10, response.getPaging().getTotalPage());
			assertEquals(1000, response.getPaging().getCurrentPage());
			assertEquals(10, response.getPaging().getSize());
		});
	}

}