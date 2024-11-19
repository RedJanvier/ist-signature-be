package com.redjanvier.security.company;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_company")
public class Company {

  @Id
  @GeneratedValue
  private Integer id;
  private String name;
  private String address;
  private String mission;
  private String website;
}
