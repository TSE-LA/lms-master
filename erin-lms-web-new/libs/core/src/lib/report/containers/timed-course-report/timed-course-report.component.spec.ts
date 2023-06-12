import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimedCourseReportComponent } from './timed-course-report.component';

describe('TimedCourseReportComponent', () => {
  let component: TimedCourseReportComponent;
  let fixture: ComponentFixture<TimedCourseReportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TimedCourseReportComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TimedCourseReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
