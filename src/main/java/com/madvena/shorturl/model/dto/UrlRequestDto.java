package com.madvena.shorturl.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlRequestDto {
    @NotNull(message = "field original cannot be null")
    @NotBlank(message = "field original cannot be empty")
    @URL(message = "field original must be a valid URL address")
    private String original;
}
