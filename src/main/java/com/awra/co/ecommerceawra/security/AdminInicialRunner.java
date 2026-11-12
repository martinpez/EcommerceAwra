package com.awra.co.ecommerceawra.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.awra.co.ecommerceawra.entity.RolEntity;
import com.awra.co.ecommerceawra.entity.UsuarioEntity;
import com.awra.co.ecommerceawra.repository.RolRepository;
import com.awra.co.ecommerceawra.repository.UsuarioRepository;

@Component
public class AdminInicialRunner implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final String username;
    private final String password;

    public AdminInicialRunner(UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.username}") String username,
            @Value("${app.admin.password}") String password) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.username = username;
        this.password = password;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (username.isBlank() || usuarioRepository.existsByUsername(username)) {
            return;
        }
        if (password.isBlank()) {
            throw new IllegalStateException("ADMIN_PASSWORD es obligatorio para crear el administrador inicial");
        }
        RolEntity admin = rolRepository.findByNombre("ADMIN")
                .orElseGet(() -> rolRepository.save(new RolEntity("ADMIN")));
        UsuarioEntity usuario = new UsuarioEntity(username, passwordEncoder.encode(password));
        usuario.asignarRol(admin);
        usuarioRepository.save(usuario);
    }
}
