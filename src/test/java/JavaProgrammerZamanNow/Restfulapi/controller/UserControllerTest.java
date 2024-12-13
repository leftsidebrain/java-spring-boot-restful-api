package JavaProgrammerZamanNow.Restfulapi.controller;

import JavaProgrammerZamanNow.Restfulapi.entity.User;
import JavaProgrammerZamanNow.Restfulapi.model.RegisterUserRequest;
import JavaProgrammerZamanNow.Restfulapi.model.UpdateUserRequest;
import JavaProgrammerZamanNow.Restfulapi.model.UserResponse;
import JavaProgrammerZamanNow.Restfulapi.model.WebResponse;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	void setUp() {
		userRepository.deleteAll();
	}

	@Test
	void testRegisterSuccess() throws Exception {
		RegisterUserRequest request = new RegisterUserRequest();
		request.setUsername("test");
		request.setName("test");
		request.setPassword("112233");
		mockMvc.perform(
				post("/api/users")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request))
		).andExpectAll(
				status().isOk()

		).andDo(result -> {
			WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
			});
			assertEquals("OK", response.getData());
		});
	}

	@Test
	void testRegisterFailed() throws Exception {
		RegisterUserRequest request = new RegisterUserRequest();
		request.setUsername("");
		request.setName("");
		request.setPassword("");
		mockMvc.perform(
				post("/api/users")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request))
		).andExpectAll(
				status().isBadRequest()

		).andDo(result -> {
			WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
			});
			assertNotNull(response.getErrors());
		});
	}

	@Test
	void testRegisterDuplicate() throws Exception {
		User user = new User();
		user.setUsername("test");
		user.setPassword(BCrypt.hashpw("112233", BCrypt.gensalt()));
		user.setName("Test");
		userRepository.save(user);

		RegisterUserRequest request = new RegisterUserRequest();
		request.setUsername("test");
		request.setName("Test");
		request.setPassword("112233");
		mockMvc.perform(
				post("/api/users")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request))
		).andExpectAll(
				status().isBadRequest()

		).andDo(result -> {
			WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
			});
			assertNotNull(response.getErrors());
		});
	}

	@Test
	void getUserUnauthorized() throws Exception {
		mockMvc.perform(
				get("/api/users/current")
						.accept(MediaType.APPLICATION_JSON)
						.header("X-API-TOKEN", "notfound")
		).andExpectAll(
				status().isUnauthorized()
		).andDo(result -> {
			WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
			});
			assertNotNull(response.getErrors());
		});
	}

	@Test
	void getUserUnauthorizedNotToken() throws Exception {
		mockMvc.perform(
				get("/api/users/current")
						.accept(MediaType.APPLICATION_JSON)

		).andExpectAll(
				status().isUnauthorized()
		).andDo(result -> {
			WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
			});
			assertNotNull(response.getErrors());
		});
	}

	@Test
	void getUserSuccess() throws Exception {
		User user = new User();
		user.setUsername("test");
		user.setPassword(BCrypt.hashpw("112233", BCrypt.gensalt()));
		user.setName("test");
		user.setToken("token");
		user.setTokenExpiredAt(System.currentTimeMillis() + 1000 * 60 * 60);
		userRepository.save(user);
		mockMvc.perform(
				get("/api/users/current")
						.accept(MediaType.APPLICATION_JSON)
						.header("X-API-TOKEN", "token")
		).andExpectAll(
				status().isOk()
		).andDo(result -> {
			WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
			});
			assertNull(response.getErrors());
			assertEquals(user.getName(), response.getData().getName());
			assertEquals(user.getUsername(), response.getData().getUsername());
		});
	}

	@Test
	void getUserExpired() throws Exception {
		User user = new User();
		user.setUsername("test");
		user.setPassword(BCrypt.hashpw("112233", BCrypt.gensalt()));
		user.setName("test");
		user.setToken("token");
		user.setTokenExpiredAt(System.currentTimeMillis() - 1000 * 60 * 60);
		userRepository.save(user);
		mockMvc.perform(
				get("/api/users/current")
						.accept(MediaType.APPLICATION_JSON)
						.header("X-API-TOKEN", "token")
		).andExpectAll(
				status().isUnauthorized()
		).andDo(result -> {
			WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
			});
			assertNotNull(response.getErrors());
		});
	}

	@Test
	void updateUserUnauthorized() throws Exception {
		UpdateUserRequest request = new UpdateUserRequest();
		mockMvc.perform(
				patch("/api/users/current")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request))
		).andExpectAll(
				status().isUnauthorized()
		).andDo(result -> {
			WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
			});
			assertNotNull(response.getErrors());
		});
	}

	@Test
	void updateUserSuccess() throws Exception {
		User user = new User();
		user.setUsername("test");
		user.setPassword(BCrypt.hashpw("112233", BCrypt.gensalt()));
		user.setName("test");
		user.setToken("token");
		user.setTokenExpiredAt(System.currentTimeMillis() + 1000 * 60 * 60);
		userRepository.save(user);

		UpdateUserRequest request = new UpdateUserRequest();
		request.setName("test2");
		request.setPassword("11223344");

		mockMvc.perform(
				patch("/api/users/current")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request))
						.header("X-API-TOKEN", "token")
		).andExpectAll(
				status().isOk()
		).andDo(result -> {
			WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
			});
			assertNull(response.getErrors());
			assertEquals("test2", response.getData().getName());
			assertEquals("test", response.getData().getUsername());
		});


		User userDb = userRepository.findById("test").orElse(null);
		assertNotNull(userDb);
		assertTrue(BCrypt.checkpw("11223344", userDb.getPassword()));


	}
}