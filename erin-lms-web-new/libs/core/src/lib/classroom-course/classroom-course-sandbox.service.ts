import {Inject, Injectable, Type} from '@angular/core';
import {ClassroomCourseService} from "./services/classroom-course.service";
import {forkJoin, Observable} from "rxjs";
import {
  ClassroomCourseAttendanceModel,
  ClassroomCourseModel,
  ClassroomCourseState,
  ClassroomCourseUpdateModel,
  ClassroomUsersModel,
  CreateClassroomCourseModel,
  CreateClassroomCourseTableModel,
  InstructorDropdownModel
} from "./model/classroom-course.model";
import {Router} from "@angular/router";
import {select, Store} from "@ngrx/store";
import {ApplicationState} from "../common/statemanagement/state/ApplicationState";
import {UserManagementService} from "../user-management/service/user-management.service";
import {GroupManagementService} from "../group-management/services/group-management.service";
import {SurveyService} from "../common/services/survey.service";
import {DetailedUserInfo, Survey} from "../common/common.model";
import {CertificateModel, ReceivedCertificateModel} from "../certificate/model/certificate.model";
import {CategoryItem, LearnerInfo} from "../../../../shared/src/lib/shared-model";
import {Location} from "@angular/common";
import {BreakPointObserverService} from "../../../../shared/src/lib/theme/services/break-point-observer.service";
import {map} from "rxjs/operators";
import {CertificateService} from "../certificate/service/certificate.service";
import {DialogConfig} from "../../../../shared/src/lib/dialog/dialog-config";
import {DialogService} from "../../../../shared/src/lib/dialog/dialog.service";
import {SnackbarService} from "../../../../shared/src/lib/snackbar/snackbar.service";
import {DialogRef} from "../../../../shared/src/lib/dialog/dialog-ref";
import {PermissionService} from "../common/services/permission.service";
import {ScormRuntimeService} from "../scorm/service/scorm-runtime.service";
import {ScoModel} from "../scorm/model/runtime-data.model";

@Injectable({
  providedIn: 'root'
})
export class ClassroomCourseSandboxService {
  username: string;
  category: CategoryItem;
  role: string;
  userRole$ = this.store.select(state => state.auth.role)
  userName$ = this.store.select(state => state.auth.userName)
  userGroup$ = this.store.pipe(select(state => {
    return state.auth.userGroups;
  }));
  currentUser$ = this.store.select(state => state.auth.userName);
  currentGroupId: string;
  currentUser: string;

  constructor(
    private service: ClassroomCourseService,
    private umService: UserManagementService,
    private gmService: GroupManagementService,
    private breakPointService: BreakPointObserverService,
    private router: Router,
    private location: Location,
    private permissionService: PermissionService,
    private store: Store<ApplicationState>,
    private surveyService: SurveyService,
    private certificateService: CertificateService,
    private scormRuntime: ScormRuntimeService,
    private dialogService: DialogService,
    private snackbarService: SnackbarService,
    @Inject('constants') public constants) {
    this.userName$.subscribe(res => this.username = res);
    this.userGroup$.subscribe(res => this.currentGroupId = res);
    this.currentUser$.subscribe(res => this.currentUser = res);
  }

  getMediaBreakPointChange(): Observable<string> {
    return this.breakPointService.getMediaBreakPointChange().pipe(map((res: any) => {
      return res;
    }))
  }

  goBack(): void {
    this.router.navigateByUrl('/classroom-course');
  }

  getCourseById(courseId: string): Observable<ClassroomCourseModel> {
    return this.service.getCourseById(courseId);
  }

  getCalendarData(date: string): Observable<any> {
    return this.service.getCalendarData(date);
  }

  createClassroomCourse(classroomCourse: CreateClassroomCourseModel, attachmentMimeType: string): Observable<any> {
    return this.service.createClassroomCourse(classroomCourse, attachmentMimeType);
  }

  getClassroomCourseCategories(): Observable<CategoryItem[]> {
    return this.service.getClassroomCourseCategories();
  }

  getInstructors(): Observable<InstructorDropdownModel[]> {
    return this.service.getInstructors();
  }

  getSurveys(): Observable<Survey[]> {
    return this.surveyService.getSurveys(false, true);
  }

  uploadAttachment(file: any): Observable<any> {
    return this.service.uploadAttachment(file)
  }

  getCertificates(): Observable<CertificateModel[]> {
    return this.certificateService.getCertificates();
  }

  navigateByUrl(url: string): void {
    this.router.navigateByUrl(url);
  }

  downloadAttachment(courseId: string, fileName: string, nameToShow: string): Observable<any> {
    return this.service.downloadAttachment(courseId, fileName, nameToShow);
  }

  getAllSupervisorEmployees(): Observable<LearnerInfo[]> {
    return this.umService.getAllSupervisorEmployees();
  }

  getClassroomCourseUsers(courseId: string): Observable<string[]> {
    return this.service.getClassroomCourseUsers(courseId);
  }

  updateSuggestedUsers(checkedUsers: string[], courseId: string): Observable<any> {
    return this.service.updateSuggestedUsers(checkedUsers, courseId)
  }

  getAllGroups(getFlatData: boolean): Observable<any> {
    return this.gmService.getAllUsersGroups(this.currentGroupId, getFlatData);
  }

  getAttendanceByCourseIdAndLearnerId(courseId: string): Observable<any> {
    return this.service.getAttendanceByCourseIdAndLearnerId(courseId, this.currentUser);
  }

