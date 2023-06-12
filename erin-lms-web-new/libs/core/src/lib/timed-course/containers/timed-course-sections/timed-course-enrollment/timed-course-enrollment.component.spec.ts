import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimedCourseEnrollmentComponent } from './timed-course-enrollment.component';

describe('TimedCourseEnrollmentComponent', () => {
  let component: TimedCourseEnrollmentComponent;
  let fixture: ComponentFixture<TimedCourseEnrollmentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TimedCourseEnrollmentComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TimedCourseEnrollmentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
