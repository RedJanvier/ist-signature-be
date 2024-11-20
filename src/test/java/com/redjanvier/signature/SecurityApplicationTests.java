package com.redjanvier.signature;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redjanvier.signature.config.EmailTestConfig;
import com.redjanvier.signature.dtos.AuthenticationRequest;
import com.redjanvier.signature.dtos.AuthenticationResponse;
import com.redjanvier.signature.dtos.CompanyDto;
import com.redjanvier.signature.dtos.RegisterRequest;
import com.redjanvier.signature.models.Role;
import com.redjanvier.signature.models.User;
import com.redjanvier.signature.repositories.CompanyRepository;
import com.redjanvier.signature.repositories.TokenRepository;
import com.redjanvier.signature.repositories.UserRepository;
import com.redjanvier.signature.services.AuthenticationService;
import com.redjanvier.signature.services.LogoutService;
import com.redjanvier.signature.services.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
@Import(EmailTestConfig.class)
class SecurityApplicationTests {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private AuthenticationService authService;
	@Autowired
	private LogoutService logoutService;
	@Autowired
	private TokenRepository tokenRepository;
	@Autowired
	private CompanyRepository companyRepository;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	private static List<RegisterRequest> employees = new ArrayList<>();
	private static List<User> users = new ArrayList<>();

	@Container
	private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16")
		.withDatabaseName("integration-tests-db")
		.withUsername("username")
		.withPassword("password")
		.withInitScript("test-data.sql");

	static {
		postgreSQLContainer.start();
	}

	static {
		RegisterRequest emp1 = RegisterRequest.builder().firstname("test emp1").password("test emp1").lastname("test emp1").email("address1@gmail.com").build();
		RegisterRequest emp2 = RegisterRequest.builder().firstname("test emp2").password("test emp2").lastname("test emp2").email("address2@gmail.com").build();
		RegisterRequest emp3 = RegisterRequest.builder().firstname("test emp3").password("test emp3").lastname("test emp3").email("address3@gmail.com").build();
		RegisterRequest emp4 = RegisterRequest.builder().firstname("test emp4").password("test emp4").lastname("test emp4").email("address4@gmail.com").build();
		RegisterRequest emp5 = RegisterRequest.builder().firstname("test emp5").password("test emp5").lastname("test emp5").email("address5@gmail.com").build();

		employees.add(emp1);
		employees.add(emp2);
		employees.add(emp3);
		employees.add(emp4);
		employees.add(emp5);

		User admin = User.builder()
			.firstname("admin")
			.lastname("admin")
			.email("admin@ist.com")
			.password("password")
			.role(Role.ADMIN)
			.enabled(true)
			.build();
		User user = User.builder()
			.firstname("user")
			.lastname("user")
			.email("user@ist.com")
			.password("password")
			.role(Role.USER)
			.enabled(true)
			.build();

		users.add(admin);
		users.add(user);
	}

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
		dynamicPropertyRegistry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
		dynamicPropertyRegistry.add("spring.datasource.username", postgreSQLContainer::getUsername);
		dynamicPropertyRegistry.add("spring.datasource.password", postgreSQLContainer::getPassword);
	}

	@AfterEach
	public void tearDown() {
			jdbcTemplate.execute("DELETE FROM token");  
	}

	@Test
	@Order(value = 1)
	void testConnectionToDatabase() {
		Assertions.assertNotNull(userRepository);
		Assertions.assertNotNull(tokenRepository);
		Assertions.assertNotNull(companyRepository);
	}

	@Test
	@Order(value = 2)
	void testLoginFail_onNoUser() throws Exception {
			AuthenticationRequest body = AuthenticationRequest.builder()
				.email(employees.get(0).getEmail())
				.password(employees.get(0).getPassword())
				.build();
			String emp = objectMapper.writeValueAsString(body);
			mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/authenticate").contentType(MediaType.APPLICATION_JSON)
					.content(emp))
					.andExpect(status().isBadRequest());
	}

	@Test
	@Order(value = 3)
	void testLoginFail_onWrongPassword() throws Exception {
			RegisterRequest empl = employees.get(0);
			authService.registerAdmin(empl);
			AuthenticationRequest body = AuthenticationRequest.builder()
				.email(empl.getEmail())
				.password("Incorrect Password")
				.build();
			String emp = objectMapper.writeValueAsString(body);
			mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/authenticate").contentType(MediaType.APPLICATION_JSON)
					.content(emp))
					.andExpect(status().isBadRequest());
	}

	@Test
	@Order(value = 4)
	void testLoginSuccess() throws Exception {
			RegisterRequest empl = employees.get(1);
			authService.registerAdmin(empl);
			AuthenticationRequest body = AuthenticationRequest.builder()
				.email(empl.getEmail())
				.password(empl.getPassword())
				.build();
			String json = objectMapper.writeValueAsString(body);
			mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/authenticate").contentType(MediaType.APPLICATION_JSON)
					.content(json))
					.andExpect(status().isOk());
	}

	@Test
	@Order(value = 5)
	void testCompany_read() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/company/get").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}

	@Test
	@Order(value = 6)
	void testCompany_update() throws Exception {
			CompanyDto body = CompanyDto.builder().name("IST Test").build();
			String jsonBody = objectMapper.writeValueAsString(body);
			AuthenticationResponse authData = authService.authenticate(AuthenticationRequest.builder().email("admin@ist.com").password("Jannyda1").build());
			mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/company").header("Authorization", "Bearer " + authData.getAccessToken()).contentType(MediaType.APPLICATION_JSON).content(jsonBody))
			.andExpect(status().isOk());
	}

	@Test
	@Order(value = 7)
	void testUsers_read() throws Exception {
			AuthenticationResponse authData = authService.authenticate(AuthenticationRequest.builder().email("admin@ist.com").password("Jannyda1").build());
			mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users").header("Authorization", "Bearer " + authData.getAccessToken()).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}

	// @Test
	// @Order(value = 3)
	// void testLoginFail_onIncorrectPassword() throws Exception {
	// 	for (RegisterRequest employee : employees) {
	// 		String emp = objectMapper.writeValueAsString(employee);
	// 		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
	// 				.content(emp))
	// 				.andExpect(status().isNotFound());
	// 	}
	// 	Assertions.assertEquals(5, userRepository.findAll().size());
	// }

}
