import { TestBed } from '@angular/core/testing';

import { TimedCourseService } from './timed-course.service';

describe('TimedCourseService', () => {
  let service: TimedCourseService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TimedCourseService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
