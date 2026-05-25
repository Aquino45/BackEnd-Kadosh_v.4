package kado.kadosh.dto;

import com.fasterxml.jackson.annotation.JsonFormat; // <--- 1. IMPORTANTE: Agrega esto
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ClienteListItemDTO {
    private UUID id;
    private String nombre;
    private String apellido;
    private Integer edad;
    private String email;
    private String telefono;
    private String dni;
    private String imagenUrl;

    // 2. Agrega esta línea justo encima de fechaNacimiento:
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate fechaNacimiento;

    // Opcional: Si quieres la fecha de creación bonita también
    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;

    public ClienteListItemDTO(
            UUID id,
            String nombre,
            String apellido,
            Integer edad,
            String email,
            String telefono,
            String dni,
            String imagenUrl,
            LocalDate fechaNacimiento,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.email = email;
        this.telefono = telefono;
        this.dni = dni;
        this.imagenUrl = imagenUrl;
        this.fechaNacimiento = fechaNacimiento;
        this.createdAt = createdAt;
    }
}