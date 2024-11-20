package com.redjanvier.signature.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {
  @NotBlank
  private String name;

  @NotBlank
  private String address;

  @NotBlank
  private String mission;

  @NotBlank
  private String website;
}
