package JavaProgrammerZamanNow.Restfulapi.controller;

import JavaProgrammerZamanNow.Restfulapi.entity.Address;
import JavaProgrammerZamanNow.Restfulapi.entity.Contact;
import JavaProgrammerZamanNow.Restfulapi.entity.User;
import JavaProgrammerZamanNow.Restfulapi.model.AddressResponse;
import JavaProgrammerZamanNow.Restfulapi.model.CreateAddressRequest;
import JavaProgrammerZamanNow.Restfulapi.model.UpdateAddressRequest;
import JavaProgrammerZamanNow.Restfulapi.model.WebResponse;
import JavaProgrammerZamanNow.Restfulapi.repository.AddressRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AddressControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private AddressRepository addressRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

	@BeforeEach
	void setUp() {
		addressRepository.deleteAll();
		contactRepository.deleteAll();
		userRepository.deleteAll();

		User user = new User();
		user.setName("test");
		user.setUsername("test");
		user.setPassword(BCrypt.hashpw("112233", BCrypt.gensalt()));
		user.setToken("token");
		user.setTokenExpiredAt(System.currentTimeMillis() + 1000 * 60 * 60);
		userRepository.save(user);

		Contact contact = new Contact();
		contact.setId("idTest");
		contact.setFirstName("ruddi");
		contact.setEmail("ruddi@mail.com");
		contact.setLastName("khai");
		contact.setPhone("08123456789");
		contact.setUser(user);
		contactRepository.save(contact);

	}


	@Test
	void createAddressBadRequest() throws Exception {
		CreateAddressRequest request = new CreateAddressRequest();
		request.setCountry("");

		mockMvc.perform(
				post("/api/contacts/idTest/addresses")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.header("X-API-TOKEN", "token")
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
	void createAddressSuccess() throws Exception {
		CreateAddressRequest request = new CreateAddressRequest();
		request.setCountry("indonesia");
		request.setCity("jakarta");
		request.setStreet("jalan raya");
		request.setPostalCode("62342");
		request.setProvince("jakarta");


		mockMvc.perform(
				post("/api/contacts/idTest/addresses")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.header("X-API-TOKEN", "token")
						.content(objectMapper.writeValueAsString(request))

		).andExpectAll(
				status().isOk()
		).andDo(result -> {
			WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
			});
			assertNull(response.getErrors());
			assertEquals(request.getCountry(), response.getData().getCountry());
			assertEquals(request.getPostalCode(), response.getData().getPostalCode());
			assertEquals(request.getCity(), response.getData().getCity());
			assertEquals(request.getProvince(), response.getData().getProvince());
			assertEquals(request.getStreet(), response.getData().getStreet());

			assertTrue(addressRepository.existsById(response.getData().getId()));

		});
	}

	@Test
	void getAddressNotFound() throws Exception {
		mockMvc.perform(
				get("/api/contacts/idTest/addresses/idTest")
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
	void getAddressSuccess() throws Exception {
		Contact contact = contactRepository.findById("idTest").orElseThrow();

		Address address = new Address();
		address.setId("idTest");
		address.setCountry("indonesia");
		address.setCity("jakarta");
		address.setStreet("jalan raya");
		address.setPostalCode("62342");
		address.setProvince("jakarta");
		address.setContact(contact);
		addressRepository.save(address);

		mockMvc.perform(
				get("/api/contacts/idTest/addresses/idTest")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.header("X-API-TOKEN", "token")


		).andExpectAll(
				status().isOk()
		).andDo(result -> {
			WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
			});
			assertNull(response.getErrors());
			assertEquals(address.getId(), response.getData().getId());
			assertEquals(address.getCountry(), response.getData().getCountry());
			assertEquals(address.getPostalCode(), response.getData().getPostalCode());
			assertEquals(address.getCity(), response.getData().getCity());
			assertEquals(address.getProvince(), response.getData().getProvince());
			assertEquals(address.getStreet(), response.getData().getStreet());


			assertTrue(addressRepository.existsById(response.getData().getId()));


		});
	}

	@Test
	void updateAddressBadRequest() throws Exception {
		UpdateAddressRequest request = new UpdateAddressRequest();
		request.setCountry("");

		mockMvc.perform(
				put("/api/contacts/idTest/addresses/idTest")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.header("X-API-TOKEN", "token")
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
	void updateAddressSuccess() throws Exception {
		Contact contact = contactRepository.findById("idTest").orElseThrow();

		Address address = new Address();
		address.setId("idTest");
		address.setCountry("indonesia");
		address.setCity("jakarta");
		address.setStreet("jalan raya");
		address.setPostalCode("62342");
		address.setProvince("jakarta");
		address.setContact(contact);
		addressRepository.save(address);

		CreateAddressRequest request = new CreateAddressRequest();
		request.setCountry("italy");
		request.setCity("rome");
		request.setStreet("rome street");
		request.setPostalCode("62342");
		request.setProvince("rome province");


		mockMvc.perform(
				put("/api/contacts/idTest/addresses/idTest")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.header("X-API-TOKEN", "token")
						.content(objectMapper.writeValueAsString(request))

		).andExpectAll(
				status().isOk()
		).andDo(result -> {
			WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
			});
			assertNull(response.getErrors());
			assertEquals(request.getCountry(), response.getData().getCountry());
			assertEquals(request.getPostalCode(), response.getData().getPostalCode());
			assertEquals(request.getCity(), response.getData().getCity());
			assertEquals(request.getProvince(), response.getData().getProvince());
			assertEquals(request.getStreet(), response.getData().getStreet());

			assertTrue(addressRepository.existsById(response.getData().getId()));

		});
	}

	@Test
	void deleteAddressNotFound() throws Exception {
		mockMvc.perform(
				delete("/api/contacts/idTest/addresses/idTest1")
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
	void deleteAddressSuccess() throws Exception {
		Contact contact = contactRepository.findById("idTest").orElseThrow();

		Address address = new Address();
		address.setId("idTest");
		address.setCountry("indonesia");
		address.setCity("jakarta");
		address.setStreet("jalan raya");
		address.setPostalCode("62342");
		address.setProvince("jakarta");
		address.setContact(contact);
		addressRepository.save(address);

		mockMvc.perform(
				delete("/api/contacts/idTest/addresses/idTest")
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
	void listAddressNotFound() throws Exception {
		mockMvc.perform(
				get("/api/contacts/idSalah/addresses")
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
	void listAddressSuccess() throws Exception {
		Contact contact = contactRepository.findById("idTest").orElseThrow();

		for (int i = 0; i < 10; i++) {


			Address address = new Address();
			address.setId("idTest" + i);
			address.setCountry("indonesia");
			address.setCity("jakarta");
			address.setStreet("jalan raya");
			address.setPostalCode("62342");
			address.setProvince("jakarta");
			address.setContact(contact);
			addressRepository.save(address);
		}

		mockMvc.perform(
				get("/api/contacts/idTest/addresses")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.header("X-API-TOKEN", "token")


		).andExpectAll(
				status().isOk()
		).andDo(result -> {
			WebResponse<List<AddressResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
			});
			assertNull(response.getErrors());

			assertEquals(10, response.getData().size());


		});
	}


}