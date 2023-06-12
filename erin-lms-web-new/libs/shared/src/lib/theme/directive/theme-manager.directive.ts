/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

import {Directive, ElementRef, Input} from '@angular/core';
import {ThemeService} from "../services/theme.service";
import {ThemeVariables} from "../theme.vairables";
import {BreakPointObserverService} from "../services/break-point-observer.service";

@Directive({
  selector: '[jrsThemeManager]'
})
export class ThemeManagerDirective {
  @Input() initialTheme: string = "default";
  @Input() syncThemeWithOperatingSystem: boolean = false;
  @Input() customizedVariables = null;

  constructor(private themeService: ThemeService, private elementRef: ElementRef, private breakPointService: BreakPointObserverService) {  }

  ngOnInit()
  {
    this.changeTheme(this.initialTheme);

    this.themeService.onThemeChange().subscribe(changes => {
      this.updateTheme(changes.name, changes.previous);
    });
  }

  ngOnChanges() {
    this.changeTheme(this.initialTheme);
    if(this.syncThemeWithOperatingSystem)
    {
      this.themeService.syncThemeWithOperatingSystem();
    }
  }

  private changeTheme(name: string):void
  {
    if(name) {
      this.themeService.changeTheme(this.initialTheme);
    }else{
      this.themeService.changeTheme("default");
    }
  }

  private updateTheme(currentTheme: string, previousTheme: string): void
  {
    if (previousTheme) {
      this.elementRef.nativeElement.classList.remove(this.themeService.THEME_STYLE_PREFIX + previousTheme);
    }
    this.elementRef.nativeElement.classList.add(this.themeService.THEME_STYLE_PREFIX + currentTheme);
    if(this.customizedVariables)
    {
      this.elementRef.nativeElement.style = "";
      if (this.customizedVariables.get(currentTheme)){
        this.overrideVariable(this.customizedVariables.get(currentTheme));
      }else{
        console.warn("unsupported data format");
      }
    }
    this.getMediaBreakPoints();
  }

  private overrideVariable(customizedVariables: Map<string, string>) : void
  {
    for(let key of customizedVariables.keys())
    {
      if(ThemeVariables.includes(key)){
        this.elementRef.nativeElement.style.setProperty(`--${key}`, customizedVariables.get(key))
      }else {
        console.warn("unsupported data format");
      }
    }
  }

  private getMediaBreakPoints():void {
    this.breakPointService.getMediaBreakPointsFromCSS(this.elementRef.nativeElement);
  }
}
