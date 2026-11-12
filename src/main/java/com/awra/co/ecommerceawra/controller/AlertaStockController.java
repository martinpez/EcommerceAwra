package com.awra.co.ecommerceawra.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.awra.co.ecommerceawra.dto.AlertaStockResponse;
import com.awra.co.ecommerceawra.service.AlertaStockService;

@Tag(name = "Admin - Alertas de stock")
@SecurityRequirement(name = "bearer-key")
@RestController
@RequestMapping("/api/admin/alertas")
public class AlertaStockController {

    private final AlertaStockService alertaStockService;

    public AlertaStockController(AlertaStockService alertaStockService) {
        this.alertaStockService = alertaStockService;
    }

    @GetMapping
    public List<AlertaStockResponse> listar(@RequestParam(required = false) Boolean leida) {
        return alertaStockService.listar(leida);
    }

    @PatchMapping("/{id}/leida")
    public AlertaStockResponse marcarLeida(@PathVariable Long id) {
        return alertaStockService.marcarLeida(id);
    }
}
