import { TestBed } from '@angular/core/testing';

import { ReportSandboxService } from './report-sandbox.service';

describe('ReportSandboxService', () => {
  let service: ReportSandboxService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ReportSandboxService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
