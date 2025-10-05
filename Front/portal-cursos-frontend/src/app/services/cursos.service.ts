import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { CursoListadoItemDto, ListarCursosResponse } from '../models/curso-listado.model';
import { map, Observable } from 'rxjs';
import { CrearCursoRequest, CursoResponse } from '../models/curso.model';

@Injectable({ providedIn: 'root' })
export class CursosService {
  private api = `${environment.apiBaseUrl}`;

  constructor(private http: HttpClient) {}

  listarDisponibles(): Observable<CursoListadoItemDto[]> {
    return this.http.get<CursoListadoItemDto[]>(`${this.api}/cursos/disponibles`);
  }

  iniciarCurso(cursoId: number) {
    return this.http.post(`${this.api}/cursos/${cursoId}/iniciar`, {});
  }

  listarCursos():Observable<ListarCursosResponse>{
    return this.http
      .get<ListarCursosResponse>(`${this.api}/cursos/listar`);
  }

  crearCurso(payload: CrearCursoRequest): Observable<CursoResponse> {
    return this.http
      .post<CursoResponse>(`${this.api}/cursos`, payload);
  }
}
