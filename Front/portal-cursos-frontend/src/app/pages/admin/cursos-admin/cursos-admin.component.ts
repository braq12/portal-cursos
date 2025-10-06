import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { CursosService } from '../../../services/cursos.service';
import { CursoListadoItemDto } from '../../../models/curso-listado.model';
import { RippleModule } from 'primeng/ripple';
import { catchError, finalize, of, tap } from 'rxjs';



@Component({
  selector: 'app-cursos-admin',
  standalone: true,
  imports: [CommonModule, TableModule, ButtonModule, TagModule, ToastModule, RippleModule ],
  templateUrl: './cursos-admin.component.html'
})
export class CursosAdminComponent implements OnInit {
  private cursosSvc = inject(CursosService);
  private msg = inject(MessageService);

  cursos: CursoListadoItemDto[] = [];
  loading = true;

  /** PrimeNG >=16: usa expandedRowKeys en lugar de expandedRows */
  expandedRows: Record<number, boolean> = {};

  ngOnInit(): void {
    this.cargar();
  }

  cargar() {
    this.loading = true;
    this.cursosSvc.listarDisponibles()
      .pipe(
        tap(res => {
          this.cursos = (res ?? []).map(c => ({
            ...c,
            capacitaciones: Array.isArray(c.capacitaciones) ? c.capacitaciones : []
          }));
          this.expandedRows = {}; // reset
        }),
        catchError(() => {
          this.msg.add({severity:'error', summary:'Error', detail:'No se pudieron cargar los cursos.'});
          return of([]);
        }),
        finalize(() => this.loading = false)
      )
      .subscribe();
  }

  expandAll() {
    this.expandedRows = {};
    for (const c of this.cursos) this.expandedRows[c.id] = true;
  }

  collapseAll() {
    this.expandedRows = {};
  }

  tagCursoSeverity(estado: string) {
    // Ajusta a tus valores reales de estadoCurso
    switch ((estado || '').toUpperCase()) {
      case 'ACTIVO': return 'success';
      case 'PAUSADO': return 'warn';
      case 'INACTIVO': return 'danger';
      default: return 'info';
    }
  }

  tagCapCompletada(b: boolean) {
    return b ? 'success' : 'warning';
  }

}
