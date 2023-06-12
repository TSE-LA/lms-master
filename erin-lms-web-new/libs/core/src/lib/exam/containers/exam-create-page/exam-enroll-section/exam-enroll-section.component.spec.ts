import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExamEnrollSectionComponent } from './exam-enroll-section.component';

describe('ExamEnrollSectionComponent', () => {
  let component: ExamEnrollSectionComponent;
  let fixture: ComponentFixture<ExamEnrollSectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ExamEnrollSectionComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ExamEnrollSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
