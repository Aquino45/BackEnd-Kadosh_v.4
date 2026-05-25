package kado.kadosh.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonaUpdateDTO {
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String dni;
    private String imagenUrl;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate fechaNacimiento;
    private Integer edad;

    // Datos Apoderado (Los agregas aquí mismo)
    private String nombreApoderado;
    private String apellidoApoderado;
    private String dniApoderado;
    private String telefonoApoderado;
    private String parentesco;

    // ✅ NUEVO CAMPO: El interruptor para borrar
    private Boolean eliminarApoderado;

    private String estado; // Recibirá "ACTIVO", "SEFUE" o "LOBANANEE"
}
