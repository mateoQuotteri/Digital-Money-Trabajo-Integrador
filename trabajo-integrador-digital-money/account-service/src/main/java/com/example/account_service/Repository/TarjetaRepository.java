package com.example.account_service.Repository;

import com.example.account_service.Entity.Tarjeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TarjetaRepository extends JpaRepository<Tarjeta, Long> {

    List<Tarjeta> findByCuentaId(Long cuentaId);

    Optional<Tarjeta> findByIdAndCuentaId(Long id, Long cuentaId);

    boolean existsByNumeroTarjeta(String numeroTarjeta);
}
