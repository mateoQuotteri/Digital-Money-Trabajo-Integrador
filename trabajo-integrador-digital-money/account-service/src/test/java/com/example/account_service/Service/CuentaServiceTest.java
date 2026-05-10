package com.example.account_service.Service;

import com.example.account_service.Dto.CuentaResponse;
import com.example.account_service.Dto.TransaccionResponse;
import com.example.account_service.Entity.Cuenta;
import com.example.account_service.Entity.TipoTransaccion;
import com.example.account_service.Entity.Transaccion;
import com.example.account_service.Exception.CuentaNotFoundException;
import com.example.account_service.Repository.CuentaRepository;
import com.example.account_service.Repository.TarjetaRepository;
import com.example.account_service.Repository.TransaccionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de CuentaService — Sprint 2
 * Cubre: TC-S2-005, TC-S2-006, TC-S2-007, TC-S2-008, TC-S2-009
 */
@ExtendWith(MockitoExtension.class)
class CuentaServiceTest {

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private TransaccionRepository transaccionRepository;

    @Mock
    private TarjetaRepository tarjetaRepository;

    @InjectMocks
    private CuentaService cuentaService;

    private Cuenta cuentaUsuario1;
    private Cuenta cuentaUsuario2;

    @BeforeEach
    void setUp() {
        cuentaUsuario1 = new Cuenta(1L, "1234567890123456789012", "sol.luna.rio");
        cuentaUsuario1.setId(1L);

        cuentaUsuario2 = new Cuenta(2L, "9876543210987654321098", "mar.pez.luz");
        cuentaUsuario2.setId(2L);
    }

    // -------------------------------------------------------------------------
    // TC-S2-005 — Editar alias de cuenta propia exitosamente
    // -------------------------------------------------------------------------

    @Test
    void tc_s2_005_actualizarAlias_exitoso() {
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaUsuario1));
        when(cuentaRepository.existsByAlias("nuevo.alias.prueba")).thenReturn(false);
        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuentaUsuario1);

        CuentaResponse response = cuentaService.actualizarCuenta(1L, 1L, "nuevo.alias.prueba");

        assertNotNull(response);
        assertEquals("nuevo.alias.prueba", cuentaUsuario1.getAlias());
        verify(cuentaRepository).save(cuentaUsuario1);
    }

    @Test
    void tc_s2_005_actualizarAlias_aliasYaEnUso_lanzaIllegalArgument() {
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaUsuario1));
        when(cuentaRepository.existsByAlias("alias.en.uso")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.actualizarCuenta(1L, 1L, "alias.en.uso"));
    }

    @Test
    void tc_s2_005_actualizarAlias_aliasVacio_lanzaIllegalArgument() {
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaUsuario1));

        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.actualizarCuenta(1L, 1L, ""));
    }

    // -------------------------------------------------------------------------
    // TC-S2-006 — Intentar editar alias de cuenta ajena
    // -------------------------------------------------------------------------

    @Test
    void tc_s2_006_actualizarAlias_cuentaAjena_lanzaSecurityException() {
        when(cuentaRepository.findById(2L)).thenReturn(Optional.of(cuentaUsuario2));

        // usuario1 (userId=1) intenta modificar la cuenta de usuario2 (userId=2)
        assertThrows(SecurityException.class,
                () -> cuentaService.actualizarCuenta(2L, 1L, "alias.hackeado"));
    }

    // -------------------------------------------------------------------------
    // TC-S2-007 — Ver historial de transacciones con registros existentes
    // -------------------------------------------------------------------------

    @Test
    void tc_s2_007_obtenerTransacciones_retornaLista() {
        Transaccion t1 = new Transaccion(cuentaUsuario1, new BigDecimal("500.00"), TipoTransaccion.CREDITO, "Ingreso desde tarjeta debito terminada en 1111");
        Transaccion t2 = new Transaccion(cuentaUsuario1, new BigDecimal("200.00"), TipoTransaccion.CREDITO, "Ingreso desde tarjeta credito terminada en 2222");

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaUsuario1));
        when(transaccionRepository.findByCuentaIdOrderByFechaDesc(1L)).thenReturn(List.of(t1, t2));

        List<TransaccionResponse> resultado = cuentaService.obtenerTransacciones(1L, 1L);

        assertEquals(2, resultado.size());
        verify(transaccionRepository).findByCuentaIdOrderByFechaDesc(1L);
    }

    // -------------------------------------------------------------------------
    // TC-S2-008 — Ver historial cuando la cuenta no tiene transacciones
    // -------------------------------------------------------------------------

    @Test
    void tc_s2_008_obtenerTransacciones_sinTransacciones_retornaListaVacia() {
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaUsuario1));
        when(transaccionRepository.findByCuentaIdOrderByFechaDesc(1L)).thenReturn(Collections.emptyList());

        List<TransaccionResponse> resultado = cuentaService.obtenerTransacciones(1L, 1L);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // -------------------------------------------------------------------------
    // TC-S2-009 — Intentar ver transacciones de cuenta ajena
    // -------------------------------------------------------------------------

    @Test
    void tc_s2_009_obtenerTransacciones_cuentaAjena_lanzaSecurityException() {
        when(cuentaRepository.findById(2L)).thenReturn(Optional.of(cuentaUsuario2));

        // usuario1 intenta ver transacciones de la cuenta de usuario2
        assertThrows(SecurityException.class,
                () -> cuentaService.obtenerTransacciones(2L, 1L));
    }

    @Test
    void tc_s2_009_obtenerTransacciones_cuentaInexistente_lanzaNotFoundException() {
        when(cuentaRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThrows(CuentaNotFoundException.class,
                () -> cuentaService.obtenerTransacciones(9999L, 1L));
    }
}
