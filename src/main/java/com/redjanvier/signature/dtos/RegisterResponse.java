package com.redjanvier.signature.dtos;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse {
  private boolean success;
  private String message;
  private Object data;
}
