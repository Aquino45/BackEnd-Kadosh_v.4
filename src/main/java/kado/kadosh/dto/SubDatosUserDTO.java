package kado.kadosh.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubDatosUserDTO {
    // 1. Datos de la cuenta
    private UUID usuarioId;
    private String rol;
    private LocalDateTime fechaPrimerHistorial;

    // 🚨 AGREGADO: Aquí guardaremos "ACTIVO", "SEFUE" o "LOBANANEE"
    private String estado;

    // 2. Datos de la Persona
    private String nombre;
    private String apellido;
    private String dni;
    private String correo;
    private String telefono;
    private String imagenUrl;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate fechaNacimiento;

    // ✅ La edad calculada al momento
    private Integer edad;

    // 3. Datos del Apoderado
    private String nombreApoderado;
    private String apellidoApoderado;
    private String dniApoderado;
    private String telefonoApoderado;
    private String parentesco;
}