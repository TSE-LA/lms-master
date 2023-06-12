import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExamineePageComponent } from './examinee-page.component';

describe('ExamineePageComponent', () => {
  let component: ExamineePageComponent;
  let fixture: ComponentFixture<ExamineePageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ExamineePageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ExamineePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
