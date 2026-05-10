package com.user_service.Service;

import com.user_service.Dtos.LoginResponse;
import com.user_service.Dtos.PatchUsuarioRequest;
import com.user_service.Dtos.UsuarioResponse;
import com.user_service.Entity.SesionUsuario;
import com.user_service.Entity.Usuario;
import com.user_service.Exception.ContrasenaIncorrectaException;
import com.user_service.Exception.UsuarioNotFoundException;
import com.user_service.Repository.SesionUsuarioRepository;
import com.user_service.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SesionUsuarioRepository sesionUsuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private final Random random = new SecureRandom();

    private List<String> cargarPalabras() {
        List<String> palabras = new ArrayList<>();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("palabras-alias.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String palabra = linea.trim();
                if (!palabra.isEmpty()) {
                    palabras.add(palabra);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("No se pudo cargar el archivo palabras-alias.txt", e);
        }
        return palabras;
    }

    public Usuario registrarUsuario(String nombre, String apellido, String dni,
                                    String email, String telefono, String password) {

        // Validar que email no exista
        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Validar que DNI no exista
        if (usuarioRepository.existsByDni(dni)) {
            throw new IllegalArgumentException("El DNI ya está registrado");
        }

        // Crear usuario con contraseña hasheada
        Usuario usuario = new Usuario(nombre, apellido, dni, email, telefono,
                passwordEncoder.encode(password));

        // asIGNAR CVU Y ALIAS GENERADOS
        usuario.setCvu(generarCVUUnico());
        usuario.setAlias(generarAliasUnico());

        return usuarioRepository.save(usuario);
    }

    public LoginResponse login(String email, String password) {
        Usuario usuario = usuarioRepository.findByEmailAndActivo(email)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario inexistente"));

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new ContrasenaIncorrectaException("Contraseña incorrecta");
        }

        String token = jwtService.generarToken(usuario.getId(), usuario.getEmail(), usuario.getRol().name());

        // Guardar sesión
        SesionUsuario sesion = new SesionUsuario(
                usuario,
                String.valueOf(token.hashCode()),
                LocalDateTime.now().plusSeconds(86400)
        );
        sesionUsuarioRepository.save(sesion);

        LoginResponse.UserDto userDto = new LoginResponse.UserDto(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getApellido()
        );

        return new LoginResponse(token, userDto);
    }

    public UsuarioResponse obtenerPerfil(Long id, Long userId) {
        if (!id.equals(userId)) {
            throw new SecurityException("No tienes permiso para ver este perfil");
        }
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado con id: " + id));
        return toResponse(usuario);
    }

    public UsuarioResponse actualizarPerfil(Long id, Long userId, PatchUsuarioRequest request) {
        if (!id.equals(userId)) {
            throw new SecurityException("No tienes permiso para modificar este perfil");
        }
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado con id: " + id));

        if (request.getNombre() != null && !request.getNombre().isBlank()) {
            usuario.setNombre(request.getNombre());
        }
        if (request.getApellido() != null && !request.getApellido().isBlank()) {
            usuario.setApellido(request.getApellido());
        }
        if (request.getTelefono() != null && !request.getTelefono().isBlank()) {
            usuario.setTelefono(request.getTelefono());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        usuarioRepository.save(usuario);
        return toResponse(usuario);
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getDni(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getCvu(),
                usuario.getAlias()
        );
    }

    public void logout(String token) {
        String tokenHash = String.valueOf(token.hashCode());
        SesionUsuario sesion = sesionUsuarioRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new IllegalArgumentException("Sesión no encontrada"));
        sesion.invalidar();
        sesionUsuarioRepository.save(sesion);
    }

    private String generarCVUUnico() {
        String cvu;
        do {
            // Generar CVU de 22 dígitos
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 22; i++) {
                sb.append(random.nextInt(10));
            }
            cvu = sb.toString();
        } while (usuarioRepository.existsByCvu(cvu));

        return cvu;
    }

    private String generarAliasUnico() {
        List<String> palabras = cargarPalabras();
        String alias;
        do {
            alias = palabras.get(random.nextInt(palabras.size())) + "." +
                    palabras.get(random.nextInt(palabras.size())) + "." +
                    palabras.get(random.nextInt(palabras.size()));
        } while (usuarioRepository.existsByAlias(alias));

        return alias;
    }
}