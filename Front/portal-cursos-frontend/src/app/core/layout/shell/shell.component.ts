import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';

import { ToastModule } from 'primeng/toast';
import { MenubarModule } from 'primeng/menubar';
import { MenuModule } from 'primeng/menu';
import { SidebarModule } from 'primeng/sidebar';
import { ButtonModule } from 'primeng/button';

import { MessageService } from 'primeng/api';
import { AuthService } from '../../auth/auth.service';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    ToastModule,
    MenubarModule,
    MenuModule,
    SidebarModule,
    ButtonModule
  ],
  providers: [MessageService],
  templateUrl: './shell.component.html'
})
export class ShellComponent implements OnInit {
  mobileOpen = false;

  // PrimeNG MenuModel
  menu: any[] = [];

  constructor(private auth: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.buildMenu();
  }

  private buildMenu() {
    const role = this.auth.role();

    if (role === 'ADMIN') {
      this.menu = [
        { label: 'Reportes', icon: 'pi pi-chart-bar', routerLink: ['/admin/reporte'] },
        { label: 'Usuarios', icon: 'pi pi-users', routerLink: ['/admin/usuarios'] },
        {
          label: 'Cursos',
          icon: 'pi pi-book',
          items: [
            { label: 'Listado', icon: 'pi pi-list', routerLink: ['/admin/cursos'] },
            { label: 'Crear curso', icon: 'pi pi-plus', routerLink: ['/admin/crear-curso'] },
            { label: 'Cargar capacitación', icon: 'pi pi-upload', routerLink: ['/admin/cargar-capacitacion'] },
          ],
        },
      ];
    } else if (role === 'USUARIO') {
      this.menu = [
        { label: 'Mis cursos', icon: 'pi pi-briefcase', routerLink: ['/cursos'] },
      ];
    } else {
      // fallback por si no hay rol
      this.menu = [];
    }
  }

  logout() {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
