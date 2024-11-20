package com.redjanvier.signature.controllers;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.redjanvier.signature.dtos.CompanyDto;
import com.redjanvier.signature.models.Company;
import com.redjanvier.signature.repositories.CompanyRepository;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/company")
public class CompanyController {

    private final CompanyRepository companyRepository;

    @GetMapping("get")
    public ResponseEntity<Company> get()  {
        return ResponseEntity.ok().body(companyRepository.findById(1).orElseThrow(() -> new RuntimeException("Failed to retrieve company info!")));
    }
    
    @PutMapping
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<CompanyDto> update(@RequestBody CompanyDto company) {
        Company old = companyRepository.findById(1).orElseThrow(() -> new RuntimeException("Failed to update company info!"));
        old.setName(company.getName());
        old.setAddress(company.getAddress());
        old.setMission(company.getMission());
        old.setWebsite(company.getWebsite());
        
        Company saved = companyRepository.save(old);
        return ResponseEntity.ok(CompanyDto.builder().name(saved.getName()).address(saved.getAddress()).mission(saved.getMission()).website(saved.getWebsite()).build());
    }
}
