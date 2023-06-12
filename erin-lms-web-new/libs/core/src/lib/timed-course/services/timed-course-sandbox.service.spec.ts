import { TestBed } from '@angular/core/testing';

import { TimedCourseSandboxService } from './timed-course-sandbox.service';

describe('TimedCourseSandboxService', () => {
  let service: TimedCourseSandboxService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TimedCourseSandboxService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
