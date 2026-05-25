package kado.kadosh.enums;

public enum EstadoUsuario {
    ACTIVO,      // El usuario puede entrar normal
    SEFUE,       // El usuario se retiró voluntariamente (Soft Delete)
    LOBANANEE    // El usuario fue bloqueado/bannado por la administración
}