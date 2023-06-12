import { TestBed } from '@angular/core/testing';

import { CourseNotificationService } from './course-notification.service';

describe('CourseNotificationService', () => {
  let service: CourseNotificationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CourseNotificationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
