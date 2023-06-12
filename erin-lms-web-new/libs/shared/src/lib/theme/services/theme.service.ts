/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

import {Injectable} from '@angular/core';
import {Observable, ReplaySubject} from 'rxjs';
import {share} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  public THEME_STYLE_PREFIX = "jrs-theme-";

  private currentTheme: string = "default";
  private themeList = ["default", "dark"];


  private themeChanges$ = new ReplaySubject(1);

  public syncThemeWithOperatingSystem(): void {
    return window.matchMedia('(prefers-color-scheme: dark)')
      .addEventListener('change', event => {
        if (event.matches) {
          this.changeTheme('dark')
        } else {
          this.changeTheme('default')
        }
      })
  }

  public changeTheme(name: string): void {
    if (this.themeList.includes(name)){
      this.themeChanges$.next({name, previous: this.currentTheme});
      this.currentTheme = name;
    }else {
      console.error(`${name} theme not found in registered theme.`)
    }
  }

  public onThemeChange(): Observable<any> {
    return this.themeChanges$.pipe(share());
  }

  public getRegisteredTheme(): string[] {
    return this.themeList;
  }

  public addThemeToRegistry(themeName: string): void {
    this.themeList.push(themeName);
  }
}
