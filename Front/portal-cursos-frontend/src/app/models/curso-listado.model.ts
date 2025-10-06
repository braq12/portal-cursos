export interface CapacitacionItemDto {
    id: number;
    titulo: string;
    descripcion: string;
    tipo: string;
    estado: string;
    completada: boolean;
  }
  
  export interface CursoListadoItemDto {
    id: number;
    titulo: string;
    descripcion: string;
    categoria: string;
    cantidadCapacitaciones: number;
    iniciado: boolean;
    estadoCurso: string;
    tieneInsignia: boolean;
    url:string;
    urlInsignia:string;
    capacitaciones: CapacitacionItemDto[];
  }

  export interface ListarCursosResponse{
    idCurso:number;
    titulo:string;
    descripcion:string;

  }
  
  