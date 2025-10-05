import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { CapacitacionResponse } from '../models/capacitacion.model';

@Injectable({
  providedIn: 'root'
})
export class CapacitacionesService {
  private api = `${environment.apiBaseUrl}`;

  constructor(private http: HttpClient ) {}

  iniciarCapacitacion(capId: number, min = 5) {
    return this.http.post(`${this.api}/capacitaciones/${capId}/play?min=${min}`, {});
  }

  // subir capacitación (admin) — multipart
  cargarCapacitacion(formData: FormData) : Observable<CapacitacionResponse>{
    return this.http.post<CapacitacionResponse>(`${this.api}/capacitaciones/upload`, formData);
  }
}
