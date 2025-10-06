import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { CursoListadoItemDto, ListarCursosResponse } from '../models/curso-listado.model';
import { map, Observable } from 'rxjs';
import {  CursoResponse, IniciarCursoResponse } from '../models/curso.model';

@Injectable({ providedIn: 'root' })
export class CursosService {
  private api = `${environment.apiBaseUrl}`;

  constructor(private http: HttpClient) {}

  listarDisponibles(): Observable<CursoListadoItemDto[]> {
    return this.http.get<CursoListadoItemDto[]>(`${this.api}/cursos/disponibles`);
  }

  iniciarCurso(cursoId: number):Observable<IniciarCursoResponse> {
    return this.http.post<IniciarCursoResponse>(`${this.api}/cursos/${cursoId}/iniciar`, {});
  }

  listarCursos():Observable<ListarCursosResponse>{
    return this.http
      .get<ListarCursosResponse>(`${this.api}/cursos/listar`);
  }

  crearCursoConInsignia(form: {
    titulo: string;
    descripcion?: string | null;
    categoria?: string | null;
    insignia?: File | null;
  }): Observable<CursoResponse> {
    const fd = new FormData();
    fd.append('titulo', form.titulo);
    if (form.descripcion) fd.append('descripcion', form.descripcion);
    if (form.categoria) fd.append('categoria', form.categoria);
    if (form.insignia)  fd.append('insignia', form.insignia);

    return this.http.post<CursoResponse>(`${this.api}/cursos`, fd);
  }
}
