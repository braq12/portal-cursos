export interface CrearCursoForm {
    titulo: string;
    descripcion?: string | null;
    categoria?: string | null;
    insignia?: File | null;
  }

  
  export interface CursoResponse {
    id: number;
    titulo: string;
    descripcion: string;
    categoria: string;
    fechaCreacion: string;
  }

  export interface CapacitacionResumenDto {
    id: number;
    titulo: string;
    descripcion: string;
    tipo: string;
    duracionMinutos: number;
    orden: number;
    iniciado: boolean;
    estado: string;
  }
  
  export interface IniciarCursoResponse {
    cursoId: number;
    yaExistia: boolean;
    estadoCurso: string;
    totalCapacitaciones: number;
    titulo:string;
    descripcion:string;
    categoria:string;
    capacitaciones: CapacitacionResumenDto[];
  }
  