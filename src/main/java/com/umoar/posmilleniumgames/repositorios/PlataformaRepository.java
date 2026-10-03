package com.umoar.posmilleniumgames.repositorios;

import com.umoar.posmilleniumgames.modelos.Plataforma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlataformaRepository extends JpaRepository<Plataforma, Long> {
}
