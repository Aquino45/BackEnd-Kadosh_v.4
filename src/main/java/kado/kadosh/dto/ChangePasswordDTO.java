package kado.kadosh.dto;

import lombok.Data;

@Data
public class ChangePasswordDTO {
    private String actualPassword; // Para validar que sea el dueño de la cuenta
    private String newPassword;    // La nueva clave que vamos a encriptar
}