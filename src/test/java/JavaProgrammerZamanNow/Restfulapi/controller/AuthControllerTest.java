package JavaProgrammerZamanNow.Restfulapi.controller;

import JavaProgrammerZamanNow.Restfulapi.entity.User;
import JavaProgrammerZamanNow.Restfulapi.model.LoginUserRequest;
import JavaProgrammerZamanNow.Restfulapi.model.TokenResponse;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ObjectMapper objectMapper;


	@BeforeEach
	void setUp() {
		userRepository.deleteAll();
	}

	@Test
	void testLoginUserNotFound() throws Exception {

		LoginUserRequest request = new LoginUserRequest();
		request.setUsername("test");
		request.setPassword("112233");

		mockMvc.perform(
				post("/api/auth/login")
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
	void testLoginFailedWrongPassword() throws Exception {
		User user = new User();
		user.setName("test");
		user.setUsername("test");
		user.setPassword(BCrypt.hashpw("112233", BCrypt.gensalt()));
		userRepository.save(user);

		LoginUserRequest request = new LoginUserRequest();
		request.setUsername("test");
		request.setPassword("112234");

		mockMvc.perform(
				post("/api/auth/login")
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
	void testLoginSuccess() throws Exception {
		User user = new User();
		user.setName("test");
		user.setUsername("test");
		user.setPassword(BCrypt.hashpw("112233", BCrypt.gensalt()));
		userRepository.save(user);

		LoginUserRequest request = new LoginUserRequest();
		request.setUsername("test");
		request.setPassword("112233");

		mockMvc.perform(
				post("/api/auth/login")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request))
		).andExpectAll(
				status().isOk()
		).andDo(result -> {
			WebResponse<TokenResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {

			});
			assertNull(response.getErrors());
			assertNotNull(response.getData().getToken());
			assertNotNull(response.getData().getExpiredAt());


			User userDB = userRepository.findById("test").orElse(null);
			assertNotNull(userDB);
			assertEquals(userDB.getToken(), response.getData().getToken());
			assertEquals(userDB.getTokenExpiredAt(), response.getData().getExpiredAt());
		});

	}

	@Test
	void testLogoutFailed() throws Exception {
		mockMvc.perform(
				delete("/api/auth/logout")
						.accept(MediaType.APPLICATION_JSON)

		).andExpectAll(
				status().isUnauthorized()
		).andDo(result -> {
			WebResponse<String> res = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {

			});
			assertNotNull(res.getErrors());
		});
	}

	@Test
	void testLogoutSuccess() throws Exception {
		User user = new User();
		user.setName("test");
		user.setUsername("test");
		user.setToken("token");
		user.setTokenExpiredAt(System.currentTimeMillis() + 1000 * 60 * 60);
		user.setPassword(BCrypt.hashpw("112233", BCrypt.gensalt()));
		userRepository.save(user);
		mockMvc.perform(
				delete("/api/auth/logout")
						.accept(MediaType.APPLICATION_JSON)
						.header("X-API-TOKEN", "token")


		).andExpectAll(
				status().isOk()
		).andDo(result -> {
			WebResponse<String> res = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {

			});
			assertNull(res.getErrors());
			assertEquals("OK", res.getData());
		});

		User userDb = userRepository.findById("test").orElse(null);
		assertNotNull(userDb);
		assertNull(userDb.getToken());
		assertNull(userDb.getTokenExpiredAt());
	}
}