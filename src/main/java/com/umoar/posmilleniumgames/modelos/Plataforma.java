package com.umoar.posmilleniumgames.modelos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "plataformas")
@Getter
@Setter
public class Plataforma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Identificador numérico autoincremental

    // Ecosistema/Consola: "PlayStation 5", "Nintendo Switch", "Xbox Series X", "PC", "Multiplataforma"
    @Column(nullable = false, unique = true)
    private String nombre;
}