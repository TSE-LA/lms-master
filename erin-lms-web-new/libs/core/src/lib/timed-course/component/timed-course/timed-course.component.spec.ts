import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimedCourseComponent } from './timed-course.component';

describe('TimedCourseComponent', () => {
  let component: TimedCourseComponent;
  let fixture: ComponentFixture<TimedCourseComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TimedCourseComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TimedCourseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
