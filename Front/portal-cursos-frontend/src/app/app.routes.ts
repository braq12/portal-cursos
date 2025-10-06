import { Routes } from '@angular/router';



import { authGuard } from './core/auth/auth.guard';
import { roleGuard } from './core/auth/role.guard';


export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'login' },

  // pública
  {
    path: 'login',
    loadComponent: () =>
      import('./pages/login/login.component').then(m => m.LoginComponent)
  },

  // área protegida con layout Shell
  {
    path: '',
    loadComponent: () =>
      import('./core/layout/shell/shell.component').then(m => m.ShellComponent),
    canActivate: [authGuard],
    children: [
      // USUARIO
      {
        path: '',
       canMatch: [roleGuard(['USUARIO'])],
        children: [
          {
            path: 'cursos',
            loadComponent: () =>
              import('./pages/usuario/cursos/cursos.component').then(m => m.CursosUsuarioComponent)
          },
          {
            path: 'curso/:id',
            loadComponent: () =>
              import('./pages/usuario/curso-detalle/curso-detalle.component').then(m => m.CursoDetalleComponent)
          },
          {
            path: 'capacitacion/:id',
            loadComponent: () =>
              import('./pages/usuario/capacitacion-play/capacitacion-play.component').then(m => m.CapacitacionPlayComponent)
          },
        ]
      },

      // ADMIN
      {
        path: 'admin',
        canMatch: [roleGuard(['ADMIN'])],
        children: [

        {
                path: 'usuarios',
                loadComponent: () =>
                  import('./pages/admin/usuarios-admin/usuarios-admin.component').then(m => m.UsuariosAdminComponent)
            },
          {
            path: 'reporte',
            loadComponent: () =>
              import('./pages/admin/reporte-cursos/reporte-cursos.component').then(m => m.ReporteCursosComponent)
          },
          {
            path: 'cursos',
            loadComponent: () =>
              import('./pages/admin/cursos-admin/cursos-admin.component').then(m => m.CursosAdminComponent)
          },
          {
            path: 'crear-curso',
            loadComponent: () =>
              import('./pages/admin/crear-curso/crear-curso.component').then(m => m.CrearCursoComponent)
          },
          {
            path: 'cargar-capacitacion',
            loadComponent: () =>
              import('./pages/admin/cargar-capacitacion/cargar-capacitacion.component').then(m => m.CargarCapacitacionComponent)
          },
          { path: '', pathMatch: 'full', redirectTo: 'reporte' }
        ]
      }
    ]
  },

  { path: '**', redirectTo: 'login' }
];
