package com.chezticket.identidade.conta.adapter.out.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

interface UsuarioJpaRepository extends JpaRepository<UsuarioJpaEntity, Long> {

    boolean existsByEmail(String email);

    Optional<UsuarioJpaEntity> findByEmail(String email);
}
