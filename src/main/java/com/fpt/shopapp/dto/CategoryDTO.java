package com.fpt.shopapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDTO {
    @NotBlank(message = "category name cannot be empty")
    private String name;
}
