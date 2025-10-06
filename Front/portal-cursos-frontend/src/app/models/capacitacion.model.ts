
export interface CapacitacionResponse {
    id: number;
    titulo: string;
    descripcion?: string | null;
    tipo: string;
    url: string;
    duracionMinutos?: number | null;
    orden?: number | null;
    fechaCreacion: string;
    cursoId: number;
  }
  
  export interface CrearCapacitacionUploadRequest {
    cursoId: number;
    titulo: string;
    descripcion?: string;
    tipo: string;
    duracionMinutos?: number;
    orden?: number;
    archivo: File;
  }
  
  /** Convierte el request a FormData para enviar como multipart/form-data */
  export function toFormDataCapacitacion(req: CrearCapacitacionUploadRequest): FormData {
    const fd = new FormData();
    fd.append('cursoId', String(req.cursoId));
    fd.append('titulo', req.titulo);
    if (req.descripcion != null) fd.append('descripcion', req.descripcion);
    fd.append('tipo', req.tipo);
    if (req.duracionMinutos != null) fd.append('duracionMinutos', String(req.duracionMinutos));
    if (req.orden != null) fd.append('orden', String(req.orden));
    fd.append('archivo', req.archivo);
    return fd;
  }

  export interface IniciarCapacitacionResponse {
    cursoId: number;
    capacitacionId: number;
    titulo: string;
    tipo: string;
    estado: string;
    url: string;
    expiraSegundos: number;
  }

  export interface FinalizarResponse {
    cursoId: number;
    capacitacionId: number;
    cursoCompletado: boolean;
    insigniaEmitida: boolean;
  }
  

  