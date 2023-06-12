import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExamBasicInfoSectionComponent } from './exam-basic-info-section.component';

describe('ExamBasicInfoSectionComponent', () => {
  let component: ExamBasicInfoSectionComponent;
  let fixture: ComponentFixture<ExamBasicInfoSectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ExamBasicInfoSectionComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ExamBasicInfoSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
