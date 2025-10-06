import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { TagModule } from 'primeng/tag';
import { RippleModule } from 'primeng/ripple';
import { MessageService } from 'primeng/api';

import { CursosService } from '../../../services/cursos.service';
import { CursoListadoItemDto, CapacitacionItemDto } from '../../../models/curso-listado.model';

import { catchError, finalize, of, tap } from 'rxjs';
import { IniciarCursoResponse } from '../../../models/curso.model';
import { ImageModule } from 'primeng/image';

@Component({
  selector: 'app-cursos-usuario',
  standalone: true,
  imports: [CommonModule, TableModule, ButtonModule, DialogModule, TagModule, RippleModule,ImageModule],
  templateUrl: './cursos.component.html'
})
export class CursosUsuarioComponent implements OnInit {
  private cursosSvc = inject(CursosService);
  private msg = inject(MessageService);
  private router = inject(Router);

  loading = false;

  cursos: CursoListadoItemDto[] = [];

  verCapsVisible = false;
  cursoSeleccionado: CursoListadoItemDto | null = null;
  caps: CapacitacionItemDto[] = [];

  verInsigniaVisible = false;
  insigniaUrl: string | null = null;

  ngOnInit(): void {
    this.cargarCursos();
  }

  cargarCursos() {
    this.loading = true;
    this.cursosSvc.listarDisponibles()
      .pipe(
        tap(res => this.cursos = res ?? []),
        catchError(() => {
          this.msg.add({ severity: 'error', summary: 'Error', detail: 'No se pudieron cargar los cursos.' });
          return of([]);
        }),
        finalize(() => this.loading = false)
      )
      .subscribe();
  }

  abrirCapacitaciones(curso: CursoListadoItemDto) {
    this.cursoSeleccionado = curso;
    this.caps = curso.capacitaciones ?? [];
    this.verCapsVisible = true;
  }

  getAccionLabel(curso: CursoListadoItemDto) {
    return curso.iniciado ? 'Continuar' : 'Iniciar';
  }

  getEstadoSeverity(estado: string | null | undefined) {
    const v = (estado || '').toUpperCase();
  
    switch (v) {
      case 'PENDIENTE':
        return 'info';
      case 'EN PROGRESO': 
        return 'warn';
      case 'COMPLETADO':
        return 'success';
      default:
        return 'info';
    }
  }
  

  iniciarOContinuar(curso: CursoListadoItemDto) {
    this.cursosSvc.iniciarCurso(curso.id).subscribe({
      next: (res: IniciarCursoResponse) => {
        curso.iniciado = true;
  
        this.router.navigate(['/curso', curso.id], {
          state: { data: res } 
        });
      },
      error: () => {
        this.msg.add({ severity: 'error', summary: 'Error', detail: 'No se pudo abrir el curso.' });
      }
    });
}


verInsignia(curso: CursoListadoItemDto) {
  if (!curso?.urlInsignia) return;
  this.insigniaUrl = curso.urlInsignia;
  this.verInsigniaVisible = true;
}
}