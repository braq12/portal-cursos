import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CargarCapacitacionComponent } from './cargar-capacitacion.component';

describe('CargarCapacitacionComponent', () => {
  let component: CargarCapacitacionComponent;
  let fixture: ComponentFixture<CargarCapacitacionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CargarCapacitacionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CargarCapacitacionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
