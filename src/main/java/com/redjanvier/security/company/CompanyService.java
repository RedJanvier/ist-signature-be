package com.redjanvier.security.company;

import org.springframework.stereotype.Service;

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
