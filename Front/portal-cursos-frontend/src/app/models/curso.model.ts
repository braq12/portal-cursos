export interface CrearCursoRequest {
    titulo: string;
    descripcion: string;
    categoria: string;
  }
  
  export interface CursoResponse {
    id: number;
    titulo: string;
    descripcion: string;
    categoria: string;
    fechaCreacion: string;
  }