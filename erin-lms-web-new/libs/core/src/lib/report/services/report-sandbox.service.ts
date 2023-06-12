import {Inject, Injectable, Type} from '@angular/core';
import {Params, Router} from "@angular/router";
import {Observable, ReplaySubject, Subject} from "rxjs";
import {GroupManagementService} from "../../group-management/services/group-management.service";
import {select, Store} from "@ngrx/store";
import {ApplicationState} from "../../common/statemanagement/state/ApplicationState";
import {ReportService} from "./report.service";
import {
  ClassroomCourseReportModel,
  CourseActivityReport,
  CourseActivityReportModel,
  ExamReportMappedModel,
  OnlineCourseAnalytics,
  OnlineCourseReport,
  SurveyReport,
  TimedCourseReport,
  TimedCourseReportModel
} from "../model/report.model";
import {TimedCourseService} from "../../timed-course/services/timed-course.service";
import {ExamService} from "../../exam/services/exam.service";
import {ClassroomCourseService} from "../../classroom-course/services/classroom-course.service";
import {OnlineCourseService} from "../../online-course/services/online-course.service";
import {SnackbarService} from "../../../../../shared/src/lib/snackbar/snackbar.service";
import {DialogService} from "../../../../../shared/src/lib/dialog/dialog.service";
import {DialogConfig} from 'libs/shared/src/lib/dialog/dialog-config';
import {DialogRef} from "../../../../../shared/src/lib/dialog/dialog-ref";
import {DetailedUserInfo} from "../../common/common.model";
import {CategoryItem, CourseShortModel, GroupNode} from "../../../../../shared/src/lib/shared-model";
import {SurveyService} from "../../survey/services/survey.service";
import {Survey} from "../../survey/model/survey.model";
import {UserManagementService} from "../../user-management/service/user-management.service";

@Injectable({
  providedIn: 'root'
})
export class ReportSandboxService {
  role: string;
  userGroup$ = this.store.pipe(select(state => {
    return state.auth.userGroups;
  }));
  authRole$ = this.store.pipe(select(state => {
    if (state.auth) {
      return state.auth.role;
    }
    return undefined;
  }));
  currentGroupId: string;
  selectedGroupId$ = new ReplaySubject<string>(0);
  startDate = new ReplaySubject<string>(0);
  endDate = new ReplaySubject<string>(0);

  constructor(
    private router: Router,
    private service: ReportService,
    private groupManagementService: GroupManagementService,
    private userManagementService: UserManagementService,
    private onlineCourseService: OnlineCourseService,
    private timedCourseService: TimedCourseService,
    private classroomCourseService: ClassroomCourseService,
    private examService: ExamService,
    private dialogService: DialogService,
    private snackbarService: SnackbarService,
    private surveyService: SurveyService,
    private store: Store<ApplicationState>,
    @Inject('constants') public constants) {
    this.userGroup$.subscribe(res => this.currentGroupId = res);
    this.authRole$.subscribe(res => this.role = res);
  }

  navigateByUrl(path: string): void {
    this.router.navigateByUrl(path);
  }

  updateQueryParams(params: Params) {
    this.router.navigate([], {queryParams: params, queryParamsHandling: 'merge',});
  }

  navigate(path: string, params?: Params): void {
    this.router.navigate([path], {queryParams: params});
  }

  getAllGroups(getFlatData: boolean): Observable<any> {
    return this.groupManagementService.getAllUsersGroups(this.currentGroupId, getFlatData);
  }

  getTimeCourseReport(startDate: string, endDate: string, categoryName: string, type?: string, groupId?: string): Observable<TimedCourseReportModel> {
    return this.service.getTimedCourseReport(startDate, endDate, categoryName, type, groupId);
  }

  getExamReport(startDate: string, endDate: string, filterItems: ExamReportMappedModel): Observable<any> {
    return this.service.getExamReport(startDate, endDate, filterItems)
  }


  getOnlineCourseReport(startDate: string, endDate: string, categoryName: string, type: string, groupId: string): Observable<OnlineCourseAnalytics> {
    return this.service.getOnlineCourseReport(startDate, endDate, categoryName, type, groupId);
  }

  getClassroomCourseReport(startDate: string, endDate: string, categoryName: string, groupId: string): Observable<ClassroomCourseReportModel> {
    return this.service.getClassroomCourseReport(startDate, endDate, categoryName, groupId);
  }

  downloadClassroomCourseExcelReport(startDate: string, endDate: string, categoryId: string, departmentId: string, duration: string[]): Observable<any> {
    return this.service.downloadClassroomCourseExcelReport(startDate, endDate, categoryId, departmentId, duration);
  }

