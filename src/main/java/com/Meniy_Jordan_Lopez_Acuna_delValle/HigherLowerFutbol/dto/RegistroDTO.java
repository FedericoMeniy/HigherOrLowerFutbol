package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class RegistroDTO {

    @NotBlank(message = "El nombre no puede estar en blanco")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @NotBlank(message = "El email no puede ser nulo ni estar vacío.")
    @Email(message = "El formato del email no es válido.")
    @Size(min = 5, max = 100, message = "El email debe tener entre 5 y 100 caracteres.")
    private String email;

    @NotBlank(message = "La contraseña no puede ser nula ni estar vacía.")
    @Size(min = 8, max = 255, message = "La contraseña debe tener entre 8 y 255 caracteres.")
    private String password;

    @NotBlank(message = "El tipo de rol no puede ser nulo ni estar vacío.")
    @Size(min = 2, max = 50, message = "El tipo de rol debe tener entre 2 y 50 caracteres.")
    private String tipoRol;

}
