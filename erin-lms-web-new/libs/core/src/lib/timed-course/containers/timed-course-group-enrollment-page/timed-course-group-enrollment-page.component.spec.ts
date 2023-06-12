import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimedCourseGroupEnrollmentPageComponent } from './timed-course-group-enrollment-page.component';

describe('TimedCourseGroupEnrollmentPageComponent', () => {
  let component: TimedCourseGroupEnrollmentPageComponent;
  let fixture: ComponentFixture<TimedCourseGroupEnrollmentPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TimedCourseGroupEnrollmentPageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TimedCourseGroupEnrollmentPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
