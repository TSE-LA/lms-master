import {Inject, Injectable, Type} from '@angular/core';
import {TimedCourseService} from "./timed-course.service";
import {Observable, Subject} from "rxjs";
import {CategoryItem, GroupNode, LearnerInfo, TimedCourseModel} from "../../../../../shared/src/lib/shared-model";
import {select, Store} from "@ngrx/store";
import {ApplicationState} from "../../common/statemanagement/state/ApplicationState";
import {CourseNotificationService} from "../../common/services/course-notification.service";
import {AddMultipleNotifications, SetNotification} from "../../common/statemanagement/actions/notification/notification";
import {BreakPointObserverService} from "../../../../../shared/src/lib/theme/services/break-point-observer.service";
import {Router} from "@angular/router";
import {DropdownModel} from "../../../../../shared/src/lib/dropdown/dropdownModel";
import {PermissionService} from "../../common/services/permission.service";
import {DialogService} from "../../../../../shared/src/lib/dialog/dialog.service";
import {DialogConfig} from "../../../../../shared/src/lib/dialog/dialog-config";
import {DialogRef} from "../../../../../shared/src/lib/dialog/dialog-ref";
import {SnackbarService} from "../../../../../shared/src/lib/snackbar/snackbar.service";
import {GroupManagementService} from "../../group-management/services/group-management.service";
import {TimedCourseStatisticBundleModel} from "../models/timed-course.model";
import {Location} from "@angular/common";
import {FileAttachment, StructureModule} from "../../../../../shared/src/lib/structures/content-structure/content-structure.model";
import {ContentModule} from "../../common/common.model";
import {TimedCourseStructure} from "../models/timed-course.model";
import {PublishingModel, TestModel} from "../../online-course/models/online-course.model";
import {UserManagementService} from "../../user-management/service/user-management.service";
import {ScoModel} from "../../scorm/model/runtime-data.model";
import {ScormRuntimeService} from "../../scorm/service/scorm-runtime.service";


@Injectable({
  providedIn: 'root'
})
export class TimedCourseSandboxService {
  role: string;
  userRole$ = this.store.select(state => state.auth.role)
  courseNotification$ = this.store.pipe(select(state => {
    if (state.notification) {
      return state.notification.categories;
    }
  }));
  private selectedCategory: CategoryItem;
  private selectedType: DropdownModel;
  private userGroup$ = this.store.pipe(select(state => state.auth.userGroups));
  private currentGroupId: string;

  constructor(
    private service: TimedCourseService,
    private scormRuntime: ScormRuntimeService,
    private notificationService: CourseNotificationService,
    private breakPointService: BreakPointObserverService,
    private router: Router,
    private permissionService: PermissionService,
    private dialogService: DialogService,
    private snackbarService: SnackbarService,
    private location: Location,
    private groupManagementService: GroupManagementService,
    private userManagementService: UserManagementService,
    @Inject('constants') public constants,
    private store: Store<ApplicationState>) {
    this.userRole$.subscribe(role => this.role = role)
    this.userGroup$.subscribe(res => this.currentGroupId = res);
  }

  getTimedCourseCategories(): Observable<CategoryItem[]> {
    return this.service.getTimedCourseCategories();
  }

  getTimedCourseData(categoryId, categoryName, state, status): Observable<TimedCourseModel[]> {
    return this.service.getTimedCourseData(categoryId, categoryName, state, status);
  }

  getTimedCourseById(courseId: string): Observable<TimedCourseModel> {
    return this.service.getTimedCourseById(courseId);
  }

  getTimedCourseStatistics(courseId: string, startDate: string, endDate: string, groupId?: string): Observable<TimedCourseStatisticBundleModel> {
    return this.service.getTimeCourseStatistics(courseId, startDate, endDate, groupId);
  }

  getNotification(): void {
    this.notificationService.getNotification().subscribe(res => {
      this.store.dispatch(new AddMultipleNotifications(res));
    });
  }

  createTimedCourse(timedCourse: TimedCourseModel): Observable<any> {
    return this.service.createTimedCourse(timedCourse);
  }

  updateNotification(courseId: string): void {
    this.notificationService.updateNotification(courseId).subscribe(res => {
      this.store.dispatch(new SetNotification(res));
    });
  }

  getMediaBreakPointChange(): Observable<any> {
    return this.breakPointService.getMediaBreakPointChange()
  }

  navigateByUrl(url: string) {
    this.router.navigateByUrl(url);
  }

  filterWithPermission(actions: DropdownModel[]): any[] {
    return this.permissionService.filterWithPermission(actions);
  }

  openDialog(component: Type<any>, config: DialogConfig): DialogRef {
    return this.dialogService.open(component, config);
  }

  openSnackbar(text: string, success?: boolean) {
    this.snackbarService.open(text, success);
  }

