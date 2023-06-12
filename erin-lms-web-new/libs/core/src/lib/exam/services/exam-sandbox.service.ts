import {Inject, Injectable, Type} from '@angular/core';
import {NavigationExtras, Router} from "@angular/router";
import {Location} from "@angular/common";
import {ExamBank, ExamLaunchModel, ExamModel, ExamReportRestModel, LearnerExamListModel, LearnerQuestionModel, StartExamModel} from "../model/exam.model";
import {Observable} from "rxjs";
import {ExamService} from "./exam.service";
import {CertificateService} from "../../certificate/service/certificate.service";
import {SnackbarService} from "../../../../../shared/src/lib/snackbar/snackbar.service";
import {DialogService} from "../../../../../shared/src/lib/dialog/dialog.service";
import {DialogConfig} from "../../../../../shared/src/lib/dialog/dialog-config";
import {DialogRef} from "../../../../../shared/src/lib/dialog/dialog-ref";
import {GroupManagementService} from "../../group-management/services/group-management.service";
import {select, Store} from "@ngrx/store";
import {ApplicationState} from "../../common/statemanagement/state/ApplicationState";
import {CategoryItem, GroupNode, LearnerInfo, Question} from "../../../../../shared/src/lib/shared-model";
import {UserManagementService} from "../../user-management/service/user-management.service";
import {QuestionService} from "./question.service";
import {QuestionBank} from "../model/question.model";
import {PermissionService} from "../../common/services/permission.service";
import {TimedCourseService} from "../../timed-course/services/timed-course.service";

@Injectable({
  providedIn: 'root'
})
export class ExamSandboxService {
  private examCategory: CategoryItem;
  private userGroup$ = this.store.pipe(select(state => {
    return state.auth.userGroups;
  }));
  private userRole$ = this.store.select(state => state.auth.role);
  private currentGroupId: string;
  public currentRole: string;
  private questionCategory: CategoryItem;
  private timedCourseService: TimedCourseService;

  constructor(
    private service: ExamService,
    private questionService: QuestionService,
    private certificateService: CertificateService,
    private router: Router,
    private location: Location,
    private groupManagementService: GroupManagementService,
    private userManagementService: UserManagementService,
    private snackbarService: SnackbarService,
    private dialogService: DialogService,
    private permissionService: PermissionService,
    @Inject('constants') public constants,
    private store: Store<ApplicationState>) {
    this.userRole$.subscribe(res => this.currentRole = res);
    this.userGroup$.subscribe(res => this.currentGroupId = res);
  }

  navigateByUrl(url: string): void {
    this.router.navigateByUrl(url);
  }

  navigate(url: string, navigationExtras: NavigationExtras): void {
    this.router.navigate([url], navigationExtras);
  }

  goBack(): void {
    this.location.back();
  }

  createExam(exam: ExamModel): Observable<any> {
    return this.service.createExam(exam);
  }

  updateExam(exam: ExamModel): Observable<any> {
    return this.service.updateExam(exam);
  }

  getCertificates(): Observable<any> {
    return this.certificateService.getCertificates();
  }

  getExamCategories(): Observable<CategoryItem[]> {
    return this.service.getCategories();
  }


  getExamsForBank(groupId?: string, categoryId?: string): Observable<ExamBank[]> {
    return this.service.getExamsForBank(categoryId, groupId);
  }

  getLearnersAllExam(): Observable<LearnerExamListModel[]> {
    return this.service.getLearnersAllExam();
  }

  snackbarOpen(text: string, success?: boolean) {
    this.snackbarService.open(text, success);
  }

  openDialog(component: Type<any>, config: DialogConfig): DialogRef {
    return this.dialogService.open(component, config);
  }

  deleteExam(id: string): Observable<boolean> {
    return this.service.deleteExam(id);
  }

  deleteTimedCourseReaderships(userId: string): Observable<any> {
    return this.timedCourseService.deleteReaderships(userId);
  }

  getExamStatistics(examId: string): Observable<ExamReportRestModel[]> {
    return this.service.getExamStatistics(examId);
  }

  getExamCategory(): CategoryItem {
    return this.examCategory;
  }

  setExamCategory(categoryItem: CategoryItem): void {
    this.examCategory = categoryItem;
  }

