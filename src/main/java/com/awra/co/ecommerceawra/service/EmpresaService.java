package com.awra.co.ecommerceawra.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.awra.co.ecommerceawra.dto.EmpresaActualizarRequest;
import com.awra.co.ecommerceawra.dto.EmpresaResponse;
import com.awra.co.ecommerceawra.entity.EmpresaEntity;
import com.awra.co.ecommerceawra.repository.EmpresaRepository;

/**
 * Datos de la empresa: contacto, redes sociales y direccion.
 *
 * Solo hay una empresa. Si todavia no existe, se crea con los valores por
 * defecto la primera vez que se consulta, para que el panel y la web siempre
 * tengan algo con que trabajar.
 */
@Service
public class EmpresaService {

    private static final String NOMBRE_POR_DEFECTO = "AWRA";
    private static final String WHATSAPP_POR_DEFECTO = "573123461277";

    private final EmpresaRepository empresaRepository;

    public EmpresaService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    @Transactional
    public EmpresaResponse obtener() {
        return aResponse(obtenerOCrear());
    }

    @Transactional
    public EmpresaResponse actualizar(EmpresaActualizarRequest request) {
        EmpresaEntity empresa = obtenerOCrear();
        empresa.setNombre(request.nombre().trim());
        empresa.setSlogan(limpio(request.slogan()));
        empresa.setDescripcion(limpio(request.descripcion()));
        empresa.setWhatsapp(request.whatsapp().trim());
        empresa.setTelefono(limpio(request.telefono()));
        empresa.setEmail(limpio(request.email()));
        empresa.setDireccion(limpio(request.direccion()));
        empresa.setCiudad(limpio(request.ciudad()));
        empresa.setPais(limpio(request.pais()));
        empresa.setInstagram(limpio(request.instagram()));
        empresa.setTiktok(limpio(request.tiktok()));
        empresa.setFacebook(limpio(request.facebook()));
        empresa.setYoutube(limpio(request.youtube()));
        empresa.setHorario(limpio(request.horario()));
        return aResponse(empresaRepository.save(empresa));
    }

    private EmpresaEntity obtenerOCrear() {
        return empresaRepository.findFirstByOrderByIdAsc()
                .orElseGet(() -> empresaRepository.save(new EmpresaEntity(NOMBRE_POR_DEFECTO, WHATSAPP_POR_DEFECTO)));
    }

    /** Los campos vacios se guardan como null, no como cadena en blanco. */
    private static String limpio(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

    private EmpresaResponse aResponse(EmpresaEntity empresa) {
        return new EmpresaResponse(
                empresa.getId(),
                empresa.getNombre(),
                empresa.getSlogan(),
                empresa.getDescripcion(),
                empresa.getWhatsapp(),
                empresa.getTelefono(),
                empresa.getEmail(),
                empresa.getDireccion(),
                empresa.getCiudad(),
                empresa.getPais(),
                empresa.getInstagram(),
                empresa.getTiktok(),
                empresa.getFacebook(),
                empresa.getYoutube(),
                empresa.getHorario(),
                empresa.getFechaActualizacion());
    }
}
