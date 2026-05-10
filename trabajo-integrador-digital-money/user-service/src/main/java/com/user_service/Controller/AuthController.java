package com.user_service.Controller;

import com.user_service.Dtos.LoginRequest;
import com.user_service.Dtos.LoginResponse;
import com.user_service.Dtos.RegisterRequest;
import com.user_service.Dtos.RegisterResponse;
import com.user_service.Entity.Usuario;
import com.user_service.Exception.ContrasenaIncorrectaException;
import com.user_service.Exception.UsuarioNotFoundException;
import com.user_service.Service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;


    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        System.out.println("testtttttttttttttttttttttttttttt");

        try {
            Usuario usuarioCreado = usuarioService.registrarUsuario(
                    request.getNombre(),
                    request.getApellido(),
                    request.getDni(),
                    request.getEmail(),
                    request.getTelefono(),
                    request.getPassword()
            );

            RegisterResponse response = new RegisterResponse(
                    usuarioCreado.getId(),
                    usuarioCreado.getNombre(),
                    usuarioCreado.getApellido(),
                    usuarioCreado.getDni(),
                    usuarioCreado.getEmail(),
                    usuarioCreado.getTelefono(),
                    usuarioCreado.getCvu(),
                    usuarioCreado.getAlias()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            return ResponseEntity.ok(usuarioService.login(request.getEmail(), request.getPassword()));
        } catch (UsuarioNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (ContrasenaIncorrectaException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor: " + e.getMessage()));
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
            usuarioService.logout(token);
            return ResponseEntity.ok(Map.of("message", "Sesión cerrada correctamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor: " + e.getMessage()));
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        System.out.println("testtttttttttttttttttttttttttttt");
        return ResponseEntity.ok("Auth Controller funcionando correctamente");
    }
}