  getAllExamsForBank(): Observable<ExamBank[]> {
    return this.service.getExamsForBank();
  }

  getAllGroups(getFlatData: boolean): Observable<GroupNode[]> {
    return this.groupManagementService.getAllUsersGroups(this.currentGroupId, getFlatData);
  }

  getAllUsers(): Observable<LearnerInfo[]> {
    return this.userManagementService.getAllUsersWithRole(false);
  }

  getActiveQuestions(categoryId: string, groupId: string): Observable<QuestionBank[]> {
    return this.questionService.getActiveQuestions(categoryId, groupId);
  }

  getQuestionCategories(): Observable<CategoryItem[]> {
    return this.questionService.getCategories();
  }

  setQuestionCategory(categoryItem: CategoryItem): void {
    this.questionCategory = categoryItem;
  }

  getQuestionCategory(): CategoryItem {
    return this.questionCategory;
  }

  getQuestionGroups(): Observable<GroupNode[]> {
    return this.questionService.getQuestionGroups();
  }

  uploadFile(file: File): Observable<any> {
    return this.questionService.uploadFile(file);
  }

  startExam(examId: string): Observable<StartExamModel> {
    return this.service.startExam(examId);
  }

  updateLearnerExam(examId: string, questions: LearnerQuestionModel[], spentTime: number): Observable<any> {
    return this.service.updateLearnerExam(examId, questions, spentTime);
  }

  finishExam(examId: string): Observable<any> {
    return this.service.finishExam(examId);
  }

  createQuestion(question: Question): Observable<any> {
    return this.questionService.createQuestion(question);
  }

  getExamLaunchData(examId: string): Observable<ExamLaunchModel> {
    return this.service.getExamLaunchData(examId);
  }

  deleteQuestion(id: string): Observable<any> {
    return this.questionService.deleteQuestion(id);
  }

  deleteExamGroup(id): Observable<any> {
    return this.service.deleteExamGroup(id);
  }

  reloadPage(): void {
    location.reload();
  };

  getExamDetailedById(id: string): Observable<ExamModel> {
    return this.service.getDetailedExamById(id);
  }

  publishExam(id: string): Observable<any> {
    return this.service.publishExam(id);
  }

  getQuestion(questionId: string): Observable<Question> {
    return this.questionService.getQuestionById(questionId);
  }

  updateQuestion(question: Question): Observable<any> {
    return this.questionService.updateQuestion(question);
  }

  getTotalAvailable(categoryId: string, groupId: string, score: number): Observable<number> {
    return this.questionService.getTotalAvailable(categoryId, groupId, score);
  }

  filterPermission(examPages: any[]): any[] {
    return this.permissionService.filterWithPermission(examPages);
  }

  checkActiveExam(): Observable<any> {
    return this.service.checkActiveExam();
  }

  getQuestionsByIds(questionIds: Set<string>): Observable<QuestionBank[]> {
    return this.questionService.getQuestionsByIds(questionIds);
  }

  getLoadedQuestionCategoryById(id: string): CategoryItem {
    return this.questionService.categories.get(id);
  }

  getLoadedQuestionGroupById(id: string): GroupNode {
    return this.questionService.groups.get(id);
  }

  downloadLearnerExamAnswer(id: string, learnerName: string): Observable<any> {
    return this.service.downloadLearnerExamAnswer(id, learnerName);
  }

  updateExamGroupName(id: string, node: GroupNode, examGroupName: string): Observable<any> {
    return this.service.updateExamGroupName(node, examGroupName);
  }

  addExamGroup(parent: string, examGroupName: string): Observable<any> {
    return this.service.addExamGroup(parent, examGroupName);

  }

  getExamGroup(): Observable<GroupNode[]> {
    return this.service.getAllExamGroups();
  }

  updateQuestionGroup(id: string, questionGroupName: string): Observable<any> {
    return this.service.updateQuestionGroup(id, questionGroupName)
  }

  addQuestionGroup(id: string, questionGroupName: string): Observable<any> {
    return this.service.addQuestionGroups(id, questionGroupName)
  }

  deleteQuestionGroup(id: string): Observable<any> {
    return this.service.deleteQuestionGroup(id)
  }
}
