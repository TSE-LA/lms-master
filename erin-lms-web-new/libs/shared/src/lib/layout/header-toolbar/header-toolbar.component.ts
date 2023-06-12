/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
} from "@angular/core";
import { BreakPointObserverService } from "../../theme/services/break-point-observer.service";
import { ThemeService } from "../../theme/services/theme.service";
import { NavigationEnd, Router } from "@angular/router";
import {
  CLASSROOM_COURSE_SEARCH,
  ONLINE_COURSE_SEARCH,
  TIMED_COURSE_SEARCH,
} from "../../shared-constants";
import { SearchModel } from "../../shared-model";

@Component({
  selector: "jrs-header-toolbar",
  template: `
    <div class="header" [ngClass]="isTabletMode ? 'tablet-mode' : null">
      <jrs-button
        class="nav-toggle"
        [iconName]="'menu'"
        [iconColor]="'dark'"
        [size]="'icon-large'"
        [isMaterial]="true"
        [noOutline]="true"
        (clicked)="toggleNavbar()"
      >
      </jrs-button>
      <div
        *ngIf="!isTabletMode"
        class="company-logo"
        (click)="navigateToHome()"
      >
        <img *ngIf="!logo" src="{{ logoPath }}" alt="header-logo" />
        <img
          *ngIf="logo"
          class="logo"
          [src]="logo"
          (error)="logo = 'assets/images/header-logo.png'"
        />
      </div>
      <jrs-search-box *ngIf="courseType !== null" (search)="emitSearch($event)">
      </jrs-search-box>
      <span class="spacer"></span>

      <jrs-button
        *ngIf="announcementFlagEnabled"
        class="nav-toggle"
        [iconName]="'notifications'"
        [iconColor]="'dark'"
        [size]="'icon-large'"
        [isMaterial]="true"
        [noOutline]="true"
        (clicked)="navigateToNotifications()"
      >
        <div
          *ngIf="notification != null && notification != '0'"
          style="margin-right: 5px;"
        >
          <div class="notification">{{ notification }}</div>
        </div>
      </jrs-button>

      <!-- <jrs-button
        *ngIf="isDarkMode && !hideThemeToggle"
        class="nav-toggle"
        [iconName]="'wb_sunny'"
        [iconColor]="'dark'"
        [size]="'icon-large'"
        [isMaterial]="true"
        [noOutline]="true"
        (clicked)="this.changeTheme('default')">
      </jrs-button> -->
      <!-- <jrs-button
        *ngIf="!isDarkMode && !hideThemeToggle"
        class="nav-toggle"
        [iconName]="'nights_stay'"
        [iconColor]="'dark'"
        [size]="'icon-large'"
        [isMaterial]="true"
        [noOutline]="true"
        (clicked)="this.changeTheme('dark')"
      >
      </jrs-button> -->
      <jrs-circle-button
        class="calendar"
        [size]="'fs-23'"
        [isMaterial]="false"
        [iconName]="'jrs-calendar'"
        [color]="'none'"
        [iconColor]="'dark'"
        (click)="calendarTriggered.emit()"
      >
      </jrs-circle-button>
      <jrs-dropdown
        class="hidden-menu"
        [values]="hiddenMenuActions"
        (selectionChange)="menuSelected($event)"
        (opened)="isDropdownMenuOpen = !isDropdownMenuOpen"
        (outsideClicked)="isDropdownMenuOpen = !isDropdownMenuOpen"
      >
        <jrs-dropdown-input
          [size]="'medium'"
          [noOutline]="true"
          [icon]="'expand_more'"
          [fitContent]="true"
          [show]="isDropdownMenuOpen"
          [color]="'gray'"
        >
          <div text class="user-menu">
            <div class="username">{{ name }}</div>
            <div class="role">{{ role }}</div>
          </div>
        </jrs-dropdown-input>
      </jrs-dropdown>
    </div>
  `,
  styleUrls: ["./header-toolbar.component.scss"],
})
export class HeaderToolbarComponent implements OnChanges, OnInit {
  @Input() logoUrl: string;
  @Input() logo: string;
  @Input() darkLogoUrl: string;
  @Input() name: string;
  @Input() role: string;
  @Input() height: string;
  @Input() hiddenMenuActions: [];
  @Input() announcementFlagEnabled: boolean;
  @Input() notification: string;

  @Output() toggle = new EventEmitter<any>();
  @Output() calendarTriggered = new EventEmitter<any>();
  @Output() navigateHomeTriggered = new EventEmitter<any>();
  @Output() menuTriggered = new EventEmitter<any>();
  @Output() search = new EventEmitter<SearchModel>();
  @Output() navigateToNotifTriggered = new EventEmitter();

  isTabletMode = false;
  isDropdownMenuOpen = false;
  logoPath: string;
  // isDarkMode: boolean;
  hideThemeToggle = true;
  courseType: string;

  constructor(
    private breakPointService: BreakPointObserverService,
    private themeService: ThemeService,
    private router: Router
  ) {
    this.router.events.subscribe((res) => {
      if (res instanceof NavigationEnd) {
        if (res.url.includes(ONLINE_COURSE_SEARCH)) {
          this.courseType = ONLINE_COURSE_SEARCH;
        } else if (res.url.includes(CLASSROOM_COURSE_SEARCH)) {
          this.courseType = CLASSROOM_COURSE_SEARCH;
        } else if (res.url.includes(TIMED_COURSE_SEARCH)) {
          this.courseType = TIMED_COURSE_SEARCH;
        } else {
          this.courseType = null;
        }
      }
    });
  }

  ngOnInit(): void {
    this.breakPointService.getMediaBreakPointChange().subscribe((res) => {
      const currentMode =
        res == "media_xs" || res == "media_sm" || res == "media_s";
      // Dark mode toggle not visible currently
      // this.hideThemeToggle = res == 'media_xs';
      if (currentMode != this.isTabletMode) {
        this.isTabletMode = currentMode;
      }
      if (this.isTabletMode) {
        this.hiddenMenuActions.unshift();
      }
    });
  }

  ngOnChanges(): void {
    this.logoPath = this.logoUrl;
  }

  toggleNavbar() {
    this.toggle.emit();
  }

  menuSelected(menuItem): void {
    this.menuTriggered.emit(menuItem);
  }

  changeTheme(themeName: string): void {
    if (themeName == "dark") {
      this.logoPath = this.darkLogoUrl;
      // this.isDarkMode = true;
    } else {
      this.logoPath = this.logoUrl;
      // this.isDarkMode = false;
    }
    this.themeService.changeTheme(themeName);
  }

  emitSearch(searchModel: SearchModel): void {
    searchModel.courseType = this.courseType;
    this.search.emit(searchModel);
  }

  navigateToHome(): void {
    this.navigateHomeTriggered.emit();
  }

  navigateToNotifications(): void {
    this.navigateToNotifTriggered.emit();
  }
}
