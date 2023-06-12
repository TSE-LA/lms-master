import { TestBed } from '@angular/core/testing';

import { DashboardSandboxService } from './dashboard-sandbox.service';

describe('DashboardSandboxService', () => {
  let service: DashboardSandboxService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DashboardSandboxService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
