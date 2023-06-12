import {Injectable, Type} from '@angular/core';
import {UserManagementService} from "./user-management.service";
import {map} from "rxjs/operators";
import {Observable} from "rxjs";
import {DialogService} from "../../../../../shared/src/lib/dialog/dialog.service";
import {DialogConfig} from "../../../../../shared/src/lib/dialog/dialog-config";
import {DialogRef} from "../../../../../shared/src/lib/dialog/dialog-ref";
import {ArchiveUserModel, UserCreateModel, UserDetailedModel} from "../models/user-management.model";
import {SnackbarService} from "../../../../../shared/src/lib/snackbar/snackbar.service";
import {ProfileService} from "./profile.service";
import {Location} from "@angular/common";
import {BreakPointObserverService} from "../../../../../shared/src/lib/theme/services/break-point-observer.service";
import {Field, UserProfile} from "../models/profile.model";
import {HistoryService} from "./history.service";

@Injectable({
  providedIn: 'root'
})
export class UserManagementSandboxService {

  constructor(
    private service: UserManagementService,
    private dialogService: DialogService,
    private snackbarService: SnackbarService,
    private profileService: ProfileService,
    private historyService: HistoryService,
    private location: Location,
    private breakPointService: BreakPointObserverService) {
  }

  getFields(organizationId: string): Observable<Field[]> {
    return this.service.getFields(organizationId);
  }

  canEdit(): Observable<boolean> {
    return this.service.getRealmType().pipe(map(res => {
      return res == "MONGO"
    }))
  }

  openDialog(component: Type<any>, config: DialogConfig): DialogRef {
    return this.dialogService.open(component, config);
  }

  createUser(newUser: UserCreateModel): Observable<any> {
    return this.service.createUser(newUser);
  }

  doesUserExist(username: string): Observable<boolean> {
    return this.service.doesUserExist(username);
  }

  deleteUser(userId: string): Observable<boolean> {
    return this.service.deleteUser(userId);
  }

  deleteUsers(userIds: string[]): Observable<boolean> {
    return this.service.deleteUsers(userIds);
  }

  archiveUser(userId: string, archived: boolean): Observable<boolean> {
    return this.service.archiveUser(userId, archived);
  }

  archiveUsers(users: ArchiveUserModel): Observable<boolean> {
    return this.service.archiveUsers(users);
  }

  getUsers(): Observable<UserDetailedModel[]> {
    return this.service.getUsers();
  }

  importUsers(file: any): Observable<any> {
    return this.service.importUsers(file);
  }

  exportUsers(): Observable<any> {
   return  this.service.exportUsers();
  }

  getUserProfile(userName): Observable<any> {
    return this.profileService.getUserProfile(userName);
  }

  updateProfile(profile: UserProfile): Observable<any> {
    return this.profileService.updateUserProfile(profile);
  }

  openSnackbar(text: string, success = true): void {
    this.snackbarService.open(text, success);
  }

  getMediaBreakPointChange(): Observable<any> {
    return this.breakPointService.getMediaBreakPointChange();
  }

  goBack(): void {
    this.location.back();
  }

  updateUser(updateUser: UserCreateModel): Observable<boolean> {
    return this.service.updateUser(updateUser);
  }
}
