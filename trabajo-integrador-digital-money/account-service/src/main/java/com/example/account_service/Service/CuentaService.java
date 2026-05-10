package com.example.account_service.Service;

import com.example.account_service.Dto.CuentaResponse;
import com.example.account_service.Dto.DestinatarioResponse;
import com.example.account_service.Dto.TransaccionResponse;
import com.example.account_service.Entity.Cuenta;
import com.example.account_service.Entity.Tarjeta;
import com.example.account_service.Entity.Transaccion;
import com.example.account_service.Entity.Transferencia;
import com.example.account_service.Entity.TipoTransaccion;
import com.example.account_service.Exception.CuentaNotFoundException;
import com.example.account_service.Exception.FondosInsuficientesException;
import com.example.account_service.Exception.TarjetaNotFoundException;
import com.example.account_service.Exception.TransaccionNotFoundException;
import com.example.account_service.Repository.CuentaRepository;
import com.example.account_service.Repository.TarjetaRepository;
import com.example.account_service.Repository.TransaccionRepository;
import com.example.account_service.Repository.TransferenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

@Service
public class CuentaService {

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private TransaccionRepository transaccionRepository;

    @Autowired
    private TarjetaRepository tarjetaRepository;

    @Autowired
    private TransferenciaRepository transferenciaRepository;

    private final Random random = new SecureRandom();

    public CuentaResponse obtenerCuenta(Long cuentaId, Long userId) {
        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada con id: " + cuentaId));

        if (!cuenta.getUserId().equals(userId)) {
            throw new SecurityException("No tienes permiso para acceder a esta cuenta");
        }

        return toResponse(cuenta);
    }

    public List<TransaccionResponse> obtenerTransacciones(Long cuentaId, Long userId) {
        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada con id: " + cuentaId));

        if (!cuenta.getUserId().equals(userId)) {
            throw new SecurityException("No tienes permiso para acceder a esta cuenta");
        }

        return transaccionRepository.findByCuentaIdOrderByFechaDesc(cuentaId)
                .stream()
                .map(this::toTransaccionResponse)
                .toList();
    }

    public List<TransaccionResponse> obtenerActividad(Long cuentaId, Long userId) {
        if (cuentaId == null || cuentaId <= 0) {
            throw new IllegalArgumentException("El id de cuenta no es válido");
        }

        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada con id: " + cuentaId));

        if (!cuenta.getUserId().equals(userId)) {
            throw new SecurityException("No tienes permiso para acceder a esta cuenta");
        }

        return transaccionRepository.findByCuentaIdOrderByFechaDesc(cuentaId)
                .stream()
                .map(this::toTransaccionResponse)
                .toList();
    }

    public TransaccionResponse obtenerDetalleActividad(Long cuentaId, Long userId, Long transferId) {
        if (cuentaId == null || cuentaId <= 0) {
            throw new IllegalArgumentException("El id de cuenta no es válido");
        }

        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada con id: " + cuentaId));

        if (!cuenta.getUserId().equals(userId)) {
            throw new SecurityException("No tienes permiso para acceder a esta cuenta");
        }

        Transaccion transaccion = transaccionRepository.findByIdAndCuentaId(transferId, cuentaId)
                .orElseThrow(() -> new TransaccionNotFoundException("Actividad no encontrada con id: " + transferId));

        return toTransaccionResponse(transaccion);
    }

    public CuentaResponse crearCuenta(Long userId) {
        if (cuentaRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("El usuario ya tiene una cuenta asociada");
        }

        Cuenta cuenta = new Cuenta(userId, generarCVU(), generarAlias());
        cuentaRepository.save(cuenta);
        return toResponse(cuenta);
    }

    @Transactional
    public TransaccionResponse registrarIngreso(Long cuentaId, Long userId, Long cardId, BigDecimal monto) {
        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada con id: " + cuentaId));

        if (!cuenta.getUserId().equals(userId)) {
            throw new SecurityException("No tienes permiso para operar en esta cuenta");
        }

        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero");
        }

        Tarjeta tarjeta = tarjetaRepository.findByIdAndCuentaId(cardId, cuentaId)
                .orElseThrow(() -> new TarjetaNotFoundException("Tarjeta no encontrada con id: " + cardId));

        cuenta.setSaldo(cuenta.getSaldo().add(monto));
        cuentaRepository.save(cuenta);

        String descripcion = "Ingreso desde tarjeta " + tarjeta.getTipo().name().toLowerCase()
                + " terminada en " + tarjeta.getNumeroTarjeta().substring(tarjeta.getNumeroTarjeta().length() - 4);

