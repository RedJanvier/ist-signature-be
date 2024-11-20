package com.redjanvier.signature.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.redjanvier.signature.models.Company;

public interface CompanyRepository extends JpaRepository<Company, Integer> {
}
