import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { CrearUsuarioRequest, UsuarioResponse } from '../models/usuario-model';


@Injectable({ providedIn: 'root' })
export class UsuariosService {
  private api = `${environment.apiBaseUrl}`;

  constructor(private http: HttpClient) {}

  crear(req: CrearUsuarioRequest): Observable<UsuarioResponse> {
    return this.http.post<UsuarioResponse>(`${this.api}/usuarios`, req);
  }

  listar(): Observable<UsuarioResponse[]> {
    return this.http.get<UsuarioResponse[]>(`${this.api}/usuarios`);
  }
}
