import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClassroomCourseAttendancePageComponent } from './classroom-course-attendance-page.component';

describe('ClassroomCourseAttendancePageComponent', () => {
  let component: ClassroomCourseAttendancePageComponent;
  let fixture: ComponentFixture<ClassroomCourseAttendancePageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ClassroomCourseAttendancePageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ClassroomCourseAttendancePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
