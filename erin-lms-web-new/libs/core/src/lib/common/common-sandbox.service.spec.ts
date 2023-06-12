import { TestBed } from '@angular/core/testing';

import { CommonSandboxService } from './common-sandbox.service';

describe('JarvisCommonSandboxService', () => {
  let service: CommonSandboxService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CommonSandboxService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
