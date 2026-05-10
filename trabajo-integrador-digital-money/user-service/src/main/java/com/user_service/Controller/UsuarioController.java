package com.user_service.Controller;

import com.user_service.Dtos.PatchUsuarioRequest;
import com.user_service.Dtos.UsuarioResponse;
import com.user_service.Exception.UsuarioNotFoundException;
import com.user_service.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPerfil(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            UsuarioResponse response = usuarioService.obtenerPerfil(id, userId);
            return ResponseEntity.ok(response);
        } catch (UsuarioNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> actualizarPerfil(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody PatchUsuarioRequest request) {
        try {
            UsuarioResponse response = usuarioService.actualizarPerfil(id, userId, request);
            return ResponseEntity.ok(response);
        } catch (UsuarioNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
