package com.umoar.posmilleniumgames.modulos.inventario.repositorios;

import com.umoar.posmilleniumgames.modulos.inventario.modelos.Plataforma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlataformaRepository extends JpaRepository<Plataforma, Long> {
}