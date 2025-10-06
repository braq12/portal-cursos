// role.guard.ts
import { inject } from '@angular/core';
import { CanMatchFn, Router, UrlTree } from '@angular/router';
import { AuthService } from './auth.service';

type Rol = 'ADMIN' | 'USUARIO';

export const roleGuard = (rolesPermitidos: Rol[]): CanMatchFn => {
  return (): boolean | UrlTree => {
    const auth = inject(AuthService);
    const router = inject(Router);

    if (!auth.isLoggedIn()) {
      return router.parseUrl('/login');
    }

    const rol = auth.role();

    // si el rol coincide, matchea; si no, devolver FALSE para que Angular pruebe la siguiente ruta
    return rol && rolesPermitidos.includes(rol) ? true : false;
  };
};
