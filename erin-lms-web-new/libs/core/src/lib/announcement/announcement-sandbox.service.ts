import { Injectable, Type } from "@angular/core";
import { Router } from "@angular/router";
import { Store } from "@ngrx/store";
import { DialogConfig } from "libs/shared/src/lib/dialog/dialog-config";
import { DialogRef } from "libs/shared/src/lib/dialog/dialog-ref";
import { DialogService } from "libs/shared/src/lib/dialog/dialog.service";
import { DropdownModel } from "libs/shared/src/lib/dropdown/dropdownModel";
import { GroupNode } from "libs/shared/src/lib/shared-model";
import { SnackbarService } from "libs/shared/src/lib/snackbar/snackbar.service";
import { Observable, of } from "rxjs";
import { UserRoleProperties } from "../common/common.model";
import { PermissionService } from "../common/services/permission.service";
import { ApplicationState } from "../common/statemanagement/state/ApplicationState";
import { GroupManagementService } from "../group-management/services/group-management.service";
import { AnnouncementService } from "./announcement.service";
import {
  Announcement,
  AnnouncementStatistic,
} from "./model/announcement.model";
import { UpdateNotification } from "../common/statemanagement/actions/notification/notification";

@Injectable({
  providedIn: "root",
})
export class AnnouncementSandboxService {
  username: string;
  role: string;
  currentUser$ = this.store.select((state) => state.auth);
  currentGroup: string;
  constructor(
    private dialogService: DialogService,
    private snackbarService: SnackbarService,
    private store: Store<ApplicationState>,
    private permissionService: PermissionService,
    private router: Router,
    private groupManagementService: GroupManagementService,
    private service: AnnouncementService
  ) {
    this.currentUser$.subscribe((res) => {
      this.username = res.userName;
      this.role = res.role;
      this.currentGroup = res.userGroups;
    });
  }

  openDialog(component: Type<any>, config: DialogConfig): DialogRef {
    return this.dialogService.open(component, config);
  }

  snackbarOpen(text: string, success = true) {
    this.snackbarService.open(text, success);
  }

  createAnnouncement(announcement: Announcement): Observable<string> {
    return this.service.createAnnouncement(this.username, announcement);
  }

  getAllAnnouncements(
    startDate: string,
    endDate: string
  ): Observable<Announcement[]> {
    if (this.role == UserRoleProperties.adminRole.id) {
      return this.service.getAllAnnouncements(startDate, endDate);
    } else {
      return this.service.getAnnouncements(startDate, endDate);
    }
  }

  getAnnouncementById(id: string): Observable<Announcement> {
    return this.service.getAnnouncementById(id);
  }

  updateAnnouncement(announcement: Announcement): Observable<boolean> {
    return this.service.updateAnnouncement(announcement);
  }

  publishAnnouncement(id: string): Observable<boolean> {
    return this.service.publishAnnouncement(id);
  }

  getAnnouncementNotification(): void {
    this.service.getAnnouncementNotification().subscribe((res) => {
      this.store.dispatch(
        new UpdateNotification({ name: "announcement", value: res })
      );
    });
  }

  filterWithPermission(actions: DropdownModel[]): any[] {
    return this.permissionService.filterWithPermission(actions);
  }

  deleteAnnouncement(id: string): Observable<any> {
    return this.service.deleteAnnouncement(id);
  }

  navigate(url: string): void {
    this.router.navigateByUrl(url);
  }

  getStatistics(id: string): Observable<{
    totalView: number;
    statistics: AnnouncementStatistic[];
  }> {
    return this.service.getStatistics(id);
  }

  getAllGroups(getFlatData: boolean): Observable<GroupNode[]> {
    return this.groupManagementService.getAllUsersGroups(
      this.currentGroup,
      getFlatData
    );
  }

  viewAnnouncement(id: string): Observable<boolean> {
    return this.service.viewAnnouncement(id);
  }

  getAnnouncementEnrolledGroups(id: string): Observable<GroupNode[]> {
    return this.service.getAnnouncementEnrolledGroups(id);
  }
}
