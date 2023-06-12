import {
  AfterViewInit,
  Component,
  OnInit,
  ViewChild,
  ViewContainerRef,
} from "@angular/core";
import {
  LayoutSidenavComponent,
  NavItem,
} from "../../../../../../shared/src/lib/layout/layout-side-navigation/layout-side-navigation.component";
import { select } from "@ngrx/store";
import { CommonSandboxService } from "../../common-sandbox.service";
import { NavigationExtras } from "@angular/router";
import { Notif } from "../../common.model";
import { Subscription } from "rxjs";

@Component({
  selector: "jrs-app-layout",
  template: `
    <div>
      <jrs-header-toolbar
        [role]="currentRole"
        [logoUrl]="logoUrl"
        [darkLogoUrl]="darkLogoUrl"
        [logo]="getLogo"
        [name]="currentUsername"
        [hiddenMenuActions]="menuNavItems"
        [announcementFlagEnabled]="true"
        [notification]="announcementNotification"
        (calendarTriggered)="navigateToCalendar()"
        (navigateHomeTriggered)="navigateToHome()"
        (menuTriggered)="selectMenuItem($event)"
        (toggle)="navigationToggled()"
        (navigateToNotifTriggered)="navigateToNotifications()"
        (search)="search($event)"
      >
      </jrs-header-toolbar>
      <jrs-layout-sidenav #sidenav [navItems]="navItems" [isOpened]="opened">
        <router-outlet></router-outlet>
      </jrs-layout-sidenav>
    </div>
    <ng-template #viewContainerRef></ng-template>
  `,
})
export class AppLayoutComponent implements OnInit, AfterViewInit {
  @ViewChild("sidenav", { static: true })
  public sidenav: LayoutSidenavComponent;
  @ViewChild("viewContainerRef", { read: ViewContainerRef })
  VCR: ViewContainerRef;
  currentPageName;
  currentUsername = "";
  currentRole = "";
  navItems: NavItem[] = [];
  menuNavItems = [];
  calendarItem;
  opened = false;
  logoUrl = "";
  darkLogoUrl = "";
  getLogo = "";
  LOGOUT_ITEM = {
    name: "Гарах",
    link: "/login",
    iconName: "",
    id: "app.navigation.logout",
  };
  announcementNotification: string;
  notifSub: Subscription;

  constructor(private sb: CommonSandboxService) {
    this.sb.getNotification();
    this.sb
      .getStore()
      .pipe(
        select((state) => {
          if (state.auth) {
            this.currentUsername = state.auth.userName;
            this.currentRole = state.auth.role;
            this.sb.getTranslation(state.auth.role).subscribe((res) => {
              this.currentRole = res;
            });
          }
        })
      )
      .subscribe();

    this.sb.getRouterService().events.subscribe(() => {
      this.currentPageName = this.sb.getRouteService().firstChild.firstChild
        ? this.sb.getRouteService().firstChild.firstChild.snapshot.data.title
        : this.sb.getRouteService().firstChild.snapshot.data.title;
      this.sb.getTitleService().setTitle(this.currentPageName);
    });
  }

  ngAfterViewInit(): void {
    this.sb.setViewContainerRef(this.VCR);
  }

  private loadLogo(): void {
    this.sb.getLogoImageUrl().subscribe((res) => {
      this.getLogo = res;
    });
  }

  ngOnInit(): void {
    this.loadLogo();
    this.navItems = this.sb
      .getPermissionService()
      .filterWithPermission(this.sb.constants.NAVIGATION_ITEMS);
    this.menuNavItems = this.sb
      .getPermissionService()
      .filterWithPermission(this.sb.constants.HIDDEN_NAV_ITEMS);
    this.menuNavItems.push(this.LOGOUT_ITEM);
    this.calendarItem = this.sb.constants.CALENDAR_NAV_ITEM;
    this.logoUrl = this.sb.constants.LOGO_URL;
    this.darkLogoUrl = this.sb.constants.DARK_LOGO_URL;

    this.navItems.forEach((category) => {
      if (category.id == "app.navigation.promotion") {
        this.notifSub = this.sb.notification$.subscribe((res) => {
          if (res != null) {
            category.notification = this.assignNotif(
              this.sb.constants.TIMED_COURSE_NAME,
              res
            );
            this.announcementNotification = this.assignNotif(
              "announcement",
              res
            );
          }
        });
      }
    });

    if (!this.notifSub) {
      this.notifSub = this.sb.notification$.subscribe((res) => {
        if (res != null) {
          this.announcementNotification = this.assignNotif("announcement", res);
        }
      });
    }
  }

  private assignNotif(type: string, res: Map<string, Notif>): string {
    let notif;
    const notificationCount = res.get(type).userNewTotal;
    if (notificationCount > 99) {
      notif = "99+";
    } else {
      notif = notificationCount.toString();
    }
    return notif;
  }

  navigationToggled(): void {
    this.sidenav.openNav();
  }

  navigateToCalendar(): void {
    this.sb.getRouterService().navigateByUrl(this.calendarItem.link);
  }

  navigateToHome(): void {
    this.sb.getRouterService().navigateByUrl("/");
  }

  selectMenuItem(menuItem): void {
    this.sb.getRouterService().navigateByUrl(menuItem.link);
  }

  search(searchModel): void {
    const data: NavigationExtras = {
      queryParams: {
        searchValue: searchModel.searchValue,
        byName: searchModel.byName,
        byDescription: searchModel.byDescription,
        byCategory: searchModel.byCategory,
        courseType: searchModel.courseType,
      },
    };
    this.sb.navigate(["/search-result/"], data);
  }

  navigateToNotifications() {
    this.sb.navigateByUrl("/announcement");
  }
}