        Transaccion transaccion = new Transaccion(cuenta, monto, TipoTransaccion.CREDITO, descripcion);
        transaccionRepository.save(transaccion);

        return toTransaccionResponse(transaccion);
    }

    public List<DestinatarioResponse> obtenerUltimosDestinatarios(Long cuentaId, Long userId) {
        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada con id: " + cuentaId));

        if (!cuenta.getUserId().equals(userId)) {
            throw new SecurityException("No tienes permiso para acceder a esta cuenta");
        }

        return transferenciaRepository.findByCuentaOrigenIdOrderByFechaDesc(cuentaId)
                .stream()
                .map(t -> new DestinatarioResponse(
                        t.getId(),
                        t.getDestinoCbuCvuAlias(),
                        t.getMonto(),
                        t.getFecha()))
                .toList();
    }

    @Transactional
    public TransaccionResponse realizarTransferencia(Long cuentaId, Long userId, String destination, BigDecimal monto) {
        Cuenta origen = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada con id: " + cuentaId));

        if (!origen.getUserId().equals(userId)) {
            throw new SecurityException("No tienes permiso para operar en esta cuenta");
        }

        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero");
        }

        Cuenta destino = cuentaRepository.findByCvu(destination)
                .or(() -> cuentaRepository.findByAlias(destination))
                .orElseThrow(() -> new CuentaNotFoundException("Cuenta destino no encontrada: " + destination));

        if (destino.getId().equals(origen.getId())) {
            throw new IllegalArgumentException("No podés transferirte dinero a vos mismo");
        }

        if (origen.getSaldo().compareTo(monto) < 0) {
            throw new FondosInsuficientesException("Saldo insuficiente para realizar la transferencia");
        }

        origen.setSaldo(origen.getSaldo().subtract(monto));
        destino.setSaldo(destino.getSaldo().add(monto));
        cuentaRepository.save(origen);
        cuentaRepository.save(destino);

        String descripcionDebito = "Transferencia enviada a " + destination;
        String descripcionCredito = "Transferencia recibida desde " + origen.getCvu();

        Transaccion debito = new Transaccion(origen, monto, TipoTransaccion.DEBITO, descripcionDebito);
        Transaccion credito = new Transaccion(destino, monto, TipoTransaccion.CREDITO, descripcionCredito);
        transaccionRepository.save(debito);
        transaccionRepository.save(credito);

        Transferencia transferencia = new Transferencia(origen, destination, destino, monto);
        transferenciaRepository.save(transferencia);

        return toTransaccionResponse(debito);
    }

    public CuentaResponse actualizarCuenta(Long cuentaId, Long userId, String nuevoAlias) {
        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada con id: " + cuentaId));

        if (!cuenta.getUserId().equals(userId)) {
            throw new SecurityException("No tienes permiso para modificar esta cuenta");
        }
        if (nuevoAlias == null || nuevoAlias.isBlank()) {
            throw new IllegalArgumentException("El alias no puede estar vacío");
        }
        if (cuentaRepository.existsByAlias(nuevoAlias)) {
            throw new IllegalArgumentException("El alias ya está en uso");
        }

        cuenta.setAlias(nuevoAlias);
        cuentaRepository.save(cuenta);
        return toResponse(cuenta);
    }

    private String generarCVU() {
        String cvu;
        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 22; i++) {
                sb.append(random.nextInt(10));
            }
            cvu = sb.toString();
        } while (cuentaRepository.existsByCvu(cvu));
        return cvu;
    }

    private String generarAlias() {
        String[] palabras = {"sol", "luna", "rio", "mar", "pez", "vez", "paz", "luz", "pan", "red"};
        String alias;
        do {
            alias = palabras[random.nextInt(palabras.length)] + "." +
                    palabras[random.nextInt(palabras.length)] + "." +
                    palabras[random.nextInt(palabras.length)];
        } while (cuentaRepository.existsByAlias(alias));
        return alias;
    }

    private CuentaResponse toResponse(Cuenta cuenta) {
        return new CuentaResponse(
                cuenta.getId(),
                cuenta.getUserId(),
                cuenta.getSaldo(),
                cuenta.getCvu(),
                cuenta.getAlias(),
                cuenta.getFechaCreacion()
        );
    }

    private TransaccionResponse toTransaccionResponse(Transaccion t) {
        return new TransaccionResponse(
                t.getId(),
                t.getMonto(),
                t.getTipo(),
                t.getDescripcion(),
                t.getFecha()
        );
    }
}
