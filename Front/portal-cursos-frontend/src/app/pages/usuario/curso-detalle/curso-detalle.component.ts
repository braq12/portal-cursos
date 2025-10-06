import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';

import { CardModule } from 'primeng/card';
import { StepsModule } from 'primeng/steps';
import { ProgressBarModule } from 'primeng/progressbar';
import { TagModule } from 'primeng/tag';
import { ButtonModule } from 'primeng/button';
import { DividerModule } from 'primeng/divider';
import { TooltipModule } from 'primeng/tooltip';
import { ToastModule } from 'primeng/toast';
import { MessageService, MenuItem } from 'primeng/api';

import { IniciarCursoResponse, CapacitacionResumenDto } from '../../../models/curso.model';
import { CursosService } from '../../../services/cursos.service';
import { CapacitacionesService } from '../../../services/capacitaciones.service';

@Component({
  selector: 'app-curso-detalle',
  standalone: true,
  imports: [
    CommonModule,
    CardModule, StepsModule, ProgressBarModule, TagModule, ButtonModule, DividerModule,
    TooltipModule, ToastModule
  ],
  templateUrl: './curso-detalle.component.html'
})
export class CursoDetalleComponent implements OnInit {
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private cursosSvc = inject(CursosService);
  private capsSvc = inject(CapacitacionesService);
  private msg = inject(MessageService);

  loading = signal(false);

  // Datos del curso
  curso?: IniciarCursoResponse;

  // Steps
  steps: MenuItem[] = [];
  activeIndex = 0;

  // Derivados
  get sel(): CapacitacionResumenDto | undefined {
    return this.curso?.capacitaciones?.[this.activeIndex];
  }

  get progreso(): number {
    if (!this.curso) return 0;
    const total = this.curso.totalCapacitaciones || this.curso.capacitaciones.length || 1;
    const hechas = this.curso.capacitaciones.filter(c => c.estado === 'COMPLETADO').length;
    return Math.round((hechas / total) * 100);
  }

  ngOnInit(): void {
    // 1) Intentamos leer los datos enviados por navegación (state)
    const state = history.state?.data as IniciarCursoResponse | undefined;
    if (state?.capacitaciones?.length) {
      this.setCurso(state);
      return;
    }

    // 2) Si no vino por state, intentamos con el :id de la ruta y llamamos iniciarCurso(id)
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.loading.set(true);
      this.cursosSvc.iniciarCurso(id).subscribe({
        next: (res) => this.setCurso(res),
        error: () => {
          this.msg.add({severity:'error', summary:'Error', detail:'No se pudo cargar el curso.'});
          this.router.navigate(['/cursos']);
        },
        complete: () => this.loading.set(false)
      });
    }
  }

  private setCurso(c: IniciarCursoResponse) {
    this.curso = c;
    // construir steps
    this.steps = (c.capacitaciones || [])
      .sort((a, b) => (a.orden ?? 0) - (b.orden ?? 0))
      .map(cap => ({
        label: cap.titulo.length > 22 ? cap.titulo.slice(0, 22) + '…' : cap.titulo,
        tooltip: cap.titulo
      }));

    // posicionarse en la primera no completada (o la primera)
    const idxNoCompletada = c.capacitaciones.findIndex(x => x.estado !== 'COMPLETADA');
    this.activeIndex = idxNoCompletada >= 0 ? idxNoCompletada : 0;
  }

  // Acción principal
  reproducir() {
    if (!this.sel) return;
    this.loading.set(true);
    this.capsSvc.iniciarCapacitacion(this.sel.id).subscribe({
      next: () => {
        this.msg.add({severity:'success', summary:'Listo', detail:'Capacitación iniciada.'});
        this.router.navigate(['/capacitacion', this.sel!.id]); 
      },
      error: () => this.msg.add({severity:'error', summary:'Error', detail:'No se pudo iniciar.'}),
      complete: () => this.loading.set(false)
    });
  }

  // Helpers UI
  estadoSeverity(estado?: string) {
    switch (estado) {
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

  tipoSeverity(tipo?: string) {
    switch (tipo) {
      case 'VIDEO': return 'info';
      case 'DOCUMENTO': return 'secondary';
      default: return 'contrast';
    }
  }
}
