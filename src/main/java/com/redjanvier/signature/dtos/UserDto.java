package com.redjanvier.signature.dtos;

import com.redjanvier.signature.models.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
  private Integer id;
  private String firstname;
  private String lastname;
  private String email;
  private String phone;
  private Role role;
  private String position;
  private Boolean enabled;
}
