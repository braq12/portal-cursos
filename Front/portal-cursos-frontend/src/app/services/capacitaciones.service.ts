import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { CapacitacionResponse, FinalizarResponse, IniciarCapacitacionResponse } from '../models/capacitacion.model';

@Injectable({
  providedIn: 'root'
})
export class CapacitacionesService {
  private api = `${environment.apiBaseUrl}`;

  constructor(private http: HttpClient ) {}

  iniciarCapacitacion(capId: number):Observable<IniciarCapacitacionResponse> {
    return this.http.post<IniciarCapacitacionResponse>(`${this.api}/capacitaciones/${capId}/iniciar`, {});
  }

  finalizarCapacitacion(capId: number):Observable<FinalizarResponse > {
    return this.http.post<FinalizarResponse>(`${this.api}/capacitaciones/${capId}/finalizar`, {});
  }



  // subir capacitación (admin) — multipart
  cargarCapacitacion(formData: FormData) : Observable<CapacitacionResponse>{
    return this.http.post<CapacitacionResponse>(`${this.api}/capacitaciones/upload`, formData);
  }
}
