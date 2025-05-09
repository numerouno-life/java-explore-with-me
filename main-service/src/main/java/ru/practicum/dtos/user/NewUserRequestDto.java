package ru.practicum.dtos.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewUserRequestDto {

    @NotNull
    @Email
    @NotBlank
    @Size(min = 6, max = 254)
    String email;

    @NotBlank
    @Size(min = 2, max = 250)
    String name;
}
