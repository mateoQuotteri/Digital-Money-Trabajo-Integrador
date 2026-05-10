package com.example.account_service.Controller;

import com.example.account_service.Dto.PostTarjetaRequest;
import com.example.account_service.Dto.TarjetaResponse;
import com.example.account_service.Exception.TarjetaNotFoundException;
import com.example.account_service.Service.TarjetaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/accounts")
@CrossOrigin(origins = "*")
public class TarjetaController {

    @Autowired
    private TarjetaService tarjetaService;

    @GetMapping("/{id}/cards")
    public ResponseEntity<?> obtenerTarjetas(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            List<TarjetaResponse> tarjetas = tarjetaService.obtenerTarjetas(id, userId);
            return ResponseEntity.ok(tarjetas);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/cards/{cardId}")
    public ResponseEntity<?> obtenerTarjeta(
            @PathVariable Long id,
            @PathVariable Long cardId,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            TarjetaResponse tarjeta = tarjetaService.obtenerTarjeta(id, cardId, userId);
            return ResponseEntity.ok(tarjeta);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}/cards/{cardId}")
    public ResponseEntity<?> eliminarTarjeta(
            @PathVariable Long id,
            @PathVariable Long cardId,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            tarjetaService.eliminarTarjeta(id, cardId, userId);
            return ResponseEntity.ok().build();
        } catch (TarjetaNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/cards")
    public ResponseEntity<?> crearTarjeta(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody PostTarjetaRequest request) {
        try {
            TarjetaResponse tarjeta = tarjetaService.crearTarjeta(id, userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(tarjeta);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
