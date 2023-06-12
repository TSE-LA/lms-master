import {Injectable} from '@angular/core';
import {
  AnswerResultType,
  ExamBank,
  ExamConfigModel,
  ExamLaunchModel,
  ExamModel,
  ExamReportRestModel,
  LearnerAnswerModel,
  LearnerExamListModel,
  LearnerQuestionModel,
  StartExamModel
} from "../model/exam.model";
import {HttpClient, HttpParams} from "@angular/common/http";
import {catchError, map} from "rxjs/operators";
import {Observable, throwError} from "rxjs";
import {DateFormatter} from "../../../../../shared/src/lib/utilities/date-formatter.util";
import {CategoryItem, GroupNode, RandomQuestion} from "../../../../../shared/src/lib/shared-model";
import {ExamStateValueUtil} from "../../../../../shared/src/lib/utilities/exam-state-value.util";
import {FileDownloadUtil} from "../../common/utilities/file-download-util";
import {GroupUtil} from "../../group-management/model/group-util";

@Injectable({
  providedIn: 'root'
})
export class ExamService {

  private group: GroupNode[] = [{name: 'Root group ', id: "", nthSibling: 1, parent: '', children: []}];
  private groups = new Map<string, GroupNode>();
  private categories = new Map<string, CategoryItem>();

  constructor(private httpClient: HttpClient) {
  }

  createExam(exam: ExamModel): Observable<any> {
    const body = this.mapToCreateExamRest(exam);
    return this.httpClient.post("/lms/exams", body).pipe(map((res: any) => {
      return res.entity.examId;
    }));
  }

  updateExam(exam: ExamModel): Observable<any> {
    const body = this.mapToCreateExamRest(exam);
    return this.httpClient.put("/lms/exams/" + exam.id, body).pipe(map((res: any) => {
      return res;
    }))
  }

  getCategories(): Observable<CategoryItem[]> {
    let params = new HttpParams();
    params = params.append('parentCategoryId', 'exam');
    return this.httpClient.get('/lms/exam-categories', {params}).pipe(map(res => {
      return this.mapToCategoryModel(res);
    }), catchError(err => throwError(err)));
  }


  deleteExam(id: string): Observable<boolean> {
    return this.httpClient.delete('/lms/exams/' + id).pipe(map((res: any) => {
      return res.entity;
    }));
  }


  getAllExamGroups(): Observable<GroupNode[]> {
    return this.httpClient.get('/lms/exam-group').pipe(map((res: any) => {
      const nodes: GroupNode[] = [];
      if (res.entity.length === undefined) {
        GroupUtil.pushNode(nodes, res.entity);
      } else {
        for (const node of res.entity) {
          GroupUtil.pushNode(nodes, node);
        }
      }
      return nodes;
    }));
  }

  getExamsForBank(categoryId?: string, groupId?: string): Observable<ExamBank[]> {
    let params = new HttpParams();
    if (categoryId) {
      params = params.append('categoryId', categoryId);
    }

    if (groupId) {
      params = params.append('groupId', groupId);
    }

    return this.httpClient.get('/lms/exams', {params}).pipe(map(res => {
      return this.mapToExamBankModels(res);
    }));
  }

  getDetailedExamById(id: string): Observable<ExamModel> {
    return this.httpClient.get('/lms/exams/detailed/' + id).pipe(map(res => {
      return this.mapToExamModel(res);
    }));
  }

  getLearnersAllExam(): Observable<LearnerExamListModel[]> {
    return this.httpClient.get('/lms/exams/learner').pipe(map(res => {
      return this.mapToLearnerExamList(res);
    }));
  }

  getExamLaunchData(examId: string): Observable<ExamLaunchModel> {
    return this.httpClient.get('/lms/exam-runtime/launch/' + examId).pipe(map((res: any) => {
      return res.entity;
    }));
  }

  startExam(examId: string): Observable<StartExamModel> {
    return this.httpClient.get('/lms/exam-runtime/start/' + examId).pipe(map((res: any) => {
      return this.mapToStartExam(res.entity);
    }));
  }

