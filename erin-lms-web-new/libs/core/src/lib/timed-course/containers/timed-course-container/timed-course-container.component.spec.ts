import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimedCourseContainerComponent } from './timed-course-container.component';

describe('TimedCourseContainerComponent', () => {
  let component: TimedCourseContainerComponent;
  let fixture: ComponentFixture<TimedCourseContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TimedCourseContainerComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TimedCourseContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
