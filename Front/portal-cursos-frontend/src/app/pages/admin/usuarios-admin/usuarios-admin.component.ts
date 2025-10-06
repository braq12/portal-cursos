import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MessageService } from 'primeng/api';

import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { DropdownModule } from 'primeng/dropdown';
import { PasswordModule } from 'primeng/password';
import { ButtonModule } from 'primeng/button';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { ToastModule } from 'primeng/toast';
import { ToolbarModule } from 'primeng/toolbar';
import { UsuariosService} from '../../../services/usuarios.service';
import { UsuarioResponse } from '../../../models/usuario-model';

@Component({
  selector: 'app-usuarios-admin',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule,
    CardModule, InputTextModule, DropdownModule, PasswordModule, ButtonModule,
    TableModule, TagModule, ToastModule, ToolbarModule
  ],
  templateUrl: './usuarios-admin.component.html'
})
export class UsuariosAdminComponent implements OnInit {
  private fb = inject(FormBuilder);
  private svc = inject(UsuariosService);
  private msg = inject(MessageService);

  loadingLista = signal(false);
  creando = signal(false);

  usuarios: UsuarioResponse[] = [];
  roles = [
    { label: 'Administrador', value: 'ADMIN' },
    { label: 'Usuario', value: 'USUARIO' }
  ];

  // Filtro global de la tabla
  globalFilter = '';

  form = this.fb.group({
    nombre: ['', [Validators.required, Validators.minLength(2)]],
    correo: ['', [Validators.required, Validators.email]],
    rol:    ['USUARIO' as 'ADMIN' | 'USUARIO', [Validators.required]],
    clave:  ['', [Validators.required, Validators.minLength(6)]],
  });

  ngOnInit(): void {
    this.cargarUsuarios();
  }

  cargarUsuarios() {
    this.loadingLista.set(true);
    this.svc.listar().subscribe({
      next: (data) => this.usuarios = data,
      error: () => this.msg.add({severity:'error', summary:'Error', detail:'No se pudieron cargar los usuarios'}),
      complete: () => this.loadingLista.set(false)
    });
  }

  crearUsuario() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.msg.add({severity:'warn', summary:'Validación', detail:'Completa los campos obligatorios'});
      return;
    }
    this.creando.set(true);
    this.svc.crear(this.form.value as any).subscribe({
      next: (u) => {
        this.msg.add({severity:'success', summary:'Usuario creado', detail:`${u.nombre} (${u.correo})`});
        this.form.reset({ rol: 'USUARIO' });
        this.cargarUsuarios();
      },
      error: (err) => {
        const detalle = err?.error?.message || 'No se pudo crear el usuario';
        this.msg.add({severity:'error', summary:'Error', detail:detalle});
      },
      complete: () => this.creando.set(false)
    });
  }

  getSeverity(rol: string) {
    return rol === 'ADMIN' ? 'warn' : 'info';
  }
}
