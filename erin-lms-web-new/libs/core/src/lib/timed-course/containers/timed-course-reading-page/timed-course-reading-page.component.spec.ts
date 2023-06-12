import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimedCourseReadingPageComponent } from './timed-course-reading-page.component';

describe('TimedCourseReadingContainerComponent', () => {
  let component: TimedCourseReadingPageComponent;
  let fixture: ComponentFixture<TimedCourseReadingPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TimedCourseReadingPageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TimedCourseReadingPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
