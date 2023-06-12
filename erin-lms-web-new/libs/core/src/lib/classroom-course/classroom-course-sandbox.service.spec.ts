import { TestBed } from '@angular/core/testing';

import { ClassroomCourseSandboxService } from './classroom-course-sandbox.service';

describe('ClassroomCourseSandboxService', () => {
  let service: ClassroomCourseSandboxService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ClassroomCourseSandboxService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
