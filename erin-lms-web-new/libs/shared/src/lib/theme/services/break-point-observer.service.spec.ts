/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

import { TestBed } from '@angular/core/testing';

import { BreakPointObserverService } from './break-point-observer.service';

describe('BreakPointObserverService', () => {
  let service: BreakPointObserverService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BreakPointObserverService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