  updateLearnerExam(examId: string, questions: LearnerQuestionModel[], spentTime: number): Observable<any> {
    const body = {examId: examId, learnerQuestion: this.mapToLearnerQuestionDto(questions), spentTime: spentTime};
    return this.httpClient.patch('/lms/exam-runtime/update', body).pipe(map(() => {
      return;
    }));
  }

  getExamStatistics(examId: string): Observable<ExamReportRestModel[]> {
    return this.httpClient.get('/lms/exam-runtime/score/' + examId).pipe(map((item: any) => item.entity));
  }

  checkActiveExam(): Observable<any> {
    return this.httpClient.get('/lms/exam-runtime/ongoing').pipe(map((res: any) => {
      return res.entity;
    }));
  }

  finishExam(examId: string): Observable<any> {
    return this.httpClient.get('/lms/exam-runtime/end/' + examId).pipe(map(() => {
      return;
    }));
  }

  publishExam(id: string): Observable<any> {
    return this.httpClient.put('/lms/exams/publish/' + id, {});
  }

  downloadLearnerExamAnswer(id: string, learnerName: string): Observable<any> {
    let params = new HttpParams();
    params = params.append('examId', id);
    params = params.append('learnerId', learnerName);
    return this.httpClient.get('/lms/exam-runtime/download/', {
      responseType: 'blob',
      observe: 'response',
      params
    }).pipe(map(response => {
      FileDownloadUtil.downloadFile(response);
      return response;
    }))
  }

  private mapToLearnerQuestionDto(questions: LearnerQuestionModel[]): any {
    const result = [];
    for (const question of questions) {
      result.push({
        id: question.id,
        value: question.value,
        imagePath: question.imagePath,
        type: question.type,
        selectedAnswers: null,
        answers: question.answers
      })
    }
    return result;
  }

  private mapToStartExam(res): StartExamModel {
    return {
      title: res.title,
      remainingTime: res.remainingTime,
      durationInSeconds: res.duration,
      questions: this.mapToLearnerQuestion(res.learnerQuestion)
    };
  }

  private mapToLearnerQuestion(questions): LearnerQuestionModel[] {
    const result: LearnerQuestionModel[] = [];
    let index = 1;
    for (const question of questions) {
      result.push({
        id: question.id,
        index: index,
        answers: this.mapToLearnerAnswerModel(question.answers),
        imagePath: question.imagePath,
        type: question.type,
        value: question.value,
        selected: this.checkSelectedQuestion(question.answers)
      })
      index++;
    }
    return result
  }

  private checkSelectedQuestion(answers): boolean {
    for (const answer of answers) {
      if (answer.selected) {
        return true;
      }
    }
  }

  deleteExamGroup(id: string): Observable<any> {
    return this.httpClient.delete('/lms/exam-group/' + id).pipe(map((res: any) => {
      return res.entity;
    }));
  }

  addExamGroup(parent: string, examGroupName: string): Observable<any> {
    const body = {
      name: examGroupName
    };
    if (parent) {
      body["parentId"] = parent;
    }
    return this.httpClient.post('/lms/exam-group/', body).pipe(map((res: any) => {
      return res.entity;
    }));
  }

  updateExamGroupName(node: GroupNode, newName: string): Observable<any> {
    const body = {
      id: node.id,
      name: newName,
      parentId: node.parent
    };
    return this.httpClient.put('/lms/exam-group/', body).pipe(map((res: any) => {
      return res.entity;
    }));
  }

  private mapToLearnerAnswerModel(answers: any): LearnerAnswerModel[] {
    let index = 0;
    const result: LearnerAnswerModel[] = [];
    for (const answer of answers) {
      result.push({
        index: index,
        value: answer.value,
        selected: answer.selected,
      })
      index++;
    }
    return result;
  }

