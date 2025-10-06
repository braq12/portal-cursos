export interface ReporteCursosUsuarioDto {
    usuarioId: number;
    usuario: string;
    completados: number;
    enProgreso: number;
    pendientes: number;
  }
  
  export interface DetalleCursosUsuarioDto {
    usuarioId: number;
    usuario: string;
    cursoId: number;
    curso: string;
    estado: 'COMPLETADO' | 'EN PROGRESO' | 'PENDIENTE';
  }