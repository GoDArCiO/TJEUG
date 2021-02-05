package com.project18.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDTO {

    @NotNull
    @Size(min=3, max=50, message = "Niepoprawna wartość imienia")
    @Email(message = "Zły format email")
    private String email;

    @NotNull
    @Size(min=8, max=50, message = "Zbyt krótkie hasło")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message = "Hasło powinno zawierwać wielką litere, małą litere, cyfrę, znak specjalny: @#$%^&+=")
    private String password;

    @NotNull
    @Size(min=8, max=50, message = "Zbyt krótkie hasło")

    private String re_password;

    @NotNull
    @Size(min=3, max=50, message = "Niepoprawna wartość imienia i nazwiska")
    private String fullName;
}
