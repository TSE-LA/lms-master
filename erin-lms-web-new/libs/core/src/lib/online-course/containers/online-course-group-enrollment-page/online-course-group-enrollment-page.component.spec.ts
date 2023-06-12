import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OnlineCourseGroupEnrollmentPageComponent } from './online-course-group-enrollment-page.component';

describe('OnlineCourseGroupEnrollmentPageComponent', () => {
  let component: OnlineCourseGroupEnrollmentPageComponent;
  let fixture: ComponentFixture<OnlineCourseGroupEnrollmentPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OnlineCourseGroupEnrollmentPageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OnlineCourseGroupEnrollmentPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
