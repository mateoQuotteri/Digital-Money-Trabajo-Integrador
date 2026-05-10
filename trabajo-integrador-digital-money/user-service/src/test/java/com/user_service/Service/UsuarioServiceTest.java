package com.user_service.Service;

import com.user_service.Dtos.PatchUsuarioRequest;
import com.user_service.Dtos.UsuarioResponse;
import com.user_service.Entity.Rol;
import com.user_service.Entity.Usuario;
import com.user_service.Exception.UsuarioNotFoundException;
import com.user_service.Repository.SesionUsuarioRepository;
import com.user_service.Repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de UsuarioService — Sprint 2
 * Cubre: TC-S2-001, TC-S2-002, TC-S2-003, TC-S2-004
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private SesionUsuarioRepository sesionUsuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario1;
    private Usuario usuario2;

    @BeforeEach
    void setUp() {
        usuario1 = new Usuario("Mateo", "Lopez", "12345678",
                "mateo@test.com", "+5491112345678", "hashedPass",
                "1234567890123456789012", "sol.luna.rio");
        usuario1.setId(1L);
        usuario1.setRol(Rol.ROLE_USER);
        usuario1.setActivo(true);

        usuario2 = new Usuario("Juan", "Gomez", "87654321",
                "juan@test.com", "+5491198765432", "hashedPass2",
                "9876543210987654321098", "mar.pez.luz");
        usuario2.setId(2L);
        usuario2.setRol(Rol.ROLE_USER);
        usuario2.setActivo(true);
    }

    // -------------------------------------------------------------------------
    // TC-S2-001 — Editar nombre del usuario autenticado
    // -------------------------------------------------------------------------

    @Test
    void tc_s2_001_actualizarPerfil_nombre_exitoso() {
        PatchUsuarioRequest request = new PatchUsuarioRequest();
        request.setNombre("Matias");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario1));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario1);

        UsuarioResponse response = usuarioService.actualizarPerfil(1L, 1L, request);

        assertNotNull(response);
        assertEquals("Matias", response.getNombre());
        verify(usuarioRepository).save(usuario1);
    }

    @Test
    void tc_s2_001_actualizarPerfil_nombre_nulo_noModificaNombre() {
        PatchUsuarioRequest request = new PatchUsuarioRequest();
        request.setNombre(null);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario1));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario1);

        UsuarioResponse response = usuarioService.actualizarPerfil(1L, 1L, request);

        assertEquals("Mateo", response.getNombre());
    }

    // -------------------------------------------------------------------------
    // TC-S2-002 — Editar contraseña del usuario autenticado
    // -------------------------------------------------------------------------

    @Test
    void tc_s2_002_actualizarPerfil_password_exitoso() {
        PatchUsuarioRequest request = new PatchUsuarioRequest();
        request.setPassword("NuevoPass99");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario1));
        when(passwordEncoder.encode("NuevoPass99")).thenReturn("nuevoHashedPass");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario1);

        usuarioService.actualizarPerfil(1L, 1L, request);

        verify(passwordEncoder).encode("NuevoPass99");
        assertEquals("nuevoHashedPass", usuario1.getPassword());
    }

    // -------------------------------------------------------------------------
    // TC-S2-003 — Editar teléfono del usuario autenticado
    // -------------------------------------------------------------------------

    @Test
    void tc_s2_003_actualizarPerfil_telefono_exitoso() {
        PatchUsuarioRequest request = new PatchUsuarioRequest();
        request.setTelefono("+5491199887766");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario1));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario1);

        UsuarioResponse response = usuarioService.actualizarPerfil(1L, 1L, request);

        assertNotNull(response);
        assertEquals("+5491199887766", response.getTelefono());
    }

    // -------------------------------------------------------------------------
    // TC-S2-004 — Intentar editar perfil de otro usuario
    // -------------------------------------------------------------------------

    @Test
    void tc_s2_004_actualizarPerfil_perfilAjeno_lanzaSecurityException() {
        PatchUsuarioRequest request = new PatchUsuarioRequest();
        request.setNombre("Hackeado");

        // usuario1 (userId=1) intenta modificar el perfil de usuario2 (id=2)
        assertThrows(SecurityException.class,
                () -> usuarioService.actualizarPerfil(2L, 1L, request));
    }

    @Test
    void tc_s2_004_actualizarPerfil_usuarioInexistente_lanzaNotFoundException() {
        PatchUsuarioRequest request = new PatchUsuarioRequest();
        request.setNombre("Test");

        when(usuarioRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNotFoundException.class,
                () -> usuarioService.actualizarPerfil(9999L, 9999L, request));
    }
}
