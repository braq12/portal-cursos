// src/app/pages/usuario/capacitacion-play/capacitacion-play.component.ts
import { Component, OnDestroy, OnInit, ViewChild, ElementRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { ProgressBarModule } from 'primeng/progressbar';
import { TagModule } from 'primeng/tag';
import { ToastModule } from 'primeng/toast';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { MessageService, ConfirmationService } from 'primeng/api';

import { CapacitacionesService } from '../../../services/capacitaciones.service';
import { IniciarCapacitacionResponse } from '../../../models/capacitacion.model';
import { finalize, interval, Subscription } from 'rxjs';

@Component({
  selector: 'app-capacitacion-play',
  standalone: true,
  imports: [
    CommonModule, CardModule, ButtonModule, ProgressBarModule, TagModule,
    ToastModule, ConfirmDialogModule
  ],
  providers: [MessageService, ConfirmationService],
  templateUrl: './capacitacion-play.component.html',
  styleUrls: ['./capacitacion-play.component.scss']
})
export class CapacitacionPlayComponent implements OnInit, OnDestroy {

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private svc = inject(CapacitacionesService);
  private sanitizer = inject(DomSanitizer);
  private msg = inject(MessageService);
  private confirm = inject(ConfirmationService);

  @ViewChild('player') player?: ElementRef<HTMLVideoElement>;

  loading = false;

  // Datos de la sesión
  data?: IniciarCapacitacionResponse;
  safeUrl?: SafeResourceUrl;

  // Tiempo / barra
  elapsed = 0; // segundos transcurridos
  tickerSub?: Subscription;

  get remaining(): number {
    const total = this.data?.expiraSegundos ?? 0;
    return Math.max(total - this.elapsed, 0);
  }

  get progressValue(): number {
    const total = this.data?.expiraSegundos ?? 0;
    if (!total) return 0;
    return Math.min(100, Math.round((this.elapsed / total) * 100));
  }

  ngOnInit(): void {
    const capId = Number(this.route.snapshot.paramMap.get('id'));
    if (!capId) {
      this.msg.add({ severity: 'warn', summary: 'Aviso', detail: 'Capacitación inválida.' });
      this.router.navigate(['/cursos']);
      return;
    }

    this.loading = true;
    this.svc.iniciarCapacitacion(capId)
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (res) => {
          this.data = res;
          // Sanear URL para iframe/video
          this.safeUrl = this.sanitizer.bypassSecurityTrustResourceUrl(res.url);

          // Timer genérico por segundo (para PDF y como respaldo del video)
          this.tickerSub = interval(1000).subscribe(() => {
            // Si es video y tenemos currentTime, preferir ese valor para elapsed.
            const vid = this.player?.nativeElement;
            if (this.data?.tipo === 'VIDEO' && vid) {
              this.elapsed = Math.floor(vid.currentTime);
            } else {
              this.elapsed++;
            }
          });

          this.msg.add({ severity: 'info', summary: res.titulo, detail: 'Sesión iniciada.' });
        },
        error: () => {
          this.msg.add({ severity: 'error', summary: 'Error', detail: 'No se pudo iniciar la capacitación.' });
          this.router.navigate(['/cursos']);
        }
      });
  }

  ngOnDestroy(): void {
    this.tickerSub?.unsubscribe();
  }

  finalizar(): void {
    if (!this.data) return;

    this.confirm.confirm({
      header: 'Finalizar contenido',
      message: '¿Confirmas que has finalizado esta capacitación?',
      icon: 'pi pi-check-circle',
      acceptLabel: 'Sí, finalizar',
      rejectLabel: 'Cancelar',
      acceptButtonStyleClass: 'p-button-success',
      rejectButtonStyleClass: 'p-button-text',
      accept: () => {
        this.loading = true;
        this.svc.finalizarCapacitacion(this.data!.capacitacionId)
          .pipe(finalize(() => (this.loading = false)))
          .subscribe({
            next: (r) => {
              this.msg.add({ severity: 'success', summary: 'Completado', detail: 'Capacitación finalizada.' });
              // Redirigir al detalle del curso con el id que viene en la respuesta
              this.router.navigate(['/curso', r.cursoId]);
            },
            error: () => {
              this.msg.add({ severity: 'error', summary: 'Error', detail: 'No se pudo finalizar.' });
            }
          });
      }
    });
  }

  formatTime(sec: number): string {
    const s = Math.max(0, Math.floor(sec));
    const mm = Math.floor(s / 60).toString().padStart(2, '0');
    const ss = (s % 60).toString().padStart(2, '0');
    return `${mm}:${ss}`;
  }
}
