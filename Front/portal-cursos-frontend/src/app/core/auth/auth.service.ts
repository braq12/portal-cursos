import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

type JwtPayload = { sub: string; correo: string; rol: 'ADMIN'|'USUARIO'; exp: number };

@Injectable({ providedIn: 'root' })
export class AuthService {
  private api = `${environment.apiBaseUrl}/auth`;

  constructor(private http: HttpClient) {}

  login(correo: string, clave: string) {
    return this.http.post<{token:string}>(`${this.api}/login`, { correo, clave });
  }

  saveToken(t: string) { localStorage.setItem('token', t); }
  logout() { localStorage.removeItem('token'); }

  get token(): string | null { return localStorage.getItem('token'); }

  get payload(): JwtPayload | null {
    const t = this.token; if (!t) return null;
    try {
      const [, payloadB64] = t.split('.');
      const json = atob(payloadB64.replace(/-/g, '+').replace(/_/g, '/'));
      return JSON.parse(json) as JwtPayload;
    } catch { return null; }
  }

  isLoggedIn(): boolean {
    const p = this.payload; if (!p) return false;
    return Date.now()/1000 < p.exp;
  }

  role(): 'ADMIN'|'USUARIO'|null {
    return this.payload?.rol ?? null;
  }
}
