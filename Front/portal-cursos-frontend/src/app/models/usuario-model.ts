export interface CrearUsuarioRequest {
  correo: string;
  nombre: string;
  clave: string;
  rol: 'ADMIN' | 'USUARIO';
}

export interface UsuarioResponse {
  id: number;
  correo: string;
  nombre: string;
  rol: 'ADMIN' | 'USUARIO';
  fechaCreacion: string;
}
