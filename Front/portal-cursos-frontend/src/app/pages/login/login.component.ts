import { Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { ButtonModule } from 'primeng/button';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { CommonModule } from '@angular/common';
import { finalize } from 'rxjs/operators';
import { DividerModule } from 'primeng/divider';
import { FloatLabelModule } from 'primeng/floatlabel';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule,
    CardModule, InputTextModule, PasswordModule, ButtonModule, ToastModule, ProgressSpinnerModule
  ],
  providers: [MessageService],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);
  private msg = inject(MessageService);

  loading = signal(false);
  form = this.fb.group({
    correo: ['', [Validators.required, Validators.email]],
    clave:  ['', [Validators.required, Validators.minLength(6)]],
  });

  submit() {
    if (this.form.invalid || this.loading()) return;
    this.loading.set(true);

    const { correo, clave } = this.form.value as { correo: string; clave: string };
    this.auth.login(correo, clave)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (r) => {
          this.auth.saveToken(r.token);
          const rol = this.auth.role();
          this.msg.add({ severity: 'success', summary: 'Bienvenido' });
          this.router.navigate([ rol === 'ADMIN' ? '/admin/reporte' : '/cursos' ]);
        },
        error: (err) => {
          const detalle = err?.error?.detalle || err?.error?.mensaje || 'Credenciales inválidas';
          this.msg.add({ severity: 'error', summary: 'Error', detail: detalle });
        }
      });
  }
}
