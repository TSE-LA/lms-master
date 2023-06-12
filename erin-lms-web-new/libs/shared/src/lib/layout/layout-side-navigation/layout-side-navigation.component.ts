/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

import {Component, Input, OnInit} from '@angular/core';
import {BreakPointObserverService} from "../../theme/services/break-point-observer.service";
import {NavigationEnd, Router} from "@angular/router";
import {CommonSandboxService} from "../../../../../core/src/lib/common/common-sandbox.service";

/*
* Side Navigation Section
*/

export interface NavItem {
  name: string;
  link: string;
  iconName: string;
  id: string;
  contextPath: string;
  notification?: string
}

@Component({
  selector: 'jrs-layout-sidenav',
  template: `
    <div class="sidenav-container">
      <jrs-overlay [show]="isTabletMode && isOpened" (clicked)="isOpened = !isOpened" [zIndex]="'7'"></jrs-overlay>
      <div *ngIf="!isTabletMode || (isTabletMode && isOpened)" class="sidebar">
        <ul>
          <li *ngFor="let item of navItems" (click)="selectItem(item)"
              routerLink="{{item.link}}"
              [class.selected]="isLinkActive(item.link)">
            <span class="tooltip" *ngIf="!isOpened">{{item.name}}</span>
            <div class="menu-item">
              <div class="menu-icon">
                <jrs-icon [color]="'dark-gray'" [size]="'fs-20'">{{item.iconName}}</jrs-icon>
              </div>
              <div class="menu-name" *ngIf="isOpened">
                {{item.name}}
              </div>
              <div *ngIf="item.notification != null && item.notification != '0'"
                   [ngClass]="{'closed-notification': !isOpened}"
                   style="margin-right: 5px;">
                <div class="notification">{{item.notification}}</div>
              </div>
            </div>
          </li>
        </ul>
      </div>
      <div [ngClass]="this.isFullSizeContentUrl ? 'sidenav-content sidenav-style' : 'sidenav-content'">
        <div [ngClass]="!isTabletMode && isOpened ? 'pushMainArea' : isTabletMode? 'originalMain' : 'collapseMain'">
          <ng-content></ng-content>
        </div>
      </div>
    </div>
  `,
  styleUrls: ['./layout-side-navigation.component.scss']
})
export class LayoutSidenavComponent implements OnInit {
  @Input() navItems: NavItem[];
  @Input() isOpened = true;
  selectedItem: string;
  currentUrl: string;

  routerId: string;
  isTabletMode = false;
  checkUrl: boolean;
  isFullSizeContentUrl: boolean;

  constructor(
    private breakPointService: BreakPointObserverService,
    private sb: CommonSandboxService,
    private router: Router) {
    router.events.forEach((event) => {
      if (event instanceof NavigationEnd) {
        this.currentUrl = event.url;
        this.isFullSizeContentUrl = (this.currentUrl.includes('online-course/launch') || this.currentUrl.includes('timed-course/launch') || this.currentUrl.includes('timed-course/read'));
      }
    })
  }

  ngOnInit(): void {
    this.currentUrl = this.router.url;
    this.isFullSizeContentUrl = (this.currentUrl.includes('online-course/launch') || this.currentUrl.includes('timed-course/launch') || this.currentUrl.includes('timed-course/read'));
    this.breakPointService.getMediaBreakPointChange().subscribe(res => {
      const currentMode = (res == "media_xs" || res == "media_sm" || res == "media_s");
      if (currentMode != this.isTabletMode) {
        this.isTabletMode = currentMode;
      }
    });
  }

  selectItem(item): void {
    this.routerId = item.id
    if (this.isTabletMode && this.isOpened) {
      this.isOpened = false;
    }
    this.selectedItem = item.name;
  }

  openNav(): void {
    this.isOpened = !this.isOpened;
  }

  isLinkActive(url): boolean {
    const currentUrl = this.router.url.substring(0, url.length);
    return currentUrl === url;
  }
}