  downloadOnlineCourseExcelReport(reportData: OnlineCourseReport[]): Observable<any> {
    return this.service.downloadOnlineCourseExcelReport(reportData);
  }

  downloadAllOnlineCourseExcelReport(startDate: string, endDate: string, categoryId: string, courseType: string, departmentId: string): Observable<any> {
    return this.service.downloadAllOnlineCourseExcelReport(startDate, endDate, categoryId, courseType, departmentId);
  }

  downloadTimedCourseExcelReport(reportData: TimedCourseReport[]): Observable<any> {
    return this.service.downloadTimedCourseExcelReport(reportData);
  }

  downloadPromoExcelReport(startDate: string, endDate: string): Observable<any> {
    return this.service.downloadPromoExcelReport(startDate, endDate);
  }

  getTimedCourseCategories(): Observable<CategoryItem[]> {
    return this.timedCourseService.getTimedCourseCategories();
  }

  getOnlineCourseCategories(): Observable<CategoryItem[]> {
    return this.onlineCourseService.getOnlineCourseCategories();
  }

  getClassroomCourseCategories(): Observable<CategoryItem[]> {
    return this.classroomCourseService.getClassroomCourseCategories();
  }

  snackbarOpen(text: string, success?: boolean) {
    this.snackbarService.open(text, success);
  }

  openDialog(component: Type<any>, config: DialogConfig): DialogRef {
    return this.dialogService.open(component, config);
  }

  setStartDate(date): void {
    this.startDate.next(date);
  }

  setEndDate(date): void {
    this.endDate.next(date);
  }

  setSelectedGroupId(id: string): void {
    this.selectedGroupId$.next(id);
  }

  getSelectedGroupId(): Observable<string> {
    return this.selectedGroupId$.asObservable();
  }

  getStartDate(): Subject<string> {
    return this.startDate;
  }

  getEndDate(): Subject<string> {
    return this.endDate;
  }

  getAllUsers(includeMe?: boolean): Observable<DetailedUserInfo[]> {
    return this.userManagementService.getAllUsers(includeMe);
  }

  getClassroomCourseActivityReport(startDate: string, endDate: string, departmentId: string, username: string): Observable<CourseActivityReportModel> {
    return this.service.getClassroomCourseActivityReport(startDate, endDate, departmentId, username);
  }

  getOnlineCourseActivityReport(startDate: string, endDate: string, departmentId: string, username: string): Observable<CourseActivityReportModel> {
    return this.service.getOnlineCourseActivityReport(startDate, endDate, departmentId, username);
  }

  getTimedCourseActivityReport(startDate: string, endDate: string, departmentId: string, username: string): Observable<CourseActivityReportModel> {
    return this.service.getTimedCourseActivityReport(startDate, endDate, departmentId, username);
  }

  getSurveys(active?: boolean, withQuestions?: boolean): Observable<Survey[]> {
    return this.surveyService.getSurveys(active, withQuestions);
  }

  getCoursesWithSurvey(surveyId: string): Observable<CourseShortModel[]> {
    return this.service.getCoursesWithSurvey(surveyId);
  }

  getSurveyReport(surveyId: string, startDate: string, endDate: string, courseId?: string): Observable<SurveyReport[]> {
    return this.surveyService.getSurveyReport(surveyId, startDate, endDate, courseId);
  }

  downloadOnlineCourseActivityReport(report: CourseActivityReport[]): Observable<any> {
    return this.service.downloadOnlineCourseActivityReport(report);
  }

  downloadTimedCourseActivityReport(startDate: string, endDate: string, departmentId: string, username: string): Observable<any> {
    return this.service.downloadTimedCourseActivityReport(startDate, endDate, departmentId, username);
  }

  downloadClassroomCourseActivityReport(startDate: string, endDate: string, departmentId: string, username: string): Observable<any> {
    return this.service.downloadClassroomCourseActivityReport(startDate, endDate, departmentId, username);
  }

  getTotalScore(learnerId: string, courseType: string): Observable<number> {
    return this.service.getTotalScore(learnerId, courseType);
  }

  timedCourseTotalScore(learnerId: string): Observable<number> {
    return this.service.timedCourseTotalScore(learnerId);
  }

  getExamCategories(): Observable<CategoryItem[]>{
    return this.examService.getCategories();
  }

  getExamGroups(): Observable<GroupNode[]>{
    return this.examService.getAllExamGroups();
  }
}
