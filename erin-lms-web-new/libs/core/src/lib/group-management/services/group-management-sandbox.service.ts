import {Inject, Injectable, Type} from '@angular/core';
import {Router} from "@angular/router";
import {Location} from "@angular/common";
import {SnackbarService} from "../../../../../shared/src/lib/snackbar/snackbar.service";
import {DialogService} from "../../../../../shared/src/lib/dialog/dialog.service";
import {GroupManagementService} from "./group-management.service";
import {select, Store} from "@ngrx/store";
import {ApplicationState} from "../../common/statemanagement/state/ApplicationState";
import {UserManagementService} from "../../user-management/service/user-management.service";
import {PermissionService} from "../../common/services/permission.service";
import {Observable} from "rxjs";
import {DialogConfig} from "../../../../../shared/src/lib/dialog/dialog-config";
import {DialogRef} from "../../../../../shared/src/lib/dialog/dialog-ref";
import {Membership, Role} from "../../common/common.model";
import {User, UserCourses} from "../model/group.model";
import {TimedCourseService} from "../../timed-course/services/timed-course.service";
import {OnlineCourseService} from "../../online-course/services/online-course.service";
import {GroupNode} from "../../../../../shared/src/lib/shared-model";

@Injectable({
  providedIn: 'root'
})
export class GroupManagementSandboxService {
  private userGroup$ = this.store.pipe(select(state => {
    return state.auth.userGroups;
  }));
  private userRole$ = this.store.select(state => state.auth.role);
  private currentGroupId: string;
  public currentRole: string;

  constructor(
    private router: Router,
    private location: Location,
    private service: GroupManagementService,
    private userManagementService: UserManagementService,
    private timedCourseService: TimedCourseService,
    private onlineCourse: OnlineCourseService,
    private snackbarService: SnackbarService,
    private dialogService: DialogService,
    private permissionService: PermissionService,
    @Inject('constants') public constants,
    private store: Store<ApplicationState>) {
    this.userRole$.subscribe(res => this.currentRole = res);
    this.userGroup$.subscribe(res => this.currentGroupId = res);
  }

  getAllGroups(getFlatData: boolean): Observable<GroupNode[]> {
    return this.service.getAllUsersGroups(this.currentGroupId, getFlatData);
  }

  getAllUsers(): Observable<Map<string, User>> {
    return this.userManagementService.getAllUsersMap(false);
  }

  openDialog(component: Type<any>, config?: DialogConfig): DialogRef {
    return this.dialogService.open(component, config);
  }

  openSnackbar(text: string, success?: boolean): void {
    this.snackbarService.open(text, success)
  }

  getGroupMembers(id: string): Observable<Membership[]> {
    return this.service.getGroupMembers(id);
  }

  isPermitted(access: string): boolean {
    return this.permissionService.getPermissionAccess(access);
  }
  filterPermission(actions: any[]): any[] {
    return this.permissionService.filterWithPermission(actions);
  }

  deleteMember(membershipId: string): Observable<any> {
    return this.service.deleteMembership(membershipId);
  }

  deleteTimedCourseReaderships(userId: string): Observable<any> {
    return this.timedCourseService.deleteReaderships(userId);
  }

  getAllRoles(): Observable<Role[]> {
    return this.service.getAllRoles();
  }

  createMembers(groupId: string, roleId: string, users: User[]): Observable<any> {
    return this.service.createMembers(groupId, roleId, users);
  }

  createPromotionReadership(groupId: string, userId: string, newGroupId?: string): Observable<any> {
    return this.service.createPromotionReadership(groupId, userId, newGroupId);
  }

  deleteCourseRelations(id: string):Observable<any> {
    return this.onlineCourse.deleteCourseRelations(id);
  }

  deleteTimedCourseRelations(id: string): Observable<any> {
    return this.timedCourseService.deleteTimedCourseRelations(id);
  }

  deleteGroup(id): Observable<any> {
    return this.service.deleteGroup(id);
  }

  updateGroupName(id: string, name: string): Observable<any> {
    return this.service.updateGroupName(id, name);
  }

  addGroup(parent: string, groupName: string): Observable<any> {
    return this.service.addGroup(parent, groupName);
  }

  getLearnersReaderships(groupId: string, selectedUsers: User[]): Observable<UserCourses[]>  {
    return this.service.getLearnersReaderships(groupId, selectedUsers);
  }
}
