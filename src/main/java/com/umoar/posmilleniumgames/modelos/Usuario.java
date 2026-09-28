package com.umoar.posmilleniumgames.modelos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Identificador para inicio de sesión en el sistema (ej: "admin", "cajero1", "diego.morales")
    @Column(unique = true, nullable = false)
    private String username;

    // Clave de acceso guardada en base de datos (ej: "password123")
    @Column(nullable = false)
    private String password;

    // Nombre real del empleado que atiende (ej: "Fercho", "Andrea Morales")
    @Column(nullable = false)
    private String nombre;

    // Nivel de permisos en el sistema: "ADMIN" (acceso total) o "CAJERO" (solo ventas e inventario básico)
    @Column(nullable = false)
    private String rol;
}