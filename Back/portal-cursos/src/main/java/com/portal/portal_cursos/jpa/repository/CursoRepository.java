package com.portal.portal_cursos.jpa.repository;



import com.portal.portal_cursos.dtos.curso.ListarCursosResponse;
import com.portal.portal_cursos.jpa.entity.CursoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CursoRepository extends JpaRepository<CursoEntity, Long> {

    @Query("""
           select new com.portal.portal_cursos.dtos.curso.ListarCursosResponse(
               c.id, c.titulo, c.descripcion
           )
           from CursoEntity c
           order by c.titulo asc
           """)
    List<ListarCursosResponse> listarCursosOrdenados();

}

