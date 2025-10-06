import { HttpInterceptorFn } from '@angular/common/http';
import { environment } from '../../../environments/environment';

const LOGIN_URL = `${environment.apiBaseUrl}/auth/login`;

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  if (req.url.includes('/auth/login')) return next(req);
  if (req.url.startsWith(LOGIN_URL))   return next(req);

  const token = localStorage.getItem('token');
  if (token) req = req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
  return next(req);
};
