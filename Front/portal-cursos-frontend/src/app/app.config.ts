import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideClientHydration, withEventReplay } from '@angular/platform-browser';
import { provideHttpClient, withInterceptors } from '@angular/common/http';

import { jwtInterceptor } from './core/auth/jwt.interceptor';
import { providePrimeNG } from 'primeng/config';
import Aura from '@primeng/themes/aura';

import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { MessageService } from 'primeng/api';


export const appConfig: ApplicationConfig = {
  providers: 
  [provideZoneChangeDetection({ eventCoalescing: true }),
     provideRouter(routes), 
     provideHttpClient(
      withInterceptors([jwtInterceptor]) 
    ),
    MessageService,
    providePrimeNG({
      theme: {
          preset: Aura
      }
  }),
  provideAnimationsAsync(),
     provideClientHydration(withEventReplay())]
};


