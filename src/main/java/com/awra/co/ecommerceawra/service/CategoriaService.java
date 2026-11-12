package com.awra.co.ecommerceawra.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.awra.co.ecommerceawra.dto.CategoriaRequest;
import com.awra.co.ecommerceawra.dto.CategoriaResponse;
import com.awra.co.ecommerceawra.entity.CategoriaEntity;
import com.awra.co.ecommerceawra.exception.RecursoNoEncontradoException;
import com.awra.co.ecommerceawra.exception.ReglaNegocioException;
import com.awra.co.ecommerceawra.repository.CategoriaRepository;
import com.awra.co.ecommerceawra.repository.ProductoRepository;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public CategoriaService(CategoriaRepository categoriaRepository, ProductoRepository productoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
        return categoriaRepository.findAll().stream()
                .map(this::aResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponse obtener(Long id) {
        return aResponse(buscarEntidad(id));
    }

    @Transactional
    public CategoriaResponse crear(CategoriaRequest request) {
        String nombre = request.nombre().trim();
        if (categoriaRepository.existsByNombre(nombre)) {
            throw new ReglaNegocioException("Ya existe una categoría con el nombre '" + nombre + "'");
        }
        CategoriaEntity entidad = categoriaRepository.save(new CategoriaEntity(nombre));
        return aResponse(entidad);
    }

    @Transactional
    public CategoriaResponse actualizar(Long id, CategoriaRequest request) {
        CategoriaEntity entidad = buscarEntidad(id);
        String nombre = request.nombre().trim();
        if (categoriaRepository.existsByNombreAndIdNot(nombre, id)) {
            throw new ReglaNegocioException("Ya existe una categoría con el nombre '" + nombre + "'");
        }
        entidad.setNombre(nombre);
        return aResponse(entidad);
    }

    @Transactional
    public void eliminar(Long id) {
        CategoriaEntity entidad = buscarEntidad(id);
        if (productoRepository.existsByCategoria_Id(id)) {
            throw new ReglaNegocioException("No se puede eliminar la categoría porque tiene productos activos");
        }
        categoriaRepository.delete(entidad);
        categoriaRepository.flush();
    }

    private CategoriaEntity buscarEntidad(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría", id));
    }

    private CategoriaResponse aResponse(CategoriaEntity entidad) {
        return new CategoriaResponse(entidad.getId(), entidad.getNombre());
    }
}
