/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

import { TestBed } from '@angular/core/testing';

import { ThemeService } from './theme.service';
import {fail} from "assert";

describe('ThemeService', () => {
  let service: ThemeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ThemeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should update observable when change theme triggered', ()=> {
    let expectedChangingThemeName = "dark";
    service.changeTheme(expectedChangingThemeName);

    service.onThemeChange().subscribe(res => {
      expect(res.name).toBe(expectedChangingThemeName);
      expect(res.previous).toBe("default");
    });
  });

  it('should not update observable when change theme triggered with not registered theme', ()=>{
    spyOn(console, 'error');
    service.changeTheme("imposter");
    expect(console.error).toHaveBeenCalled();
    service.onThemeChange().subscribe(res => {
      fail("should not update theme by unregistered theme");
    });
  });
});
