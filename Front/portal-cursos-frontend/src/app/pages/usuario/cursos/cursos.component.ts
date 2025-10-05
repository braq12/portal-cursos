import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { CursosService } from '../../../services/cursos.service';

@Component({
  standalone: true,
  selector: 'app-cursos',
  imports: [CommonModule, MatButtonModule],
  template: `
  <h2>Cursos disponibles</h2>
  <div *ngFor="let c of cursos" style="margin-bottom:12px;padding:12px;border:1px solid #ddd;border-radius:8px">
    <div><b>{{c.titulo}}</b> <small>({{c.categoria}})</small></div>
    <div>{{c.descripcion}}</div>
    <div>Capacitaciones: {{c.cantidadCapacitaciones}}</div>
    <div>Estado curso: {{c.estadoCurso}} | Iniciado: {{c.iniciado ? 'Sí' : 'No'}}</div>
    <button mat-raised-button color="primary" (click)="iniciar(c.id)">Iniciar / Continuar</button>
  </div>
  `
})
export class CursosComponent implements OnInit {
  cursos: any[] = [];
  constructor(private svc: CursosService, private router: Router) {}
  ngOnInit() { this.svc.listarDisponibles().subscribe(r => this.cursos = r); }
  iniciar(cursoId: number) {
    //this.svc.iniciarCurso(cursoId).subscribe(r => this.router.navigate(['/curso', r.cursoId]));
  }
}
