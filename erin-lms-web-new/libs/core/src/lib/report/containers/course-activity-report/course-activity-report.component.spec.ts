import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CourseActivityReportComponent } from './course-activity-report.component';

describe('CourseActivityReportComponent', () => {
  let component: CourseActivityReportComponent;
  let fixture: ComponentFixture<CourseActivityReportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CourseActivityReportComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CourseActivityReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
