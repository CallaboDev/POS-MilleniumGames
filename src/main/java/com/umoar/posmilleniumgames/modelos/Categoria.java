package com.umoar.posmilleniumgames.modelos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "categorias")
@Getter
@Setter
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Identificador numérico autoincremental (ej: 1, 2, 3...)

    // Tipo general de producto: "Videojuegos", "Consolas", "Accesorios", "Coleccionables / Figuras"
    @Column(nullable = false)
    private String nombre;
}