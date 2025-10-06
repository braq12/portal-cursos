// src/app/pages/admin/crear-curso/crear-curso.component.ts
import { Component, ViewChild, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';

// PrimeNG
import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { DropdownModule } from 'primeng/dropdown';
import { ButtonModule } from 'primeng/button';
import { FileUpload, FileUploadModule } from 'primeng/fileupload';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';

import { CursosService } from '../../../services/cursos.service';
import { CursoResponse } from '../../../models/curso.model';
import { catchError, finalize, of, tap } from 'rxjs';

@Component({
  selector: 'app-crear-curso',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule,
    CardModule, InputTextModule, DropdownModule, ButtonModule, FileUploadModule,
    ToastModule
  ],
  templateUrl: './crear-curso.component.html'
})
export class CrearCursoComponent {
  private fb = inject(FormBuilder);
  private svc = inject(CursosService);
  private msg = inject(MessageService);
  private router = inject(Router);

  @ViewChild('uploader') uploader?: FileUpload;

  loading = signal(false);

  categorias = [
    { label: 'Tecnología', value: 'Tecnología' },
    { label: 'Ventas',     value: 'Ventas' },
    { label: 'Finanzas',   value: 'Finanzas' },
    { label: 'Legal',      value: 'Legal' },
  ];

  // restricciones de fichero (ajústalas si quieres)
  readonly maxSizeBytes = 512 * 1024; // 512 KB
  readonly acceptedTypes = ['image/png', 'image/jpeg', 'image/webp'];

  previewUrl: string | null = null;

  form = this.fb.group({
    titulo: ['', [Validators.required, Validators.minLength(3)]],
    descripcion: ['', [Validators.maxLength(500)]],
    categoria: [null as string | null, [Validators.required]],
    insignia: [null as File | null] 
  });

  get f() { return this.form.controls; }

  onFileSelect(event: any) {
    const file: File | undefined =
      event?.files?.[0] || event?.currentFiles?.[0] || event?.target?.files?.[0];

    if (!file) return;

    // Validaciones
    if (!this.acceptedTypes.includes(file.type)) {
      this.msg.add({severity:'warn', summary:'Formato no permitido', detail:'Usa PNG, JPG o WEBP.'});
      this.clearFileOnly();
      return;
    }
    if (file.size > this.maxSizeBytes) {
      this.msg.add({severity:'warn', summary:'Archivo demasiado grande', detail:'Máximo 512KB.'});
      this.clearFileOnly();
      return;
    }

    // Set al form + preview
    this.form.patchValue({ insignia: file });
    const reader = new FileReader();
    reader.onload = () => { this.previewUrl = reader.result as string; };
    reader.readAsDataURL(file);
  }

  clearFileOnly() {
    this.form.patchValue({ insignia: null });
    this.previewUrl = null;
    this.uploader?.clear(); // limpia el p-fileUpload
  }

  guardar() {

    const file = this.form.get('insignia')?.value as File | null;
    if (!file) {
      this.msg.add({
        severity: 'warn',
        summary: 'Insignia obligatorio',
        detail: 'Seleccione una insignia para continuar.',
        life: 4000
      });
      return;
    }
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.msg.add({severity:'warn', summary:'Validación', detail:'Completa los campos obligatorios.'});
      return;
    }

    const v = this.form.value;
    this.loading.set(true);

    this.svc.crearCursoConInsignia({
      titulo: v.titulo!,
      descripcion: v.descripcion ?? undefined,
      categoria: v.categoria ?? undefined,
      insignia: v.insignia ?? undefined
    })
    .pipe(
      tap((res: CursoResponse) => {
        this.msg.add({severity:'success', summary:'Curso creado', detail:`#${res.id} - ${res.titulo}`});
        this.form.reset();
        this.form.patchValue({ categoria: null, insignia: null });
        this.previewUrl = null;
        this.uploader?.clear();
      }),
      catchError(err => {
        const detalle = err?.error?.mensaje || 'No se pudo crear el curso';
        this.msg.add({severity:'error', summary:'Error', detail: detalle});
        return of(null);
      }),
      finalize(() => this.loading.set(false))
    )
    .subscribe();
  }
}
