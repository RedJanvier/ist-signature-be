package com.redjanvier.security;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.redjanvier.security.auth.AuthenticationService;
import com.redjanvier.security.auth.RegisterRequest;
import com.redjanvier.security.company.CompanyDto;
import com.redjanvier.security.company.CompanyService;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class SecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurityApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(
			AuthenticationService authService, CompanyService companyService
	) {
		return args -> {
			var admin = RegisterRequest.builder()
					.firstname("Admin")
					.lastname("Admin")
					.email("admin@ist.com")
					.password("Jannyda1")
					.build();
			authService.registerAdmin(admin);

			CompanyDto company = CompanyDto.builder()
					.name("IST Africa")
					.address("Kigali · KG 28 Ave, 57 · Kigali")
					.mission("Empowering learning, every day and everywhere.")
					.website("www.ist.com")
					.build();
			companyService.create(company);
		};
	}
}
