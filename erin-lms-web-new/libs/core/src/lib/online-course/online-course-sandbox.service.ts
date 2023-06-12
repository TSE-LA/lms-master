import {Inject, Injectable, Type} from '@angular/core';
import {OnlineCourseService} from "./services/online-course.service";
import {Observable, Subject} from "rxjs";
import {
  AttachmentModel,
  OnlineCourseModel,
  OnlineCourseStatistic,
  OnlineCourseStatisticBundleModel,
  PublishingModel,
  TestModel
} from "./models/online-course.model";
import {BreakPointObserverService} from "../../../../shared/src/lib/theme/services/break-point-observer.service";
import {select, Store} from "@ngrx/store";
import {ApplicationState} from "../common/statemanagement/state/ApplicationState";
import {ScormRuntimeService} from "../scorm/service/scorm-runtime.service";
import {NavigationExtras, Router} from "@angular/router";
import {Location} from "@angular/common";
import {SurveyService} from "../common/services/survey.service";
import {Survey} from "../common/common.model";
import {CertificateService} from "../certificate/service/certificate.service";
import {CertificateModel} from "../certificate/model/certificate.model";
import {DialogService} from "../../../../shared/src/lib/dialog/dialog.service";
import {SnackbarService} from "../../../../shared/src/lib/snackbar/snackbar.service";
import {PermissionService} from "../common/services/permission.service";
import {DialogConfig} from "../../../../shared/src/lib/dialog/dialog-config";
import {DropdownModel} from "../../../../shared/src/lib/dropdown/dropdownModel";
import {DialogRef} from "../../../../shared/src/lib/dialog/dialog-ref";
import {ScoModel} from "../scorm/model/runtime-data.model";
import {
  CourseStructure, FileAttachment,
  StructureModule
} from "../../../../shared/src/lib/structures/content-structure/content-structure.model";
import {CategoryItem, GroupNode, LearnerInfo} from "../../../../shared/src/lib/shared-model";
import {GroupManagementService} from "../group-management/services/group-management.service";
import {UserManagementService} from "../user-management/service/user-management.service";


@Injectable({
  providedIn: 'root'
})
export class OnlineCourseSandboxService {
  category: CategoryItem;
  role: string;
  username: string;
  thumbnailFileName: string;
  authRole$ = this.store.pipe(select(state => {
    if (state.auth) {
      return state.auth.role;
    }
    return undefined;
  }));
  private username$ = this.store.select(state => state.auth.userName);
  private role$ = this.store.select(state => state.auth.role);
  private userGroup$ = this.store.pipe(select(state => state.auth.userGroups));
  private currentGroupId: string;

  constructor(
    @Inject('constants') public constants,
    @Inject('baseUrl') public baseUrl: string,
    public snackbarService: SnackbarService,
    public certificateService: CertificateService,
    private router: Router,
    private service: OnlineCourseService,
    private location: Location,
    private surveyService: SurveyService,
    private dialogService: DialogService,
    private permissionService: PermissionService,
    private breakPointService: BreakPointObserverService,
    private groupManagementService: GroupManagementService,
    private scormRuntime: ScormRuntimeService,
    private userManagementService: UserManagementService,
    private store: Store<ApplicationState>) {
    this.username$.subscribe(res => this.username = res);
    this.role$.subscribe(res => this.role = res);
    this.userGroup$.subscribe(res => this.currentGroupId = res);
  }

  getOnlineCourses(categoryId: string, type: string, publishStatus: string): Observable<OnlineCourseModel[]> {
    return this.service.getOnlineCourses(categoryId, type, publishStatus, this.role);
  }

  getOnlineCourseCategories(): Observable<CategoryItem[]> {
    return this.service.getOnlineCourseCategories();
  }

  getOnlineCourseById(onlineCourseId: any): Observable<OnlineCourseModel> {
    return this.service.getOnlineCourseById(onlineCourseId);
  }

  getMediaBreakPointChange(): Observable<any> {
    return this.breakPointService.getMediaBreakPointChange()
  }

  getPermission(id: string): boolean {
    return this.permissionService.getPermissionAccess(id);
  }

  getOnlineCourseStructure(onlineCourseId): Observable<CourseStructure> {
    return this.service.getOnlineCourseStructure(onlineCourseId);
  }

  getAttachment(onlineCourseId: string): Observable<AttachmentModel[]> {
    return this.service.getAttachment(onlineCourseId);
  }

  getRouterService(): Router {
    return this.router;
  }

  goBack(): void {
    this.location.back();
  }

  getSelectedCategory(): CategoryItem {
    return this.category;
  }

  setSelectedCategory(category: CategoryItem): void {
    this.category = category;
  }

  navigateByUrl(url: string): void {
    this.router.navigateByUrl(url);
  }

  navigate(urls: string[], data: NavigationExtras): void {
    this.router.navigate(urls, data);
  }

  getCertificateTemplates(): Observable<CertificateModel[]> {
    return this.certificateService.getCertificates();
  }

  getCourseSurveyTemplates(): Observable<Survey[]> {
    return this.surveyService.getSurveys(false, true);
  }

  createOnlineCourse(course: OnlineCourseModel): Observable<string> {
    return this.service.createOnlineCourse(course);
  }

  deleteOnlineCourse(courseId: string): Observable<any> {
    return this.service.deleteOnlineCourse(courseId);
  }

