import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PercentageProgressComponent } from './percentage-progress.component';

describe('PercentageProgressComponent', () => {
  let component: PercentageProgressComponent;
  let fixture: ComponentFixture<PercentageProgressComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PercentageProgressComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PercentageProgressComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
