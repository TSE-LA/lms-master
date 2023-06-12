/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

import { ThemeManagerDirective } from './theme-manager.directive';

import {Component, Input, OnInit} from '@angular/core';
import {ComponentFixture, TestBed} from "@angular/core/testing";
import {NO_ERRORS_SCHEMA} from "@angular/compiler";
import {ThemeService} from "../services/theme.service";
@Component({
  template: `
  <div
    jrsThemeManager
    [initialTheme]="initialTheme"
    [customizedVariables]="customizedVariables"
    [syncThemeWithOperatingSystem]="syncThemeWithOperatingSystem">
    Humans
  </div>
  `
})
export class PrometheusComponent implements OnInit{
  initialTheme = null;
  customizedVariables = null;
  syncThemeWithOperatingSystem = false;

  ngOnInit(): void {
  }
}

describe('ThemeManagerDirective', () => {
  let fixture: ComponentFixture<PrometheusComponent>;
  let component: PrometheusComponent;
  let themeService: ThemeService;
  let CUSTOM_VARIABLE_KEY = "background-color-primary";
  let CUSTOM_VARIABLE_VALUE_DEFAULT_THEME = "blue";
  let CUSTOM_VARIABLE_VALUE_DARK_THEME = "red";

  let customVariables = new Map (
    [
      ["default", new Map([
        [CUSTOM_VARIABLE_KEY, CUSTOM_VARIABLE_VALUE_DEFAULT_THEME]])
      ],
        ["dark", new Map([
        [CUSTOM_VARIABLE_KEY, CUSTOM_VARIABLE_VALUE_DARK_THEME]])
        ]
    ]);

  beforeEach(()=>{
    fixture = TestBed.configureTestingModule({
      declarations: [ PrometheusComponent, ThemeManagerDirective ],
      providers: [ThemeService],
      schemas:      [ NO_ERRORS_SCHEMA ]
    }).createComponent(PrometheusComponent);
    component = fixture.componentInstance;
    themeService = TestBed.inject(ThemeService);

    fixture.detectChanges(); // initial binding
  });

  it('should set default style when initialTheme set to null', () => {
    fixture.detectChanges();
    expect(fixture.nativeElement.firstChild.classList.contains("jrs-theme-default")).toBeTruthy();
  });

  it('should set theme when initialTheme set', () => {
    component.initialTheme = "dark";
    component.ngOnInit();
    fixture.detectChanges();
    expect(fixture.nativeElement.firstChild.classList.contains("jrs-theme-dark")).toBeTruthy();
  });

  it('should sync with with operating system when flag is set true', () => {
    spyOn(themeService, "syncThemeWithOperatingSystem");
    component.syncThemeWithOperatingSystem = true;
    component.ngOnInit();
    fixture.detectChanges();
    expect(themeService.syncThemeWithOperatingSystem).toHaveBeenCalled();
  });

  it('should not sync with with operating system when flag is set false', () => {
    spyOn(themeService, "syncThemeWithOperatingSystem");
    component.syncThemeWithOperatingSystem = false;
    component.ngOnInit();
    fixture.detectChanges();
    expect(themeService.syncThemeWithOperatingSystem).not.toHaveBeenCalled();
  });

  it('should override variables when custom variable defined', () => {
    spyOn(themeService, "syncThemeWithOperatingSystem");
    //default theme
    component.customizedVariables = customVariables;
    component.ngOnInit();
    fixture.detectChanges();
    expect(fixture.nativeElement.firstChild.style.getPropertyValue(`--${CUSTOM_VARIABLE_KEY}`)).toBe(CUSTOM_VARIABLE_VALUE_DEFAULT_THEME);
    //dark theme
    component.initialTheme = "dark";
    component.ngOnInit();
    fixture.detectChanges();
    expect(fixture.nativeElement.firstChild.style.getPropertyValue(`--${CUSTOM_VARIABLE_KEY}`)).toBe(CUSTOM_VARIABLE_VALUE_DARK_THEME);
  });
});