  startClassroomCourse(id: string): Observable<any> {
    return this.service.updateClassroomCourseState(id, ClassroomCourseState.STARTED);
  }

  cancelClassroomCourse(id: string, result: string): Observable<any> {
    return this.service.updateClassroomCourseState(id, ClassroomCourseState.CANCELED, false, result);
  }

  postponeClassroomCourse(id: string, result: string): Observable<any> {
    return this.service.updateClassroomCourseState(id, ClassroomCourseState.POSTPONED, false, result);
  }

  deleteClassroomCourse(id: string): Observable<any> {
    return this.service.deleteClassroomCourse(id);
  }

  openDialog(component: Type<any>, config?: DialogConfig): DialogRef {
    return this.dialogService.open(component, config);
  }

  openSnackbar(text: string, success?: boolean): void {
    this.snackbarService.open(text, success)
  }

  getPermission(permissionString: string): boolean {
    return this.permissionService.getPermissionAccess(permissionString);
  }


  changeStateToSent(id: string): Observable<any> {
    return this.service.updateClassroomCourseState(id, ClassroomCourseState.SENT);
  }

  changeStateToReceived(id: string): Observable<any> {
    return this.service.updateClassroomCourseState(id, ClassroomCourseState.RECEIVED);
  }

  getReceivedCertificates(learnerId: string): Observable<ReceivedCertificateModel[]> {
    return this.certificateService.getReceivedCertificates(learnerId);
  }

  updateClassroomCourse(classroomCourse: ClassroomCourseUpdateModel): Observable<any> {
    return this.service.updateClassroomCourse(classroomCourse);
  }

  updateAttachment(id: string, attachmentId: string): Observable<any> {
    return this.service.updateAttachment(id, attachmentId);
  }


  getAllUsersWithRole(): Observable<LearnerInfo[]> {
    return this.umService.getAllUsersWithRole(false);
  }

  public getAllUsers(): Observable<DetailedUserInfo[]> {
    return this.umService.getAllUsers();
  }

  saveCourseLearners(learners: string[], courseId: string): Observable<any> {
    return this.service.saveCourseLearners(learners, courseId);
  }

  publishCourse(courseId: string): Observable<any> {
    return this.service.publishCourse(courseId);
  }

  enrollSuperLearners(departments: string[], newSuperLearners: string[], courseId: string, sendNotification: boolean, type: any): Observable<any> {
    return this.service.enrollSuperLearners(departments, newSuperLearners, courseId, sendNotification, type);
  }

  onScormSaveDataCalled(): Observable<any> {
    return this.scormRuntime.onSaveDataCalled();
  }

  getClassroomCourseById(id: string): Observable<ClassroomCourseModel> {
    return this.service.getCourseById(id);
  }

  getScoData(courseId: string): Observable<ScoModel[]> {
    return this.scormRuntime.getCourseScoData(courseId);
  }

  notifyScormDataSent(): void {
    return this.scormRuntime.notifyDataSent();
  }

  saveRuntimeData(courseId: string, data: ScoModel): Observable<any> {
    return this.scormRuntime.saveCourseRuntimeData(courseId, data);
  }

  updateClassroomSurveyStatus(courseId: string): Observable<any> {
    return this.service.updateClassroomSurveyStatus(courseId, this.currentUser);
  }

  downloadAttendance(id: string, attendances: ClassroomCourseAttendanceModel[]) {
    return this.service.downloadAttendance(id, attendances);
  }

  getMyClassroomUsers(courseId: string): Observable<ClassroomUsersModel> {
    const usersByGroupMap = this.gmService.getMyUsersByGroupMap(courseId, this.currentGroupId);
    const classroomUsers = this.getClassroomCourseUsers(courseId);

    return forkJoin([usersByGroupMap, classroomUsers]).pipe(map((res) => {
      const usersByGroup = res[0];
      const suggestedUsers = res[1];
      const allCurrentUsers = [];
      const notInGroupUsers = [];
      if (res[0]) {
        for (const user of suggestedUsers) {
          const foundUser = usersByGroup.allMyUsers.get(user);
          if (foundUser) {
            foundUser.clicked = true;
            allCurrentUsers.push(foundUser);
          } else {
            if (usersByGroup.allUsers.get(user)) {
              notInGroupUsers.push(user);
            }
          }
        }
      }
      return {allCurrentUsers, notInGroupUsers, usersByGroup};
    }))
  }

  getAttendance(id: string): Observable<ClassroomCourseAttendanceModel[]> {
    return this.service.getAttendance(id);
  }

  closeClassroomCourse(courseId: string, attendances: ClassroomCourseAttendanceModel[]): Observable<any> {
    return this.service.closeClassroomCourse(courseId, attendances);
  }

  updateAttendance(id: string, attendances: ClassroomCourseAttendanceModel[]) {
    return this.service.updateAttendance(id, attendances);
  }

  removeUser(courseId: string, username: string): Observable<any> {
    return this.service.removeUser(courseId, username);
  }

  updateClassroomCourseState(id: string, state: string, rollback?: boolean, reason?: string): Observable<any> {
    return this.service.updateClassroomCourseState(id, state, rollback, reason);
  }

  getClassroomCourses(categoryId: string, type: string, state: string): Observable<CreateClassroomCourseTableModel[]> {
    return this.service.getClassroomCourses(categoryId, type, state);
  }
}
