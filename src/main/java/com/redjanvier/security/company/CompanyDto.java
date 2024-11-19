package com.redjanvier.security.company;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {
  private String name;
  private String address;
  private String mission;
  private String website;
}
