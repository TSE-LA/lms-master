import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimedCourseInfoComponent } from './timed-course-info.component';

describe('TimedCourseInfoComponent', () => {
  let component: TimedCourseInfoComponent;
  let fixture: ComponentFixture<TimedCourseInfoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TimedCourseInfoComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TimedCourseInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
