package com.redjanvier.signature;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redjanvier.signature.dtos.RegisterRequest;
import com.redjanvier.signature.models.Role;
import com.redjanvier.signature.models.User;
import com.redjanvier.signature.repositories.CompanyRepository;
import com.redjanvier.signature.repositories.TokenRepository;
import com.redjanvier.signature.repositories.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
class SecurityApplicationTests {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TokenRepository tokenRepository;
	@Autowired
	private CompanyRepository companyRepository;

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
		RegisterRequest emp1 = RegisterRequest.builder().firstname("test emp1").password("test emp1").lastname("test emp1").email("address1").build();
		RegisterRequest emp2 = RegisterRequest.builder().firstname("test emp2").password("test emp2").lastname("test emp2").email("address2").build();
		RegisterRequest emp3 = RegisterRequest.builder().firstname("test emp3").password("test emp3").lastname("test emp3").email("address3").build();
		RegisterRequest emp4 = RegisterRequest.builder().firstname("test emp4").password("test emp4").lastname("test emp4").email("address4").build();
		RegisterRequest emp5 = RegisterRequest.builder().firstname("test emp5").password("test emp5").lastname("test emp5").email("address5").build();

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


	@Test
	@Order(value = 1)
	void testConnectionToDatabase() {
		Assertions.assertNotNull(userRepository);
		Assertions.assertNotNull(tokenRepository);
		Assertions.assertNotNull(companyRepository);
	}

	@Test
	@Order(value = 2)
	void testAddEmployees() throws Exception {
		for (RegisterRequest employee : employees) {
			String emp = objectMapper.writeValueAsString(employee);
			mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON)
					.content(emp)).andExpect(status().isOk());
		}
		Assertions.assertEquals(5, userRepository.findAll().size());
	}

}
