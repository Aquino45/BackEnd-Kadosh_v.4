package kado.kadosh.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDate;

@Data
public class RegisterRequestDTO {
    private String nombre;
    private String apellido;
    private Integer edad;
    private String email;
    private String telefono;
    private String dni;
    private String password;
    private String imagenUrl;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate fechaNacimiento;

    // 👈 Nuevo campo opcional para el apoderado
    private ApoderadoDTO apoderado;
}