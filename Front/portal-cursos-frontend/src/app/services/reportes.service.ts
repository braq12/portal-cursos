import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { DetalleCursosUsuarioDto, ReporteCursosUsuarioDto } from '../models/reportes-model';


@Injectable({ providedIn: 'root' })
export class ReportesService {
  private api = `${environment.apiBaseUrl}`;

  constructor(private http: HttpClient) {}

  cursosPorUsuario(): Observable<ReporteCursosUsuarioDto[]> {
    return this.http.get<ReporteCursosUsuarioDto[]>(
      `${this.api}/reportes/cursos-por-usuario`
    );
  }

  detalleCursosPorUsuario(): Observable<DetalleCursosUsuarioDto[]> {
    return this.http.get<DetalleCursosUsuarioDto[]>(
      `${this.api}/reportes/cursos-por-usuario/detalle`
    );
  }
}
