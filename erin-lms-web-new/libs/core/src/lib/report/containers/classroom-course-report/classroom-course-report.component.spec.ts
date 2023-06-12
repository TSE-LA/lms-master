import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClassroomCourseReportComponent } from './classroom-course-report.component';

describe('ClassroomCourseReportComponent', () => {
  let component: ClassroomCourseReportComponent;
  let fixture: ComponentFixture<ClassroomCourseReportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ClassroomCourseReportComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ClassroomCourseReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
