import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OnlineCourseEnrollmentComponent } from './online-course-enrollment.component';

describe('OnlineCourseEnrollmentComponent', () => {
  let component: OnlineCourseEnrollmentComponent;
  let fixture: ComponentFixture<OnlineCourseEnrollmentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OnlineCourseEnrollmentComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OnlineCourseEnrollmentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
