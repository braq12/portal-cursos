import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReportesService } from '../../../services/reportes.service';


import { CardModule } from 'primeng/card';
import { ChartModule } from 'primeng/chart';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { ToastModule } from 'primeng/toast';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { MessageService } from 'primeng/api';
import { ToolbarModule } from 'primeng/toolbar';
import { ButtonModule } from 'primeng/button';
import { DetalleCursosUsuarioDto, ReporteCursosUsuarioDto } from '../../../models/reportes-model';

@Component({
  selector: 'app-reporte-cursos',
  standalone: true,
  imports: [
    CommonModule,
    CardModule, ChartModule,
    TableModule, TagModule,
    ToastModule, ProgressSpinnerModule,
    ToolbarModule, ButtonModule
  ],
  templateUrl: './reporte-cursos.component.html'
})
export class ReporteCursosComponent implements OnInit {
  private svc = inject(ReportesService);
  private msg = inject(MessageService);

  loading = signal(true);
  loadingDetalle = signal(true);

  resumen = signal<ReporteCursosUsuarioDto[]>([]);
  detalle = signal<DetalleCursosUsuarioDto[]>([]);

  // Chart
  chartData: any;
  chartOptions: any;

  ngOnInit(): void {
    this.fetchResumen();
    this.fetchDetalle();
    this.configureChartOptions();
  }

  private fetchResumen() {
    this.loading.set(true);
    this.svc.cursosPorUsuario().subscribe({
      next: (data) => {
        this.resumen.set(data ?? []);
        this.buildChartData();
        this.loading.set(false);
      },
      error: () => {
        this.msg.add({ severity: 'error', summary: 'Error', detail: 'No se pudo cargar el resumen.' });
        this.loading.set(false);
      }
    });
  }

  private fetchDetalle() {
    this.loadingDetalle.set(true);
    this.svc.detalleCursosPorUsuario().subscribe({
      next: (data) => {
        this.detalle.set(data ?? []);
        this.loadingDetalle.set(false);
      },
      error: () => {
        this.msg.add({ severity: 'error', summary: 'Error', detail: 'No se pudo cargar el detalle.' });
        this.loadingDetalle.set(false);
      }
    });
  }

  private buildChartData() {
    const labels = this.resumen().map(r => r.usuario);
    const completados = this.resumen().map(r => r.completados);
    const enProgreso  = this.resumen().map(r => r.enProgreso);
    const pendientes  = this.resumen().map(r => r.pendientes);

    this.chartData = {
      labels,
      datasets: [
        { label: 'Completados', data: completados, backgroundColor: '#22C55E' },
        { label: 'En Progreso', data: enProgreso,  backgroundColor: '#F59E0B' },
        { label: 'Pendientes',  data: pendientes,  backgroundColor: '#94A3B8' }
      ]
    };
  }

  private configureChartOptions() {
    this.chartOptions = {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { position: 'bottom' },
        tooltip: { mode: 'index', intersect: false }
      },
      scales: {
        x: { stacked: false, ticks: { autoSkip: false, maxRotation: 45, minRotation: 0 } },
        y: { beginAtZero: true, precision: 0 }
      }
    };
  }

  // Helpers Tabla
  getSeverity(estado: string) {
    switch (estado) {
      case 'COMPLETADO': return 'success';
      case 'EN PROGRESO': return 'warn';
      default: return 'secondary';
    }
  }

  // Agrupar por usuario para rowGroup
  getDetalleOrdenado(): DetalleCursosUsuarioDto[] {
    return [...this.detalle()].sort((a,b) =>
      a.usuario.localeCompare(b.usuario) || a.curso.localeCompare(b.curso)
    );
  }

  recargar() {
    this.fetchResumen();
    this.fetchDetalle();
  }
}
