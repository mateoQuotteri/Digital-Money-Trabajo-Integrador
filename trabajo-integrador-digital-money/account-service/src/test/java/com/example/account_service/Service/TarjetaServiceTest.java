package com.example.account_service.Service;

import com.example.account_service.Dto.PostTarjetaRequest;
import com.example.account_service.Dto.TarjetaResponse;
import com.example.account_service.Entity.Cuenta;
import com.example.account_service.Entity.Tarjeta;
import com.example.account_service.Entity.TipoTarjeta;
import com.example.account_service.Exception.CuentaNotFoundException;
import com.example.account_service.Exception.TarjetaNotFoundException;
import com.example.account_service.Repository.CuentaRepository;
import com.example.account_service.Repository.TarjetaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de TarjetaService — Sprint 2
 * Cubre: TC-S2-010 al TC-S2-021
 */
@ExtendWith(MockitoExtension.class)
class TarjetaServiceTest {

    @Mock
    private TarjetaRepository tarjetaRepository;

    @Mock
    private CuentaRepository cuentaRepository;

    @InjectMocks
    private TarjetaService tarjetaService;

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
    // TC-S2-010 — Agregar tarjeta de débito exitosamente
    // -------------------------------------------------------------------------

    @Test
    void tc_s2_010_crearTarjeta_debito_exitoso() {
        PostTarjetaRequest request = new PostTarjetaRequest();
        request.setNumeroTarjeta("4111111111111111");
        request.setTitular("Mateo Lopez");
        request.setFechaExpiracion("12/27");
        request.setTipo(TipoTarjeta.DEBITO);
        request.setMarca("VISA");

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaUsuario1));
        when(tarjetaRepository.existsByNumeroTarjeta("4111111111111111")).thenReturn(false);
        when(tarjetaRepository.save(any(Tarjeta.class))).thenAnswer(inv -> inv.getArgument(0));

        TarjetaResponse response = tarjetaService.crearTarjeta(1L, 1L, request);

        assertNotNull(response);
        assertEquals("4111111111111111", response.getNumeroTarjeta());
        assertEquals(TipoTarjeta.DEBITO, response.getTipo());
        assertEquals("VISA", response.getMarca());
        assertEquals("Mateo Lopez", response.getTitular());
    }

    // -------------------------------------------------------------------------
    // TC-S2-011 — Agregar tarjeta de crédito exitosamente
    // -------------------------------------------------------------------------

    @Test
    void tc_s2_011_crearTarjeta_credito_exitoso() {
        PostTarjetaRequest request = new PostTarjetaRequest();
        request.setNumeroTarjeta("5500005555555559");
        request.setTitular("Mateo Lopez");
        request.setFechaExpiracion("08/26");
        request.setTipo(TipoTarjeta.CREDITO);
        request.setMarca("MASTERCARD");

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaUsuario1));
        when(tarjetaRepository.existsByNumeroTarjeta("5500005555555559")).thenReturn(false);
        when(tarjetaRepository.save(any(Tarjeta.class))).thenAnswer(inv -> inv.getArgument(0));

        TarjetaResponse response = tarjetaService.crearTarjeta(1L, 1L, request);

        assertNotNull(response);
        assertEquals(TipoTarjeta.CREDITO, response.getTipo());
        assertEquals("MASTERCARD", response.getMarca());
    }

    // -------------------------------------------------------------------------
    // TC-S2-012 y TC-S2-013 — Validaciones de formato (Bean Validation)
    // Nota: @Pattern y @NotBlank en PostTarjetaRequest son procesados por Spring
    // Validation antes de llegar al servicio. Se validan en nivel de controller/
    // integración. A nivel de servicio se valida la lógica de negocio.
    // -------------------------------------------------------------------------

    @Test
    void tc_s2_012_y_013_crearTarjeta_expirada_lanzaIllegalArgument() {
        PostTarjetaRequest request = new PostTarjetaRequest();
        request.setNumeroTarjeta("4111111111111111");
        request.setTitular("Mateo Lopez");
        request.setFechaExpiracion("01/20"); // fecha ya vencida
        request.setTipo(TipoTarjeta.DEBITO);
        request.setMarca("VISA");

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaUsuario1));
        when(tarjetaRepository.existsByNumeroTarjeta("4111111111111111")).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> tarjetaService.crearTarjeta(1L, 1L, request));
    }

    // -------------------------------------------------------------------------
    // TC-S2-014 — Agregar tarjeta duplicada (mismo número ya registrado)
    // -------------------------------------------------------------------------

    @Test
    void tc_s2_014_crearTarjeta_duplicada_lanzaIllegalState() {
        PostTarjetaRequest request = new PostTarjetaRequest();
        request.setNumeroTarjeta("4111111111111111");
        request.setTitular("Mateo Lopez");
        request.setFechaExpiracion("12/27");
        request.setTipo(TipoTarjeta.DEBITO);
        request.setMarca("VISA");

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaUsuario1));
        when(tarjetaRepository.existsByNumeroTarjeta("4111111111111111")).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> tarjetaService.crearTarjeta(1L, 1L, request));
    }

    // -------------------------------------------------------------------------
    // TC-S2-015 — Listar todas las tarjetas de la cuenta propia
    // -------------------------------------------------------------------------

    @Test
    void tc_s2_015_obtenerTarjetas_retornaLista() {
        Tarjeta tarjeta1 = new Tarjeta(cuentaUsuario1, "4111111111111111", "Mateo Lopez",
                LocalDate.of(2027, 12, 31), TipoTarjeta.DEBITO, "VISA");
        Tarjeta tarjeta2 = new Tarjeta(cuentaUsuario1, "5500005555555559", "Mateo Lopez",
                LocalDate.of(2026, 8, 31), TipoTarjeta.CREDITO, "MASTERCARD");

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaUsuario1));
        when(tarjetaRepository.findByCuentaId(1L)).thenReturn(List.of(tarjeta1, tarjeta2));

        List<TarjetaResponse> resultado = tarjetaService.obtenerTarjetas(1L, 1L);

        assertEquals(2, resultado.size());
    }

    // -------------------------------------------------------------------------
    // TC-S2-016 — Listar tarjetas cuando la cuenta no tiene tarjetas
    // -------------------------------------------------------------------------

    @Test
    void tc_s2_016_obtenerTarjetas_sinTarjetas_retornaListaVacia() {
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaUsuario1));
        when(tarjetaRepository.findByCuentaId(1L)).thenReturn(Collections.emptyList());

        List<TarjetaResponse> resultado = tarjetaService.obtenerTarjetas(1L, 1L);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // -------------------------------------------------------------------------
    // TC-S2-017 — Ver tarjeta específica por ID
    // -------------------------------------------------------------------------

    @Test
    void tc_s2_017_obtenerTarjeta_porId_exitoso() {
        Tarjeta tarjeta = new Tarjeta(cuentaUsuario1, "4111111111111111", "Mateo Lopez",
                LocalDate.of(2027, 12, 31), TipoTarjeta.DEBITO, "VISA");
        tarjeta.setId(1L);

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaUsuario1));
        when(tarjetaRepository.findByIdAndCuentaId(1L, 1L)).thenReturn(Optional.of(tarjeta));

        TarjetaResponse response = tarjetaService.obtenerTarjeta(1L, 1L, 1L);

        assertNotNull(response);
        assertEquals("4111111111111111", response.getNumeroTarjeta());
        assertEquals(TipoTarjeta.DEBITO, response.getTipo());
        assertEquals("VISA", response.getMarca());
    }

    // -------------------------------------------------------------------------
    // TC-S2-018 — Ver tarjeta con ID inexistente
    // -------------------------------------------------------------------------

    @Test
    void tc_s2_018_obtenerTarjeta_idInexistente_lanzaNotFoundException() {
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaUsuario1));
        when(tarjetaRepository.findByIdAndCuentaId(999L, 1L)).thenReturn(Optional.empty());

        assertThrows(CuentaNotFoundException.class,
                () -> tarjetaService.obtenerTarjeta(1L, 999L, 1L));
    }

    // -------------------------------------------------------------------------
    // TC-S2-019 — Eliminar tarjeta existente exitosamente
    // -------------------------------------------------------------------------

    @Test
    void tc_s2_019_eliminarTarjeta_exitoso() {
        Tarjeta tarjeta = new Tarjeta(cuentaUsuario1, "4111111111111111", "Mateo Lopez",
                LocalDate.of(2027, 12, 31), TipoTarjeta.DEBITO, "VISA");
        tarjeta.setId(1L);

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaUsuario1));
        when(tarjetaRepository.findByIdAndCuentaId(1L, 1L)).thenReturn(Optional.of(tarjeta));

        assertDoesNotThrow(() -> tarjetaService.eliminarTarjeta(1L, 1L, 1L));
        verify(tarjetaRepository).delete(tarjeta);
    }

    // -------------------------------------------------------------------------
    // TC-S2-020 — Intentar eliminar tarjeta con ID inexistente
    // -------------------------------------------------------------------------

    @Test
    void tc_s2_020_eliminarTarjeta_idInexistente_lanzaNotFoundException() {
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaUsuario1));
        when(tarjetaRepository.findByIdAndCuentaId(999L, 1L)).thenReturn(Optional.empty());

        assertThrows(TarjetaNotFoundException.class,
                () -> tarjetaService.eliminarTarjeta(1L, 999L, 1L));
    }

    // -------------------------------------------------------------------------
    // TC-S2-021 — Intentar eliminar tarjeta de otra cuenta
    // -------------------------------------------------------------------------

    @Test
    void tc_s2_021_eliminarTarjeta_cuentaAjena_lanzaSecurityException() {
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaUsuario1));

        // usuario2 (userId=2) intenta eliminar una tarjeta de la cuenta de usuario1
        assertThrows(SecurityException.class,
                () -> tarjetaService.eliminarTarjeta(1L, 1L, 2L));
    }
}
