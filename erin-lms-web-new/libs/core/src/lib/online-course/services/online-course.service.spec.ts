import { TestBed } from '@angular/core/testing';

import { OnlineCourseService } from './online-course.service';

describe('OnlineCourseService', () => {
  let service: OnlineCourseService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OnlineCourseService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
