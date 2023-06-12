/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

import {LayoutSidenavComponent} from "./layout-side-navigation.component";
import {ComponentFixture, fakeAsync, flush, flushMicrotasks, TestBed} from "@angular/core/testing";
import {BreakPointObserverService} from "../../theme/services/break-point-observer.service";
import {of} from "rxjs";

describe('SidenavComponent', () => {
  let component: LayoutSidenavComponent;
  let breakPointService: BreakPointObserverService;
  let fixture: ComponentFixture<LayoutSidenavComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LayoutSidenavComponent],
      providers: [BreakPointObserverService]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LayoutSidenavComponent);
    breakPointService = TestBed.inject(BreakPointObserverService);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it("should update side nave on mobile mode", fakeAsync(()=>{
    let breakpointsServiceSpy = spyOn(breakPointService, "getMediaBreakPointChange")
    breakpointsServiceSpy.and.returnValue(of("media_s"));
    spyOn(component, "close");
    component.ngOnInit();
    expect(component.isTabletMode).toBeTruthy();
    expect(component.close).toHaveBeenCalled();

    //change
    breakpointsServiceSpy.and.returnValue(of("media_xl"));
    component.ngOnInit();
    expect(component.isTabletMode).toBeFalsy();
    expect(component.close).toHaveBeenCalled();
  }));

  it("sidenav update should update only when date changed", fakeAsync(()=>{
    let breakpointsServiceSpy = spyOn(breakPointService, "getMediaBreakPointChange")
    breakpointsServiceSpy.and.returnValue(of("media_s"));
    component.isTabletMode = true;
    spyOn(component, "close");
    component.ngOnInit();
    expect(component.close).not.toHaveBeenCalled();
  }));

  it("should update state open/close when toggle triggered",()=>{
    let requestCount = 0;
    component.dataChange().subscribe(res=>{
      requestCount++;
      if(requestCount==1){
        //default
        expect(res).toEqual({over:false, opened:true});
      }else if(requestCount==2){
        expect(res).toEqual({over:false, opened:false});
      }
    });

    expect(component.isOpened).toBeTruthy();
    component.toggle();
    expect(component.isOpened).toBeFalsy();
  });

  it("should update open state to close when close triggered",()=>{
    let requestCount = 0;
    component.dataChange().subscribe(res=>{
      requestCount++;
      if(requestCount==1){
        //default
        expect(res).toEqual({over:false, opened:true});
      }else if(requestCount==2){
        expect(res).toEqual({over:false, opened:false});
      }else if(requestCount==3){
        expect(res).toEqual({over:false, opened:false});
      }
    });

    expect(component.isOpened).toBeTruthy();
    component.close();
    expect(component.isOpened).toBeFalsy();
    component.close();
    expect(component.isOpened).toBeFalsy();
  });

  it("should return over set true/false on tablet mode change",()=>{
    //case1
    component.isTabletMode = true;
    component.over = false;
    component.toggle();

    //expectation
    let requestCount = 0;
    component.dataChange().subscribe(res=>{
      requestCount++;
      if(requestCount==1){
        //case:1
        expect(res).toEqual({over:true, opened:false}, "over should set false when tablet mode");
      }else if(requestCount==2){
        //case:2
        expect(res).toEqual({over:false, opened:true}, "over should set ture when it is not tablet mode");
      }
    });

    //case2
    component.isTabletMode = false;
    component.toggle();
  })
});
