import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExamUpdatePageComponent } from './exam-update-page.component';

describe('ExamUpdatePageComponent', () => {
  let component: ExamUpdatePageComponent;
  let fixture: ComponentFixture<ExamUpdatePageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ExamUpdatePageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ExamUpdatePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
