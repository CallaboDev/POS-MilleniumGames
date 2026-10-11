package com.umoar.posmilleniumgames.modulos.inventario.repositorios;

import com.umoar.posmilleniumgames.modulos.inventario.modelos.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}