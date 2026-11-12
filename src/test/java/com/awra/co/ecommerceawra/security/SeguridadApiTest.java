package com.awra.co.ecommerceawra.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PagedModel;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.awra.co.ecommerceawra.controller.AuthController;
import com.awra.co.ecommerceawra.controller.CategoriaController;
import com.awra.co.ecommerceawra.controller.PublicoProductoController;
import com.awra.co.ecommerceawra.dto.ProductoPublicoResumenResponse;
import com.awra.co.ecommerceawra.service.AuthService;
import com.awra.co.ecommerceawra.service.CategoriaService;
import com.awra.co.ecommerceawra.service.ProductoPublicoService;

@WebMvcTest(controllers = {CategoriaController.class, PublicoProductoController.class, AuthController.class})
@Import({SecurityConfig.class, ErrorAutenticacionEntryPoint.class, ErrorAccesoDenegadoHandler.class, RespuestaSeguridad.class})
@ActiveProfiles("test")
class SeguridadApiTest {

    private static final String LOGIN_VACIO = "{}";

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CategoriaService categoriaService;

    @MockitoBean
    private ProductoPublicoService productoPublicoService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    void catalogoPublicoSeVeSinToken() throws Exception {
        when(productoPublicoService.listar(any(), any()))
                .thenReturn(new PagedModel<>(new PageImpl<ProductoPublicoResumenResponse>(List.of())));

        mvc.perform(get("/api/publico/productos")).andExpect(status().isOk());
    }

    @Test
    void adminSinTokenDevuelve401() throws Exception {
        mvc.perform(get("/api/admin/categorias")).andExpect(status().isUnauthorized());
    }

    @Test
    void adminConTokenSinRolAdminDevuelve403() throws Exception {
        mvc.perform(get("/api/admin/categorias")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminConRolAdminAccedeAlListado() throws Exception {
        when(categoriaService.listar()).thenReturn(List.of());

        mvc.perform(get("/api/admin/categorias")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk());
    }

    @Test
    void escribirEnRutaPublicaSinTokenDevuelve401() throws Exception {
        mvc.perform(post("/api/publico/productos").contentType("application/json").content(LOGIN_VACIO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginConCredencialesIncorrectasDevuelve401() throws Exception {
        when(authService.login(any())).thenThrow(new BadCredentialsException("mal"));

        mvc.perform(post("/api/auth/login").contentType("application/json")
                        .content("{\"username\":\"admin\",\"password\":\"mal\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginSinCamposDevuelve400() throws Exception {
        mvc.perform(post("/api/auth/login").contentType("application/json").content(LOGIN_VACIO))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cors_permiteElOrigenDelFrontend() throws Exception {
        mvc.perform(options("/api/admin/categorias")
                        .header("Origin", "http://localhost:4200")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"));
    }
}
