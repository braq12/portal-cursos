import { inject } from '@angular/core';
import { CanMatchFn, Router, UrlTree } from '@angular/router';
import { AuthService } from './auth.service';

export const roleGuard =
  (rolesPermitidos: Array<'ADMIN'|'USUARIO'>): CanMatchFn =>
  (): boolean | UrlTree => {
    const auth = inject(AuthService);
    const router = inject(Router);

    if (!auth.isLoggedIn()) return router.parseUrl('/login');

    const rol = auth.role();
    if (rol && rolesPermitidos.includes(rol)) return true;
    return router.createUrlTree(rol === 'ADMIN' ? ['/admin/cursos'] : ['/cursos']);
  };
