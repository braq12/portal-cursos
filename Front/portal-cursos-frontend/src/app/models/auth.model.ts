

export interface LoginPayload  {
    correo: string;
    clave:  string;
  }
  
  export type Rol = 'ADMIN' | 'USUARIO';
  
  export interface LoginResponse {
    token: string;
    rol?: Rol; 
  }
  
  export interface UserSession {
    token: string;
    rol: Rol;
  }