package com.example.account_service.Repository;

import com.example.account_service.Entity.Transferencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferenciaRepository extends JpaRepository<Transferencia, Long> {
    List<Transferencia> findByCuentaOrigenIdOrderByFechaDesc(Long cuentaOrigenId);
}
