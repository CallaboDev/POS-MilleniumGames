package com.umoar.posmilleniumgames.modelos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "clientes")
@Getter
@Setter
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre completo del comprador (ej: "Carlos Alberto Gómez Mendoza", "Consumidor Final")
    @Column(nullable = false)
    private String nombreCompleto;

    // Número de contacto para avisos de apartados o pedidos (ej: "7890-1234")
    private String telefono;

    // Correo electrónico para comprobantes digitales (ej: "cliente@gmail.com")
    private String correo;
}