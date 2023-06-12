import {Inject, Injectable, Type} from '@angular/core';
import {DashboardService} from "./dashboard.service";
import {Observable} from "rxjs";
import {Location} from "@angular/common";
import {PermissionService} from "../../common/services/permission.service";
import {ApplicationState} from "../../common/statemanagement/state/ApplicationState";
import {select, Store} from "@ngrx/store";
import {GroupManagementService} from "../../group-management/services/group-management.service";
import {SmallDashletModel} from "../../../../../shared/src/lib/shared-model";
import {DetailedLearnerActivity, LearnerSuccessDashletModel} from "../dashboard-model/dashboard-model";
import {Router} from "@angular/router";
import {SnackbarService} from "../../../../../shared/src/lib/snackbar/snackbar.service";
import {DialogService} from "../../../../../shared/src/lib/dialog/dialog.service";
import {DialogRef} from "../../../../../shared/src/lib/dialog/dialog-ref";
import {DialogConfig} from "../../../../../shared/src/lib/dialog/dialog-config";
import {ReceivedCertificateModel} from "../../certificate/model/certificate.model";
import {CertificateService} from "../../certificate/service/certificate.service";
import {BreakPointObserverService} from "../../../../../shared/src/lib/theme/services/break-point-observer.service";

@Injectable({
  providedIn: 'root'
})
export class DashboardSandboxService {
  username: string;
  role: string;
  username$ = this.store.select(state => state.auth.userName);
  userGroup$ = this.store.pipe(select(state => {
    return state.auth.userGroups;
  }));
  currentGroupId: string;

  constructor(
    private router: Router,
    private service: DashboardService,
    private permissionService: PermissionService,
    private store: Store<ApplicationState>,
    private groupManagementService: GroupManagementService,
    private location: Location,
    private snackbarService: SnackbarService,
    private dialogService: DialogService,
    private certificateService: CertificateService,
    private breakPointService: BreakPointObserverService,
    @Inject('constants') public constants) {
    this.username$.subscribe(res => this.username = res);
    this.userGroup$.subscribe(res => this.currentGroupId = res);
    this.store.select(state => state.auth.role).subscribe(res => this.role = res);
  }

  getTimedCourseData(startDate: string, endDate: string, role: string): Observable<any> {
    return this.service.getTimedCourseData(startDate, endDate, role);
  }

  getOnlineCourseData(startDate: string, endDate: string, role: string): Observable<any> {
    return this.service.getOnlineCourseData(startDate, endDate, role);
  }

  getPermissionAccess(permissionId): boolean {
    return this.permissionService.getPermissionAccess(permissionId);
  }

  getAllGroups(getFlatData: boolean): Observable<any> {
    return this.groupManagementService.getAllUsersGroups(this.currentGroupId, getFlatData);
  }

  getOnlineCourseUserActivityData(groupId: string): Observable<SmallDashletModel[]> {
    return this.service.getOnlineCourseUserActivityData(groupId);
  }

  getOnlineCourseLearnerActivityData(groupId: string): Observable<DetailedLearnerActivity[]> {
    return this.service.getOnlineCourseLearnerActivityData(groupId);
  }

  getTimedCourseUserActivityData(groupId: string, asDashletData: boolean): Observable<SmallDashletModel[] | DetailedLearnerActivity[]> {
    return this.service.getTimedCourseUserActivityData(groupId, asDashletData);
  }

  goBack(): void {
    this.location.back();
  }

  openSnackbar(text: string, success?: boolean): void {
    this.snackbarService.open(text, success)
  }

  navigateByUrl(url: string): void {
    this.router.navigateByUrl(url);
  }

  navigate(url: string, usename: string): void {
    this.router.navigate([url], {queryParams: {id: usename}});
  }

  openDialog(component: Type<any>, config?: DialogConfig): DialogRef {
    return this.dialogService.open(component, config);
  }

  getReceivedCertificates(learnerId: string): Observable<ReceivedCertificateModel[]> {
    return this.certificateService.getReceivedCertificates(learnerId)
  }

  getMediaBreakPointChange(): Observable<any> {
    return this.breakPointService.getMediaBreakPointChange();
  }

  getOnlineLearnerSuccessData(year: string, halfYear: string, group: string): Observable<LearnerSuccessDashletModel> {
    return this.service.getOnlineLearnerSuccessData(year, halfYear, group);
  }

  getTimedLearnerSuccessData(year: string, halfYear: string, group: string): Observable<LearnerSuccessDashletModel> {
    return this.service.getTimedLearnerSuccessData(year, halfYear, group);
  }
}
