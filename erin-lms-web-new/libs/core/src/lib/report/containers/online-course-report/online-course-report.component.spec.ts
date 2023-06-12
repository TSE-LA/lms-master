import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OnlineCourseReportComponent } from './online-course-report.component';

describe('OnlineCourseReportComponent', () => {
  let component: OnlineCourseReportComponent;
  let fixture: ComponentFixture<OnlineCourseReportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OnlineCourseReportComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OnlineCourseReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
