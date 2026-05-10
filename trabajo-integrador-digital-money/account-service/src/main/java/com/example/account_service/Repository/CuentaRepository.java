package com.example.account_service.Repository;

import com.example.account_service.Entity.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    Optional<Cuenta> findByUserId(Long userId);
    Optional<Cuenta> findByCvu(String cvu);
    Optional<Cuenta> findByAlias(String alias);
    boolean existsByUserId(Long userId);
    boolean existsByCvu(String cvu);
    boolean existsByAlias(String alias);
}
