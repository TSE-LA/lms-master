/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

import { ComponentFixture, TestBed } from '@angular/core/testing';
import {LayoutComponent} from "./layout.component";
import {LayoutSidenavComponent} from "./layout-side-navigation/layout-side-navigation.component";
import {BreakPointObserverService} from "../theme/services/break-point-observer.service";

describe('SidenavComponent', () => {
  let component: LayoutComponent;
  let fixture: ComponentFixture<LayoutComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LayoutComponent, LayoutSidenavComponent],
      providers: [BreakPointObserverService]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LayoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    //both sidenav model value should be false
    expect(fixture.nativeElement.getElementsByClassName("over").length).toEqual(0);
    expect(fixture.nativeElement.getElementsByClassName("closed").length).toEqual(2);
  });

  it('should set over class when sidenav property updated', ()=>{
    //left sidenav class check
    expect(fixture.nativeElement.getElementsByClassName("over").length).toEqual(0);
    component.leftSideNavProperty.over = true;
    fixture.detectChanges();
    expect(fixture.nativeElement.getElementsByClassName("over").length).toEqual(1);
    //right sidenav class check
    component.rightSideNavProperty.over = true;
    fixture.detectChanges();
    expect(fixture.nativeElement.getElementsByClassName("over").length).toEqual(2);
  });

  it('should set closed class when sidenav property updated', ()=>{
    component.leftSideNavProperty.isOpened = false;
    component.rightSideNavProperty.isOpened = false;
    //left sidenav class check
    expect(fixture.nativeElement.getElementsByClassName("closed").length).toEqual(2);
    component.leftSideNavProperty.isOpened = true;
    fixture.detectChanges();
    expect(fixture.nativeElement.getElementsByClassName("closed").length).toEqual(1);

    //right sidenav class check
    component.rightSideNavProperty.isOpened = true;
    fixture.detectChanges();
    expect(fixture.nativeElement.getElementsByClassName("closed").length).toEqual(0);
  });
});