  deleteTimedCourse(id: string): Observable<any> {
    return this.service.deleteTimedCourse(id);
  }

  hideTimedCourse(id: string): Observable<any> {
    return this.service.hideTimedCourse(id);
  }

  cloneTimedCourse(id: string, timedCourse: TimedCourseModel): Observable<any> {
    return this.service.cloneTimedCourse(id, timedCourse);
  }

  getSelectedCategory(): CategoryItem {
    return this.selectedCategory;
  }

  setSelectedCategory(value: CategoryItem) {
    this.selectedCategory = value;
  }

  getSelectedType(): DropdownModel {
    return this.selectedType;
  }

  setSelectedType(value: DropdownModel) {
    this.selectedType = value;
  }

  getPermission(permissionString: string): boolean {
    return this.permissionService.getPermissionAccess(permissionString);
  }

  updateTimedCourse(course: TimedCourseModel): Observable<any> {
    return this.service.updateTimedCourse(course);
  }

  getTargets(): any[] {
    return this.constants.TIMED_COURSE_TARGET;
  }

  getCourseTypes(): DropdownModel[] {
    return this.constants.TIMED_COURSE_STATE;
  }

  getPdfSplitPercentage(): Subject<number> {
    return this.service.getPdfSplitPercentage();
  }

  getSection(file: File, name: string, id: string): Observable<StructureModule> {
    return this.service.getSection(file, name, id);
  }

  saveContentStructure(contents: FileAttachment[], additional: FileAttachment[], structure: StructureModule[], id: string): Observable<TimedCourseStructure> {
    return this.service.saveStructure(contents, additional, structure, id);
  }

  updateContentStructure(contents: FileAttachment[], additional: FileAttachment[], structure: StructureModule[], id: string): Observable<TimedCourseStructure> {
    return this.service.updateStructure(contents, additional, structure, id);
  }

  uploadFile(file: File, name: string, id: string): Observable<any> {
    return this.service.uploadFile(file, name, id)
  }

  getTimedCourseTest(id: string): Observable<TestModel> {
    return this.service.getTimedCourseTest(id);
  }

  saveCourseTest(id: string, testModel: TestModel): Observable<any> {
    return this.service.saveCourseTest(id, testModel);
  }

  updateCourseTest(id: string, testModel: TestModel): Observable<any> {
    return this.service.updateCourseTest(id, testModel);
  }

  uploadAttachment(file: File): Observable<any> {
    return this.service.uploadAttachment(file);
  }

  deleteAttachment(id: string, type: string, courseId: string): Observable<any> {
    return this.service.deleteAttachments(id, type, courseId, false);
  }

  getAllGroups(getFlatData: boolean): Observable<GroupNode[]> {
    return this.groupManagementService.getAllUsersGroups(this.currentGroupId, getFlatData);
  }

  getAllUsers(): Observable<LearnerInfo[]> {
    return this.userManagementService.getAllUsersWithRole(false);
  }

  saveEnrollment(addedUsers: string[], addedGroupIds: string[], id: string): Observable<any> {
    return this.service.saveEnrollments(addedUsers, addedGroupIds, id);
  }

  publishCourse(courseId: string, publish: PublishingModel): Observable<any> {
    return this.service.publishCourse(courseId, publish);
  }

  goBack(): void {
    this.location.back();
  }

  downloadStatistics(promotionId: string, startDate: string, endDate: string, groupId?: string): Observable<any> {
    return this.service.downloadStatistics(promotionId, startDate, endDate, groupId);
  }

  getRouterService(): Router {
    return this.router;
  }

  saveRuntimeData(scormContentId: string, data: ScoModel): Observable<any> {
    return this.scormRuntime.saveRuntimeData(scormContentId, data)
  }

  getScoData(id: string): Observable<ScoModel[]> {
    return this.scormRuntime.getScoData(id);
  }

  getTimedCourseStructure(id: string): Observable<TimedCourseStructure> {
    return this.service.getTimedCourseStructure(id);
  }

  onScormSaveDataCalled(): Observable<any> {
    return this.scormRuntime.onSaveDataCalled();
  }

  notifyScormDataSent(): void {
    return this.scormRuntime.notifyDataSent();
  }

  convertEnrollmentToReadership(startDate: Date, endDate: Date, state: string): Observable<any> {
    return this.service.convertEnrollmentToReadership(startDate, endDate, state);
  }

  getTimedCourseContent(timedCourseId: string): Observable<ContentModule[]> {
    return this.service.getTimedCourseContent(timedCourseId);
  }

  sendUpdateNotification(previousCode: string, previousName: string, id: string): Observable<any> {
    return this.service.sendUpdateNotification(previousCode, previousName, id);
  }

  getTimedCourseEnrollmentGroup(timedCourseId: string): Observable<GroupNode[]> {
    return this.service.getTimedCourseEnrollmentGroup(timedCourseId);
  }
}