  openDialog(component: Type<any>, config?: DialogConfig): DialogRef {
    return this.dialogService.open(component, config);
  }

  openSnackbar(text: string, success?: boolean): void {
    this.snackbarService.open(text, success)
  }

  filterWithPermission(actions: DropdownModel[]): any[] {
    return this.permissionService.filterWithPermission(actions);
  }

  hideOnlineCourse(courseId: string): Observable<any> {
    return this.service.hideOnlineCourse(courseId);
  }

  getScoData(courseId: string): Observable<ScoModel[]> {
    return this.scormRuntime.getCourseScoData(courseId)
  }

  saveRuntimeData(courseId: string, data: ScoModel): Observable<any> {
    return this.scormRuntime.saveCourseRuntimeData(courseId, data);
  }

  onScormSaveDataCalled(): Observable<any> {
    return this.scormRuntime.onSaveDataCalled();
  }

  getSuggestedOnlineCourses(categoryId: string, type: string, publishStatus: string, courseCount: string): Observable<OnlineCourseModel[]> {
    return this.service.getSuggestedOnlineCourses(categoryId, type, publishStatus, courseCount, this.role)
  }

  notifyScormDataSent(): void {
    return this.scormRuntime.notifyDataSent();
  }

  getCourseTypes(): any[] {
    return this.constants.COURSE_TYPES;
  }

  uploadFile(file: any, name: string, courseId: string): Observable<any> {
    return this.service.uploadFile(file, name, courseId);
  }

  getVideoConverterPercentage(): void {

  }

  updateOnlineCourse(course: OnlineCourseModel, courseId: string, sendNotification: boolean): Observable<any> {
    return this.service.updateOnlineCourse(course, courseId, sendNotification);
  }

  updateThumbnail(thumbnailId: string, courseId: string) {
    return this.service.updateThumbnail(thumbnailId, courseId);
  }

  uploadThumbnail(file: File): Observable<any> {
    return this.service.uploadThumbnail(file);
  }

  updateProperties(course: OnlineCourseModel, courseId: string): Observable<any> {
    return this.service.updateProperty(course, courseId);
  }

  getPdfSplitPercentage(): Subject<number> {
    return this.service.getPdfSplitPercentage();
  }

  getSection(content: File, sectionName: string, courseId: string): Observable<StructureModule> {
    return this.service.getSection(content, sectionName, courseId);
  }

  getServerSentEvent(): Subject<EventSource> {
    return this.service.getServerSentEvent();
  }

  convertVideo(filePath: string, fileId: string, sectionName: string, file: File): Observable<StructureModule> {
    return this.service.convertVideo(filePath, fileId, sectionName, file);
  }

  getCodecConvertingPercentage(): Subject<number> {
    return this.service.getCodecConvertingPercentage();
  }

  saveContentStructure(attachments: FileAttachment[], currentStructure: StructureModule[], courseId: string): Observable<CourseStructure> {
    return this.service.saveStructure(attachments, currentStructure, courseId);
  }

  updateContentStructure(attachments: FileAttachment[], currentStructure: StructureModule[], courseId: string): Observable<CourseStructure> {
    return this.service.updateStructure(attachments, currentStructure, courseId);
  }

  getOnlineCourseTest(courseId: string): Observable<TestModel> {
    return this.service.getOnlineCourseTest(courseId);
  }

  saveCourseTest(courseId: string, testModel: TestModel): Observable<TestModel> {
    return this.service.saveCourseTest(courseId, testModel);
  }

  updateCourseTest(courseId: string, testModel: TestModel): Observable<TestModel> {
    return this.service.updateCourseTest(courseId, testModel);
  }

  getAllGroups(getFlatData: boolean): Observable<GroupNode[]> {
    return this.groupManagementService.getAllUsersGroups(this.currentGroupId, getFlatData);
  }

  getAllUsers(): Observable<LearnerInfo[]> {
    return this.userManagementService.getAllUsersWithRole(false);
  }

  publishCourse(courseId: string, publishingModel: PublishingModel): Observable<any> {
    return this.service.publishCourse(courseId, publishingModel);
  }

  saveEnrollment(courseId: string, learners: string[], groups?: string[]): Observable<any> {
    return this.service.saveEnrollment(courseId, learners, groups);
  }

  getOnlineCourseStatistics(courseId: string, startDate: string, endDate: string, groupId: string): Observable<OnlineCourseStatisticBundleModel> {
    return this.service.getOnlineCourseStatistics(courseId, startDate, endDate, groupId);
  }

  downloadSurveyInOneRowByExcel(courseId: string, startDate: string, endDate: string, groupId: string, userId: string): Observable<any> {
    return this.service.downloadSurveyOneRowExcelFile(courseId, startDate, endDate, groupId, userId);
  }

  downloadSurvey(onlineCourseId: string, startDate: string, endDate: string, groupId?: string): Observable<any> {
    return this.service.downloadSurvey(onlineCourseId, startDate, endDate, groupId);
  }

  getCourseEnrollmentGroup(courseId: string): Observable<any> {
    return this.service.getCourseEnrollmentGroup(courseId);
  }

  downloadStatistics(downloadData: OnlineCourseStatistic[]): Observable<any> {
    return this.service.downloadStatistics(downloadData);
  }

  downloadAttachment(attachmentFolderId, attachmentId, fileName): Observable<any> {
    return this.service.downloadAttachment(attachmentFolderId, attachmentId, fileName);
  }
}
