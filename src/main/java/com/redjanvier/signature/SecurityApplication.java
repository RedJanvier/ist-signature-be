package com.redjanvier.signature;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.redjanvier.signature.dtos.CompanyDto;
import com.redjanvier.signature.dtos.RegisterRequest;
import com.redjanvier.signature.services.AuthenticationService;
import com.redjanvier.signature.services.CompanyService;

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
