package com.example.account_service.Service;

import com.example.account_service.Dto.PostTarjetaRequest;
import com.example.account_service.Dto.TarjetaResponse;
import com.example.account_service.Entity.Cuenta;
import com.example.account_service.Entity.Tarjeta;
import com.example.account_service.Exception.CuentaNotFoundException;
import com.example.account_service.Exception.TarjetaNotFoundException;
import com.example.account_service.Repository.CuentaRepository;
import com.example.account_service.Repository.TarjetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class TarjetaService {

    @Autowired
    private TarjetaRepository tarjetaRepository;

    @Autowired
    private CuentaRepository cuentaRepository;

    public List<TarjetaResponse> obtenerTarjetasPorUsuario(Long userId) {
        Cuenta cuenta = cuentaRepository.findByUserId(userId)
                .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada para el usuario"));

        return tarjetaRepository.findByCuentaId(cuenta.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<TarjetaResponse> obtenerTarjetas(Long cuentaId, Long userId) {
        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada con id: " + cuentaId));

        if (!cuenta.getUserId().equals(userId)) {
            throw new SecurityException("No tienes permiso para acceder a esta cuenta");
        }

        return tarjetaRepository.findByCuentaId(cuentaId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TarjetaResponse obtenerTarjeta(Long cuentaId, Long cardId, Long userId) {
        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada con id: " + cuentaId));

        if (!cuenta.getUserId().equals(userId)) {
            throw new SecurityException("No tienes permiso para acceder a esta cuenta");
        }

        Tarjeta tarjeta = tarjetaRepository.findByIdAndCuentaId(cardId, cuentaId)
                .orElseThrow(() -> new CuentaNotFoundException("Tarjeta no encontrada con id: " + cardId));

        return toResponse(tarjeta);
    }

    public TarjetaResponse crearTarjeta(Long cuentaId, Long userId, PostTarjetaRequest request) {
        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada con id: " + cuentaId));

        if (!cuenta.getUserId().equals(userId)) {
            throw new SecurityException("No tienes permiso para acceder a esta cuenta");
        }

        if (tarjetaRepository.existsByNumeroTarjeta(request.getNumeroTarjeta())) {
            throw new IllegalStateException("La tarjeta ya está asociada a otra cuenta");
        }

        YearMonth expiracion = YearMonth.parse(request.getFechaExpiracion(), DateTimeFormatter.ofPattern("MM/yy"));
        if (expiracion.isBefore(YearMonth.now())) {
            throw new IllegalArgumentException("La tarjeta está vencida");
        }
        LocalDate fechaExpiracion = expiracion.atEndOfMonth();

        Tarjeta tarjeta = new Tarjeta(
                cuenta,
                request.getNumeroTarjeta(),
                request.getTitular(),
                fechaExpiracion,
                request.getTipo(),
                request.getMarca()
        );
        tarjetaRepository.save(tarjeta);
        return toResponse(tarjeta);
    }

    public void eliminarTarjeta(Long cuentaId, Long cardId, Long userId) {
        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada con id: " + cuentaId));

        if (!cuenta.getUserId().equals(userId)) {
            throw new SecurityException("No tienes permiso para acceder a esta cuenta");
        }

        Tarjeta tarjeta = tarjetaRepository.findByIdAndCuentaId(cardId, cuentaId)
                .orElseThrow(() -> new TarjetaNotFoundException("Tarjeta no encontrada con id: " + cardId));

        tarjetaRepository.delete(tarjeta);
    }

    private TarjetaResponse toResponse(Tarjeta tarjeta) {
        return new TarjetaResponse(
                tarjeta.getId(),
                tarjeta.getCuenta().getId(),
                tarjeta.getNumeroTarjeta(),
                tarjeta.getTitular(),
                tarjeta.getFechaExpiracion(),
                tarjeta.getTipo(),
                tarjeta.getMarca()
        );
    }
}
