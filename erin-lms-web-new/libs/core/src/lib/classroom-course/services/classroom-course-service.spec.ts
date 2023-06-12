import { TestBed } from '@angular/core/testing';

import { ClassroomCourseService } from './classroom-course.service';

describe('ClassroomCourseServiceService', () => {
  let service: ClassroomCourseService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ClassroomCourseService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