  private mapToLearnerExamList(res: any): LearnerExamListModel[] {
    const result: LearnerExamListModel[] = [];
    for (const exam of res.entity) {
      result.push({
        id: exam.id,
        name: exam.name,
        author: exam.author,
        categoryId: exam.categoryId,
        hasCertificate: exam.hasCertificate ? 'Сертификаттай' : 'Сертификатгүй',
        startDate: DateFormatter.toISODateString(exam.startDate),
        status: ExamStateValueUtil.getExamStateDisplayName(exam.status),
        thresholdScore: exam.thresholdScore,
        upcoming: exam.upcoming,
        startTime: exam.startTime,
        endTime: exam.endTime,
        endDate: DateFormatter.toISODateString(exam.endDate)
      })
    }
    return result;
  }

  private mapToExamBankModels(res: any): ExamBank[] {
    const sorted = res.entity.sort((firstExam, secondExam) => {
      const firstDate = new Date(firstExam.createdDate);
      const secondDate = new Date(secondExam.createdDate);
      return (secondDate as any) - (firstDate as any);
    });
    const exams: ExamBank[] = [];
    for (const exam of sorted) {
      exams.push({
        id: exam.id,
        name: exam.name,
        duration: exam.examConfigure.duration,
        start: DateFormatter.toISODateString(exam.examConfigure.startDate) + ' ' + exam.examConfigure.startTime,
        end: DateFormatter.toISODateString(exam.examConfigure.endDate) + ' ' + exam.examConfigure.endTime,
        startDate: DateFormatter.toISODateString(exam.examConfigure.startDate),
        endDate: DateFormatter.toISODateString(exam.examConfigure.endDate),
        startTime: exam.examConfigure.startTime,
        endTime: exam.examConfigure.endTime ? exam.examConfigure.endTime : '',
        questionCount: exam.examConfigure.totalQuestions,
        passingScore: exam.examConfigure.thresholdScore,
        maxScore: exam.examConfigure.maxScore,
        enrolled: exam.enrolledLearners,
        modified: exam.modifiedUser + ' ' + DateFormatter.toISODateString(exam.modifiedDate),
        examStatus: this.getExamStatus(exam.examStatus),
        notPublished: exam.publishState != 'PUBLISHED'
      })
    }
    return exams;
  }

  private getExamStatus(status: string): string {
    switch (status) {
      case "NEW":
        return "ШИНЭ";
      case  "STARTED":
        return "ЭХЭЛСЭН"
      case "FINISHED":
        return "ДУУССАН";
      default:
        return "ДУУССАН"
    }
  }

  private mapToCategoryModel(res: any): CategoryItem[] {
    const categories: CategoryItem[] = [];
    for (const category of res.entity) {
      const categoryItem = {id: category.categoryId, name: category.name, index: category.index};
      categories.push(categoryItem);
      this.categories.set(categoryItem.id, categoryItem);
    }
    return categories;
  }

  private mapToCreateExamRest(exam: ExamModel): any {
    const examConfig: ExamConfigModel = exam.config;
    const randomQuestions = [];
    for (const randQuestion of examConfig.randomQuestions) {
      randomQuestions.push({
        groupId: randQuestion.group.id,
        categoryId: randQuestion.category.id,
        score: randQuestion.score,
        amount: randQuestion.totalAmount
      })
    }
    return {
      id: exam.id,
      name: exam.name,
      description: exam.description ? exam.description : null,
      categoryId: exam.categoryId,
      groupId: exam.groupId,
      examType: exam.type,
      enrolledGroups: exam.enrolledGroups,
      enrolledLearners: exam.enrolledLearners,
      publishConfig: {
        publishDate: new Date(exam.publishDate),
        publishTime: exam.publishTime,
        sendEmail: exam.sendEmail,
        sendSMS: exam.sendSms,
        mailText: exam.mailText,
        smsText: exam.smsText,
      },
      examConfigure: {
        questionIds: Array.from(exam.config.questionIds),
        randomQuestions: randomQuestions,
        answerResult: examConfig.showAnswerResult,
        shuffleQuestion: examConfig.shuffleQuestion,
        shuffleAnswer: examConfig.shuffleAnswer,
        autoStart: examConfig.autoStart,
        questionsPerPage: examConfig.questionsPerPage,
        thresholdScore: Number(examConfig.threshold),
        attempt: Number(examConfig.attempt),
        certificateId: examConfig.certificateId,
        startDate: new Date(examConfig.startDate),
        endDate: new Date(examConfig.endDate),
        startTime: examConfig.startTime,
        endTime: examConfig.endTime,
        duration: Number(examConfig.duration),
        maxScore: Number(examConfig.maxScore)
      }
    }
  };

