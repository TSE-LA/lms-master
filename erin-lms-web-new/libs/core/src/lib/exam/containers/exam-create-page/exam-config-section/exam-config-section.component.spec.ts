import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExamConfigSectionComponent } from './exam-config-section.component';

describe('ExamConfigSectionComponent', () => {
  let component: ExamConfigSectionComponent;
  let fixture: ComponentFixture<ExamConfigSectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ExamConfigSectionComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ExamConfigSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
