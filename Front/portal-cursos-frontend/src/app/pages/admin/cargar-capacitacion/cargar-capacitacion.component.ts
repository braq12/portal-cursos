import { Component, OnInit, ViewChild, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { DropdownModule } from 'primeng/dropdown';
import { ButtonModule } from 'primeng/button';
import { FileUpload, FileUploadModule } from 'primeng/fileupload';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';

import { CursosService } from '../../../services/cursos.service';
import { CapacitacionesService } from '../../../services/capacitaciones.service';
import { catchError, finalize, map, of, tap } from 'rxjs';



@Component({
  selector: 'app-cargar-capacitacion',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule,
    CardModule, InputTextModule, DropdownModule, ButtonModule, FileUploadModule,
    ToastModule
  ],
  templateUrl: './cargar-capacitacion.component.html'
})
export class CargarCapacitacionComponent implements OnInit {
  private fb = inject(FormBuilder);
  private cursosService = inject(CursosService);
  private capsService = inject(CapacitacionesService);
  private msg = inject(MessageService);
  private router = inject(Router);

  @ViewChild('uploader') uploader!: FileUpload;

  loading = false;
  cargandoCursos = false;
  archivoFaltante = false;

  cursosOptions: { label: string; value: number }[] = [];
  tipos = [
    { label: 'VIDEO', value: 'VIDEO' },
    { label: 'DOCUMENTO', value: 'DOCUMENTO' }
  ];

  form = this.fb.group({
    cursoId: [null as number | null, [Validators.required]],
    tipo: ['VIDEO', [Validators.required]],
    titulo: ['', [Validators.required, Validators.minLength(3)]],
    descripcion: ['', [Validators.maxLength(500)]],
    duracionMinutos: [null as number | null],
    orden: [1],
    archivo: [null as File | null]
  });

  ngOnInit(): void {
    this.cargarCursos();
  }

  private cargarCursos() {
    this.cargandoCursos=true;
    this.cursosService.listarCursos()
      .pipe(
        map((res: any) => {
          const items = Array.isArray(res?.lista) ? res.lista : res;
          return (items || []).map((c: any) => ({
            label: `${c.titulo} (ID: ${c.idCurso})`,
            value: c.idCurso
          }));
        }),
        tap(opts => this.cursosOptions = opts),
        catchError(() => {
          this.msg.add({ severity: 'error', summary: 'Error', detail: 'No se pudieron cargar los cursos' });
          return of([]);
        }),
        finalize(() => this.cargandoCursos=false)
      )
      .subscribe();
  }


  enviar() {
    this.loading=true;
    const file = this.form.get('archivo')?.value as File | null;
    if (!file) {
      this.archivoFaltante = true;
      this.msg.add({
        severity: 'warn',
        summary: 'Archivo obligatorio',
        detail: 'Seleccione un archivo para continuar.',
        life: 4000
      });
      this.loading=false;
      return;
    }

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.msg.add({ severity: 'warn', summary: 'Validación', detail: 'Complete los campos obligatorios.' });
      this.loading=false;
      return;
    }

    

    const v = this.form.value;
    const fd = new FormData();
    fd.append('cursoId', String(v.cursoId));
    fd.append('titulo', v.titulo ?? '');
    if (v.descripcion) fd.append('descripcion', v.descripcion);
    fd.append('tipo', v.tipo ?? 'VIDEO');
    if (v.duracionMinutos != null) fd.append('duracionMinutos', String(v.duracionMinutos));
    if (v.orden != null) fd.append('orden', String(v.orden));
    if (v.archivo) fd.append('archivo', v.archivo as File);


    this.capsService.cargarCapacitacion(fd)
      .pipe(
        tap(res => {
          this.msg.add({ severity: 'success', summary: 'Capacitación creada', detail: res.titulo });
          this.form.reset({ tipo: 'VIDEO', orden: 1 ,archivo: null});
          this.uploader.clear(); 
        }),
        catchError(() => {
          this.msg.add({ severity: 'error', summary: 'Error', detail: 'No se pudo subir la capacitación.' });
          return of(null);
        }),
        finalize(() => this.loading=false)
      )
      .subscribe();
  }

  
  onFileSelect(event: any) {
    const file = event?.files?.[0] || event?.target?.files?.[0];
    if (file) {
      this.form.patchValue({ archivo: file });
      this.form.get('archivo')?.markAsDirty();
      this.form.get('archivo')?.updateValueAndValidity();
      this.archivoFaltante = false;
    }
  }

}
