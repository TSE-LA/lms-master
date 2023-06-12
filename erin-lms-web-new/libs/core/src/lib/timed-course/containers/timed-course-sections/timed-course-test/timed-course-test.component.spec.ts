import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimedCourseTestComponent } from './timed-course-test.component';

describe('TimedCourseTestComponent', () => {
  let component: TimedCourseTestComponent;
  let fixture: ComponentFixture<TimedCourseTestComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TimedCourseTestComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TimedCourseTestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
