package com.aa.ecommerce.dto;

import com.aa.ecommerce.enums.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
@Getter
@Setter
public class UserResponseDTO {
    private Long id;
    private String email;
    private Set<Role> role;
}
