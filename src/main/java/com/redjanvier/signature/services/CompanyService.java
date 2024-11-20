package com.redjanvier.signature.services;

import org.springframework.stereotype.Service;

import com.redjanvier.signature.dtos.CompanyDto;
import com.redjanvier.signature.models.Company;
import com.redjanvier.signature.repositories.CompanyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyService {

  private final CompanyRepository companyRepository;
  
  public Company create(CompanyDto company)  {
    return companyRepository.save(
            Company.builder()
            .name(company.getName())
            .address(company.getAddress())
            .mission(company.getMission())
            .website(company.getWebsite())
            .build()
        );
}
}
