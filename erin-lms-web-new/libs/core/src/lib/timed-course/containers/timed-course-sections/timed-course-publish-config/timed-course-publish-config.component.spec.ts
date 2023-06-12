import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimedCoursePublishConfigComponent } from './timed-course-publish-config.component';

describe('TimedCoursePublishConfigComponent', () => {
  let component: TimedCoursePublishConfigComponent;
  let fixture: ComponentFixture<TimedCoursePublishConfigComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TimedCoursePublishConfigComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TimedCoursePublishConfigComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
