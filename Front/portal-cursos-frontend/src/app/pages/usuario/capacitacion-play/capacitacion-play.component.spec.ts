import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CapacitacionPlayComponent } from './capacitacion-play.component';

describe('CapacitacionPlayComponent', () => {
  let component: CapacitacionPlayComponent;
  let fixture: ComponentFixture<CapacitacionPlayComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CapacitacionPlayComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CapacitacionPlayComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
