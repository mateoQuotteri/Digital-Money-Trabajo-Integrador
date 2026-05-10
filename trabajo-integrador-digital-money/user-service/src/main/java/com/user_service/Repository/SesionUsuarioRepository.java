package com.user_service.Repository;

import com.user_service.Entity.SesionUsuario;
import com.user_service.Entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SesionUsuarioRepository extends JpaRepository<SesionUsuario, Long> {

    // Buscar sesión por hash de token para validación JWT
    Optional<SesionUsuario> findByTokenHash(String tokenHash);

    // Buscar sesiones activas de un usuario específico
    List<SesionUsuario> findByUsuarioAndActivaTrue(Usuario usuario);

    // Invalidar todas las sesiones de un usuario (logout completo)
    @Modifying
    @Transactional
    @Query("UPDATE SesionUsuario s SET s.activa = false WHERE s.usuario = ?1")
    void invalidarSesionesPorUsuario(Usuario usuario);

    // Limpiar sesiones expiradas automáticamente
    @Modifying
    @Transactional
    @Query("DELETE FROM SesionUsuario s WHERE s.fechaExpiracion < ?1")
    void eliminarSesionesExpiradas(LocalDateTime fecha);

    // Verificar si existe una sesión activa con un token específico
    boolean existsByTokenHashAndActivaTrue(String tokenHash);

    // Encontrar sesión activa y válida por hash
    @Query("SELECT s FROM SesionUsuario s WHERE s.tokenHash = ?1 AND s.activa = true AND s.fechaExpiracion > ?2")
    Optional<SesionUsuario> findSesionValidaPorToken(String tokenHash, LocalDateTime fechaActual);
}