  private mapToExamModel(res: any): ExamModel {
    const exam = res.entity;
    const randomQuestions: RandomQuestion[] = [];
    let index = 0;
    for (const randQuestion of exam.examConfigure.randomQuestionConfigs) {
      randomQuestions.push({
        group: {parent: "", id: randQuestion.groupId, name: "", nthSibling: 0, children: []},
        category: {id: randQuestion.categoryId, name: ""},
        score: randQuestion.score,
        totalAmount: Number(randQuestion.amount),
        index: index,
        available: 0
      });
      index++;
    }
    return {
      id: exam.id,
      name: exam.name,
      description: exam.description,
      categoryId: exam.categoryId,
      groupId: exam.groupId,
      type: exam.examType,
      enrolledGroups: exam.enrolledGroups,
      enrolledLearners: exam.enrolledLearners,
      publishDate: DateFormatter.toISODateString(exam.publishConfig.publishDate),
      publishTime: exam.publishConfig.publishTime,
      sendEmail: exam.publishConfig.sendEmail,
      sendSms: exam.publishConfig.sendSMS,
      mailText: exam.publishConfig.mailText,
      smsText: exam.publishConfig.smsText,
      isEditable: !(exam.examStatus == "STARTED" || exam.examStatus == "FINISHED"),
      notPublished: exam.publishState != "PUBLISHED",
      config: {
        questionIds: new Set(exam.examConfigure.questionIds ? exam.examConfigure.questionIds : []),
        randomQuestions: randomQuestions,
        autoStart: exam.examConfigure.autoStart,
        showAnswerResult: AnswerResultType[exam.examConfigure.answerResult],
        shuffleQuestion: exam.examConfigure.shuffleQuestion,
        shuffleAnswer: exam.examConfigure.shuffleAnswer,
        questionsPerPage: exam.examConfigure.questionsPerPage,
        threshold: Number(exam.examConfigure.thresholdScore),
        attempt: Number(exam.examConfigure.attempt),
        certificateId: exam.examConfigure.certificateId,
        startDate: DateFormatter.toISODateString(exam.examConfigure.startDate),
        endDate: DateFormatter.toISODateString(exam.examConfigure.endDate),
        startTime: exam.examConfigure.startTime,
        endTime: exam.examConfigure.endTime,
        duration: Number(exam.examConfigure.duration),
        maxScore: Number(exam.examConfigure.maxScore)
      }
    }
  }

  updateQuestionGroup(id: string, questionGroupName: string): Observable<any> {
    const body = {
      id: id,
      name: questionGroupName
    }
    if (parent) {
      body['parentId'] = 'question'
    }
    return this.httpClient.put('/lms/question-groups/update/' + id, body).pipe(map((res: any) => {
      return res.entity;
    }));
  }

  addQuestionGroups(id: string, questionGroupName: string) {
    const body = {
      name: questionGroupName,
      id: id,
    };
    if (parent) {
      body["parentId"] = 'question';
    }
    return this.httpClient.post('/lms/question-groups/create', body).pipe(map((res: any) => {
      return res.entity;
    }));
  }

  deleteQuestionGroup(id: string): Observable<any> {
    return this.httpClient.delete('lms/question-groups/delete/' + id).pipe(map((res: any) => {
      return res.entity
    }));
  }

}
