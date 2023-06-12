import { Inject, Injectable, ViewContainerRef } from "@angular/core";
import { ActivatedRoute, NavigationExtras, Router } from "@angular/router";
import { Title } from "@angular/platform-browser";
import { select, Store } from "@ngrx/store";
import { ApplicationState } from "./statemanagement/state/ApplicationState";
import { PermissionService } from "./services/permission.service";
import { AuthenticationService } from "../authentication/services/authentication/authentication.service";
import { AnnouncementService } from "../announcement/announcement.service";
import { CourseNotificationService } from "./services/course-notification.service";
import { AddMultipleNotifications, SetNotification, UpdateNotification } from "./statemanagement/actions/notification/notification";
import { Observable } from "rxjs";
import { TranslateService } from "@ngx-translate/core";
import { SnackbarService } from "../../../../shared/src/lib/snackbar/snackbar.service";
import { DialogService } from "../../../../shared/src/lib/dialog/dialog.service";
import { OnlineCourseService } from "../online-course/services/online-course.service";
import { ClassroomCourseService } from "../classroom-course/services/classroom-course.service";
import { TimedCourseService } from "../timed-course/services/timed-course.service";
import { CategoryItem } from "../../../../shared/src/lib/shared-model";
import { SearchService } from "./services/search.service";
import {SystemSettingsService} from "../settings/services/system-settings.service";

@Injectable({
  providedIn: "root",
})
export class CommonSandboxService {
  role: string;
  hasPromoNotifPermission: boolean;
  authRole$ = this.store.pipe(
    select((state) => {
      if (state.auth) {
        return state.auth.role;
      }
      return undefined;
    })
  );
  notification$ = this.store.pipe(
    select((state) => {
      if (state.notification) {
        return state.notification.categories;
      }
    })
  );

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private titleService: Title,
    private store: Store<ApplicationState>,
    private permission: PermissionService,
    private auth: AuthenticationService,
    private translate: TranslateService,
    private dialogService: DialogService,
    private snackbarService: SnackbarService,
    private onlineCourseService: OnlineCourseService,
    private classroomCourseService: ClassroomCourseService,
    private timedCourseService: TimedCourseService,
    private notificationService: CourseNotificationService,
    private searchService: SearchService,
    private logo: SystemSettingsService,
    private announcementService: AnnouncementService,
    @Inject("version") public appVersion: string,
    @Inject("constants") public constants
  ) {
    this.authRole$.subscribe((res) => {
      this.role = res;
      this.hasPromoNotifPermission = this.permission.getPermissionAccess(
        "user.promotion.notification"
      );
    });
  }

  getRouterService(): Router {
    return this.router;
  }

  getLogoImageUrl(): Observable<string> {
    return this.logo.getSystemImageUrl(true);
  }


  getRouteService(): ActivatedRoute {
    return this.route;
  }

  getTitleService(): Title {
    return this.titleService;
  }

  getStore(): Store<ApplicationState> {
    return this.store;
  }

  getPermissionService(): PermissionService {
    return this.permission;
  }

  getAuthService(): AuthenticationService {
    return this.auth;
  }

  getNotification(): void {
    if (this.hasPromoNotifPermission) {
      this.notificationService.getNotification().subscribe((res) => {
        this.store.dispatch(new AddMultipleNotifications(res));
      });
    }
    this.announcementService.getAnnouncementNotification().subscribe((res) => {
      this.store.dispatch(new UpdateNotification({name: "announcement", value: res}));
    })
  }

  getTranslation(value: string): Observable<any> {
    return this.translate.get(value);
  }

  setViewContainerRef(VCR: ViewContainerRef): void {
    this.dialogService.setViewContainerRef(VCR);
    this.snackbarService.setViewContainerRef(VCR);
  }

  navigateByUrl(url: string): void {
    this.router.navigateByUrl(url);
  }

  navigate(urls: string[], data?: NavigationExtras): void {
    this.router.navigate(urls, data);
  }

  getOnlineCourseCategories(): Observable<CategoryItem[]> {
    return this.onlineCourseService.getOnlineCourseCategories();
  }

  getClassroomCourseCategories(): Observable<CategoryItem[]> {
    return this.classroomCourseService.getClassroomCourseCategories();
  }

  getTimedCourseCategories(): Observable<CategoryItem[]> {
    return this.timedCourseService.getTimedCourseCategories();
  }

  searchByCourseCategory(
    categoryId: string,
    isTimedCourse: boolean,
    categoryName?: string
  ): Observable<any> {
    return this.searchService.searchByCourseCategory(
      categoryId,
      isTimedCourse,
      categoryName
    );
  }

  searchCourses(
    searchValue: string,
    byName: boolean,
    byDescription: boolean,
    isTimedCourse: boolean
  ): Observable<any> {
    return this.searchService.searchCourses(
      searchValue,
      byName,
      byDescription,
      isTimedCourse
    );
  }

  openSnackbar(text: string, success?: boolean): void {
    this.snackbarService.open(text, success);
  }

}
