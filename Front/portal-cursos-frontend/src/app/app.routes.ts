import { Routes } from '@angular/router';

import { LoginComponent } from './pages/login/login.component';

import { authGuard } from './core/auth/auth.guard';
import { roleGuard } from './core/auth/role.guard';

import { CursosComponent } from './pages/usuario/cursos/cursos.component';
import { CursoDetalleComponent } from './pages/usuario/curso-detalle/curso-detalle.component';
import { CapacitacionPlayComponent } from './pages/usuario/capacitacion-play/capacitacion-play.component';

import { CursosAdminComponent } from './pages/admin/cursos-admin/cursos-admin.component';
import { CrearCursoComponent } from './pages/admin/crear-curso/crear-curso.component';
import { CargarCapacitacionComponent } from './pages/admin/cargar-capacitacion/cargar-capacitacion.component';
import { ShellComponent } from './core/layout/shell/shell.component';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'login' },

  // pública
  { path: 'login', component: LoginComponent },

  // área protegida con layout Shell
  {
    path: '',
    component: ShellComponent,
    //scanMatch: [authGuard],
    children: [
      // USUARIO
      {
        path: '',
       // canMatch: [roleGuard(['USUARIO'])],
        children: [
          { path: 'cursos', component: CursosComponent },
          { path: 'curso/:id', component: CursoDetalleComponent },
          { path: 'capacitacion/:id', component: CapacitacionPlayComponent },
        ]
      },

      // ADMIN
      {
        path: 'admin',
       // canMatch: [roleGuard(['ADMIN'])],
        children: [
          { path: 'cursos', component: CursosAdminComponent },
          { path: 'crear-curso', component: CrearCursoComponent },
          { path: 'cargar-capacitacion', component: CargarCapacitacionComponent },
          { path: '', pathMatch: 'full', redirectTo: 'cursos' }
        ]
      }
    ]
  },

  { path: '**', redirectTo: 'login' }
];
