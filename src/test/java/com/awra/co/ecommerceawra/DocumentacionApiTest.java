package com.awra.co.ecommerceawra;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"dev", "test"})
class DocumentacionApiTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void laDocumentacionOpenApiSeGeneraConRutasYEsquemaJwt() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"bearer-key\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/api/admin/categorias")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/api/publico/productos")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/api/auth/login")));
    }

    @Test
    void laDocumentacionIncluyeLosEndpointsDeLaFase11() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/api/publico/sitio")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/api/publico/ediciones")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/api/admin/sitio/empresa")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/api/admin/sitio/secciones")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/api/admin/sitio/media")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/api/admin/sitio/videos")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/api/admin/sitio/ediciones")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/api/auth/me")));
    }

    @Test
    void laDocumentacionIncluyeLosEndpointsDeLaFase12() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/api/publico/categorias")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/api/admin/productos")));
    }
}
