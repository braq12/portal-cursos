import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';

// PrimeNG
import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';

import { DropdownModule } from 'primeng/dropdown';
import { ButtonModule } from 'primeng/button';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';


import { catchError, finalize, of, tap } from 'rxjs';
import { CursosService } from '../../../services/cursos.service';
import { CrearCursoRequest, CursoResponse } from '../../../models/curso.model';

@Component({
  selector: 'app-crear-curso',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule,
    CardModule, InputTextModule, InputTextModule, DropdownModule, ButtonModule, ToastModule
  ],
  templateUrl: './crear-curso.component.html',
  styleUrl: './crear-curso.component.scss'
})
export class CrearCursoComponent {
  private fb = inject(FormBuilder);
  private svc = inject(CursosService);
  private msg = inject(MessageService);
  private router = inject(Router);

  loading = false;

  categorias = [
    { label: 'Tecnología', value: 'Tecnología' },
    { label: 'Ventas',     value: 'Ventas' },
    { label: 'Finanzas',   value: 'Finanzas' },
    { label: 'Legal',      value: 'Legal' },
  ];

  form = this.fb.group({
    titulo: ['', [Validators.required, Validators.minLength(3)]],
    descripcion: ['', [Validators.maxLength(500)]],
    categoria: [null as string | null, [Validators.required]],
  });

  get f() { return this.form.controls; }

  ngOnInit(): void {
    this.msg.add({ severity: 'error', summary: 'Error', detail: 'No se pudo subir la capacitación.' });
  }

  guardar() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const payload = this.form.value as CrearCursoRequest;

    this.loading=true;
    this.svc.crearCurso(payload)
      .pipe(
        tap((res: CursoResponse) => {
          this.msg.add({ severity: 'success', summary: 'Curso creado', detail: `#${res.id} - ${res.titulo}` });
          this.form.reset(); 
        }),
        catchError((err) => {
          const detalle = err?.error?.mensaje || 'No se pudo crear el curso';
          this.msg.add({ severity: 'error', summary: 'Error', detail: detalle });
          return of(null);
        }),
        finalize(() => this.loading=false)
      )
      .subscribe();
  }
}
