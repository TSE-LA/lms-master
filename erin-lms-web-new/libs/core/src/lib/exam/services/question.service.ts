import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {Observable, throwError} from "rxjs";
import {catchError, map} from "rxjs/operators";
import {QuestionBank} from "../model/question.model";
import {AnswerChoice, CategoryItem, GroupNode, Question, QuestionTypeModel, QuestionTypes} from "../../../../../shared/src/lib/shared-model";
import {DateFormatter} from "../../../../../shared/src/lib/utilities/date-formatter.util";
import {QUESTION_TYPES} from "../../../../../shared/src/lib/shared-constants";

@Injectable({
  providedIn: 'root'
})
export class QuestionService {
  groups: Map<string, GroupNode> = new Map();
  categories: Map<string, CategoryItem> = new Map();

  constructor(private httpClient: HttpClient) {
  }

  createQuestion(question: Question): Observable<any> {
    const body = this.mapToQuestionRest(question);
    return this.httpClient.post("/lms/questions", body);
  }

  getTotalAvailable(categoryId: string, groupId: string, score: number): Observable<number> {
    let params = new HttpParams();
    params = params.append('categoryId', categoryId);
    params = params.append('groupId', groupId);
    params = params.append('score', score)
    return this.httpClient.get("/lms/questions/get-question-count", {params}).pipe(map((res: any) => {
      return res.entity;
    }));
  }

  updateQuestion(question: Question): Observable<any> {
    const body = this.mapToQuestionRest(question);
    return this.httpClient.put('/lms/questions/' + question.id, body);
  }

  getQuestionById(questionId: string): Observable<Question> {
    return this.httpClient.get("/lms/questions/" + questionId).pipe(map((res: any) => {
      return this.mapToQuestionModel(res.entity);
    }))
  }

  getQuestionsByIds(questionIds: Set<string>): Observable<QuestionBank[]> {
    return this.httpClient.put("/lms/questions/get-by-ids",Array.from(questionIds)).pipe(map((res: any) => {
      return this.mapToQuestionBank(res.entity);
    }))
  }

  getCategories(): Observable<CategoryItem[]> {
    let params = new HttpParams();
    params = params.append('parentCategoryId', 'question');
    return this.httpClient.get('/lms/question-categories', {params}).pipe(map(res => {
      return this.mapToCategoryModel(res)
    }), catchError(err => throwError(err)));
  }

  getQuestionGroups(): Observable<GroupNode[]> {
    let params = new HttpParams();
    params = params.append('parentGroupId', 'question');
    return this.httpClient.get("/lms/question-groups", {params}).pipe(map(res => {
      return this.mapToQuestionGroups(res);
    }));
  }

  getActiveQuestions(categoryId: string, groupId: string): Observable<QuestionBank[]> {
    let params = new HttpParams();
    if (categoryId != "all") {
      params = params.append('categoryId', categoryId);
    }

    if (groupId != "all") {
      params = params.append('groupId', groupId);
    }

    return this.httpClient.get("/lms/questions", {params}).pipe(map((res: any) => {
      const sorted = res.entity.sort((firstQuestion, secondQuestion) => {
        const firstDate = new Date(firstQuestion.createdDate);
        const secondDate = new Date(secondQuestion.createdDate);
        return (secondDate as any) - (firstDate as any);
      });
      return this.mapToQuestionBank(sorted);
    }))
  }

  uploadFile(file: File): Observable<any> {
    const headers = new HttpHeaders();
    headers.set('Accept', 'multipart/form-data');
    const formData = new FormData();
    formData.append('file', file);
    return this.httpClient.post('/lms/questions/attachment', formData, {headers}).pipe(map((res: any) => {
      return res.entity;
    }));
  }


  deleteQuestion(id: string): Observable<any> {
    return this.httpClient.delete('/lms/questions/' + id).pipe(map((res: any) => {
      return res.entity;
    }));
  }

  private mapToQuestionBank(res: any): QuestionBank[] {
    const questions: QuestionBank[] = [];
    for (const question of res) {
      const category = this.categories.get(question.categoryId);
      const group = this.groups.get(question.groupId);
      questions.push({
        id: question.id,
        value: question.value,
        group: group ? group.name : "",
        category: category ? category.name : "",
        score: question.score,
        type: this.getTypeName(question.type),
        created: question.author + ' ' + DateFormatter.toISODateString(question.createdDate),
        modified: question.modifiedUser + ' ' + DateFormatter.toISODateString(question.modifiedDate),
        hasImage: question.hasImage
      })
    }
    return questions;
  }

  private mapToCategoryModel(res: any): CategoryItem[] {
    this.categories = new Map<string, CategoryItem>()
    const categories: CategoryItem[] = [];
    for (const category of res.entity) {
      categories.push({id: category.id, name: category.name, index: category.index});
      this.categories.set(category.id, category);
    }
    return categories;
  }

  private mapToQuestionRest(question: Question): any {
    return {
      answers: this.mapToAnswer(question.answers),
      categoryId: question.category.id,
      fileId: question.contentId,
      groupId: question.group.id,
      id: question.id,
      hasImage: question.hasContent,
      score: question.totalScore,
      type: question.type.value,
      value: question.value,
      correctAnswerText: question.correctText,
      wrongAnswerText: question.wrongText
    }
  }

  private mapToQuestionGroups(res: any): GroupNode[] {
    this.groups = new Map<string, GroupNode>();
    const nodes: GroupNode[] = [];
    for (const node of res.entity) {
      const newNode: GroupNode = {
        parent: node.parentGroupId.id,
        id: node.id.id,
        name: node.name,
        nthSibling: node.nthSibling,
        children: node.children ? node.children : []
      };
      nodes.push(newNode);
      this.groups.set(newNode.id, newNode);
    }
    return nodes;
  }

  private mapToAnswer(answers: AnswerChoice[]): any {
    const answerBody = [];
    let index = 0;
    for (const answer of answers) {
      answerBody.push({
        column: answer.column,
        correct: answer.correct,
        index: index,
        matchIndex:index,
        value: answer.value,
        weight: answer.weight
      });
      index++;
    }
    return answerBody;
  }

  private getTypeName(type): string {
    switch (type) {
      case QuestionTypes.MULTIPLE_CHOICE:
        return QUESTION_TYPES[0].name;
      case QuestionTypes.SINGLE_CHOICE:
        return QUESTION_TYPES[1].name;
      default:
        return "unknown option";
    }
  }

  private mapToQuestionModel(res: any): Question {
    return {
      id: res.id,
      value: res.value,
      type: this.getQuestionType(res.type),
      answers: this.mapToAnswerModel(res.answers),
      totalScore: res.score,
      category: this.categories.get(res.categoryId),
      group: this.groups.get(res.groupId),
      correctText: res.correctText,
      wrongText: res.wrongText,
      contentId: res.fileId,
      hasContent: res.hasImage,
      contentName: res.fileName
    }
  }

  private mapToAnswerModel(answers): AnswerChoice[] {
    const answerChoices: AnswerChoice[] = [];
    answers.sort(function (a,b){
      return a.index - b.index;
    })
    for (const res of answers) {
      answerChoices.push({value: res.value, correct: res.correct, column: res.column, weight: res.weight})
    }
    return answerChoices;
  }

  private getQuestionType(type): QuestionTypeModel {
    if (type == QuestionTypes.MULTIPLE_CHOICE) {
      return QUESTION_TYPES[0];
    } else {
      return QUESTION_TYPES[1];
    }
  }
}
