package com.umoar.posmilleniumgames.servicios;

import com.umoar.posmilleniumgames.modelos.Plataforma;
import com.umoar.posmilleniumgames.repositorios.PlataformaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlataformaService {

    private final PlataformaRepository plataformaRepository;

    public PlataformaService(PlataformaRepository plataformaRepository) {
        this.plataformaRepository = plataformaRepository;
    }

    @Transactional(readOnly = true)
    public List<Plataforma> obtenerTodas() {
        return plataformaRepository.findAll();
    }
}
