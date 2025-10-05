import { Component } from '@angular/core';
import { RouterModule, RouterOutlet, RouterLink, Router } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { AuthService } from '../../auth/auth.service';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [CommonModule, RouterModule, RouterOutlet, RouterLink, MatToolbarModule, MatButtonModule, ToastModule],
  providers: [MessageService],               
  template: `
    <p-toast position="top-right" [baseZIndex]="3000"></p-toast>

    <mat-toolbar color="primary">
      <span>Portal Cursos</span>
      <span class="spacer"></span>

      <ng-container *ngIf="role() === 'USUARIO'">
        <a mat-button routerLink="/cursos">Mis cursos</a>
      </ng-container>

      <ng-container *ngIf="role() === 'ADMIN'">
        <a mat-button routerLink="/admin/cursos">Cursos</a>
        <a mat-button routerLink="/admin/crear-curso">Crear curso</a>
        <a mat-button routerLink="/admin/cargar-capacitacion">Cargar capacitación</a>
      </ng-container>

      <button mat-button (click)="logout()">Salir</button>
    </mat-toolbar>

    <router-outlet />
  `,
  styles: [`.spacer { flex: 1 }`]
})
export class ShellComponent {
  constructor(private auth: AuthService,private router: Router) {}
  role() { return this.auth.role(); }
  logout() {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}