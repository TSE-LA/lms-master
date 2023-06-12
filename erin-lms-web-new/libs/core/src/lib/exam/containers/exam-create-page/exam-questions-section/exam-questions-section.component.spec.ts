import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExamQuestionsSectionComponent } from './exam-questions-section.component';

describe('ExamQuestionsSectionComponent', () => {
  let component: ExamQuestionsSectionComponent;
  let fixture: ComponentFixture<ExamQuestionsSectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ExamQuestionsSectionComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ExamQuestionsSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
