import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {catchError, map} from "rxjs/operators";
import {Observable, throwError} from "rxjs";
import {DateFormatter} from "../../../../../shared/src/lib/utilities/date-formatter.util";
import {Survey, SurveyStatus} from "../model/survey.model";
import {AnswerChoice, QuestionTypes, TestQuestion} from "../../../../../shared/src/lib/shared-model";
import {SurveyReport} from "../../report/model/report.model";

@Injectable({
  providedIn: 'root'
})
export class SurveyService {

  constructor(private httpClient: HttpClient) {
  }


  createSurvey(name: string, description?: string): Observable<any> {
    return this.httpClient.post('/lms/assessments', {name, description});
  }

  deleteSurvey(assessmentId: string): Observable<any> {
    return this.httpClient.delete('/lms/assessments/' + assessmentId).pipe(map(() => {
    }), catchError(err => {
      return throwError(err);
    }));
  }

  getSurveys(active?: boolean, withQuestions?: boolean): Observable<Survey[]> {
    let params = new HttpParams();
    params = params.append('onlyActive', active ? 'true' : 'false');
    return this.httpClient.get('/lms/assessments', {params}).pipe(map((res: any) => {
      const sorted = res.entity.sort((firstModifiedDate, secondModifiedDate) => {
        const firstDate = new Date(firstModifiedDate.modifiedDate);
        const secondDate = new Date(secondModifiedDate.modifiedDate);
        return (secondDate as any) - (firstDate as any);
      });
      return this.mapToSurveys(sorted, withQuestions);
    }));
  }


  getSurveyById(assessmentId: string): Observable<Survey> {
    return this.httpClient.get('/lms/assessments/' + assessmentId).pipe(map((res: any) => {
      return this.mapToSurvey(res.entity);
    }));
  }

  cloneSurvey(id: string, survey: Survey): Observable<any> {
    return this.httpClient.post('/lms/assessments/' + id + '/clone',
      {name: '[ХУУЛСАН] - ' + survey.name, description: survey.summary}).pipe(map((res) => {
      return res;
    }), catchError(err => {
      return throwError(err);
    }));
  }

  getSurveyReport(surveyId: string, startDate: string, endDate: string, courseId?: string): Observable<SurveyReport[]> {
    let params = new HttpParams();
    params = params.append('startDate', startDate);
    params = params.append('endDate', endDate);
    if (courseId != null && courseId != 'all') {
      params = params.append('courseId', courseId);
    }
    return this.httpClient.get('/lms/analytics/survey/' + surveyId, {params})
      .pipe(map((res: any) => res.entity.analytics));
  }

  getSurveyQuestions(questionsId: string): Observable<TestQuestion[]> {
    return this.httpClient.get('/lms/quizzes/' + questionsId).pipe(map((res: any) => {
      return this.mapToQuestions(res);
    }));
  }

  saveQuestions(questions: TestQuestion[], surveyId: string, contentId: string): Observable<any> {
    const body = this.mapToQuestionsBody(questions, surveyId);
    return this.httpClient.put('/lms/quizzes/' + contentId, {questions: body});
  }

  updateSurvey(surveyId: string, name: string, description: string): Observable<any> {
    return this.httpClient.put('/lms/assessments/' + surveyId, {name, description});
  }

  private mapToSurveys(items: any, withQuestions: boolean): Survey[] {
    const assessments: Survey[] = [];

    if (withQuestions) {
      items = items.filter(item => item.questionCount > 0);
    }

    for (const assessment of items) {
      assessments.push(this.mapToSurvey(assessment));
    }

    return assessments;
  }

  private mapToSurvey(item: any): Survey {
    return {
      id: item.id,
      name: item.name,
      admin: item.authorId,
      contentId: item.quizId,
      createdDate: DateFormatter.toISODateString(item.createdDate),
      modifiedDate: DateFormatter.toISODateString(item.modifiedDate),
      summary: item.description,
      questionCount: item.questionCount,
      status: item.status === 'ACTIVE' ? SurveyStatus.ACTIVE : SurveyStatus.INACTIVE
    };
  }

  private mapToQuestions(res: any): TestQuestion[] {
    const entity = res.entity;
    const questions: TestQuestion[] = [];
    for (const item of entity.questions) {
      const choices: AnswerChoice[] = item.answers;
      const questionType = this.mapToQuestionType(item.type);
      const question = new TestQuestion(item.title, choices, questionType);
      question.required = item.required;
      questions.push(question);
    }
    return questions;
  }

  private mapToQuestionType(type: string): QuestionTypes {
    if (type === 'FILL_IN_BLANK') {
      return QuestionTypes.FILL_IN_BLANK;
    } else if (type === 'MULTI_CHOICE') {
      return QuestionTypes.MULTIPLE_CHOICE;
    } else {
      return QuestionTypes.SINGLE_CHOICE;
    }
  }

  private mapToQuestionsBody(questions: TestQuestion[], surveyId: string): any {
    const mapped: any[] = [];
    for (const question of questions) {
      mapped.push({
        question: question.question,
        questionType: question.type,
        required: !!question.required,
        answers: this.getAnswers(question.answers),
        assessmentId: surveyId
      });
    }
    return mapped;
  }

  private getAnswers(answers: AnswerChoice[]): any[] {
    const result: any[] = [];

    for (const answer of answers) {
      result.push({
        value: answer.value,
        correct: answer.correct
      });
    }

    return result;
  }
}
