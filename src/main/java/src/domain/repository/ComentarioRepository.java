package src.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import src.domain.model.Comentario;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long>{

}
