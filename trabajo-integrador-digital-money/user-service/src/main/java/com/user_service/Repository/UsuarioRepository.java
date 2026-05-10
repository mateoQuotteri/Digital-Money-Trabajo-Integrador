package com.user_service.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import com.user_service.Entity.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Buscar usuario por email para login
    Optional<Usuario> findByEmail(String email);

    // Verificar si existe usuario con email específico
    boolean existsByEmail(String email);

    // Verificar si existe usuario con DNI específico
    boolean existsByDni(String dni);

    // Buscar usuarios activos por email
    @Query("SELECT u FROM Usuario u WHERE u.email = ?1 AND u.activo = true")
    Optional<Usuario> findByEmailAndActivo(String email);

    // MÉTODOS FALTANTES para el UsuarioService:

    // Verificar si existe usuario con CVU específico
    boolean existsByCvu(String cvu);

    // Verificar si existe usuario con alias específico
    boolean existsByAlias(String alias);
}