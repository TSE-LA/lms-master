import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimedCourseStatisticsComponent } from './timed-course-statistics.component';

describe('TimedCourseStatisticsComponent', () => {
  let component: TimedCourseStatisticsComponent;
  let fixture: ComponentFixture<TimedCourseStatisticsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TimedCourseStatisticsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TimedCourseStatisticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
