package com.example.account_service.Controller;

import com.example.account_service.Dto.TarjetaResponse;
import com.example.account_service.Exception.CuentaNotFoundException;
import com.example.account_service.Service.TarjetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cards")
@CrossOrigin(origins = "*")
public class CardsController {

    @Autowired
    private TarjetaService tarjetaService;

    @GetMapping
    public ResponseEntity<?> obtenerTarjetas(@RequestHeader("X-User-Id") Long userId) {
        try {
            List<TarjetaResponse> tarjetas = tarjetaService.obtenerTarjetasPorUsuario(userId);
            return ResponseEntity.ok(tarjetas);
        } catch (CuentaNotFoundException e) {
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
}
