import {Component, Input, OnChanges, SimpleChanges, ViewChild} from '@angular/core';
import {AnswerChoice, TestQuestion} from "../../../../../../../shared/src/lib/shared-model";
import {Survey} from "../../../model/survey.model";
import {SurveySandboxService} from "../../../services/survey-sandbox.service";
import {Observable, throwError} from "rxjs";
import {catchError, map} from "rxjs/operators";
import {TestStructureComponent} from "../../../../../../../shared/src/lib/structures/test-structure/test-structure.component";

@Component({
  selector: 'jrs-survey-question',
  template: `
    <jrs-section [color]="'primary'" [outline]="false">
      <jrs-header-text [size]="'medium'">АСУУЛТУУД</jrs-header-text>
      <jrs-test-structure
        #testStructure
        [load]="load"
        [questions]="questions"
        [configurations]="false"
        [hasRequired]="true"
        [selectDisabled]="true"
        [fillInTheBlank]="true"
        (modelChange)="modelChange($event)"></jrs-test-structure>
    </jrs-section>`,
  styles: []
})
export class SurveyQuestionComponent implements OnChanges {
  @ViewChild("testStructure") testStructure: TestStructureComponent;
  @Input() survey: Survey;
  @Input() questions: TestQuestion[] = [];
  @Input() load = false;
  initialQuestions: TestQuestion[];

  constructor(private sb: SurveySandboxService) {
  }

  ngOnChanges(changes: SimpleChanges): void {
    for (const prop in changes) {
      if (prop == "survey" && this.survey) {
        this.loadQuestions();
      }
    }
  }

  modelChange($event): void {
    this.questions = $event.questions;
  }

  isDataChanged(): boolean {
    return JSON.stringify(this.questions) !== JSON.stringify(this.initialQuestions);
  }

  save(order: Observable<any>[]): Observable<any>[] {
    order.push(this.sb.saveQuestions(this.questions, this.survey.id, this.survey.contentId).pipe(map(() => {
      this.sb.openSnackbar("Үнэлгээний асуултууд амжилттай хадгаллаа.", true);
      this.initialQuestions = this.mapQuestions(this.questions);
    }), catchError((err) => {
      this.sb.openSnackbar("Үнэлгээний хуудас хадгалах үед алдаа гарлаа!");
      return throwError(err);
    })));
    return order;
  }

  hasErrors(): boolean {
    return this.testStructure && !this.testStructure.isValid();
  }

  private loadQuestions(): void {
    this.load = true;
    this.sb.getSurveyQuestions(this.survey.contentId).subscribe(res => {
      this.questions = this.mapQuestions(res);
      this.initialQuestions = this.mapQuestions(res);
      this.load = false;
    }, () => {
      this.load = false;
    });
  }

  private mapQuestions(testQuestions: TestQuestion[]): TestQuestion[] {
    const questions: TestQuestion[] = [];
    for (const testQuestion of testQuestions) {
      const answers: AnswerChoice[] = [];
      for (const answer of testQuestion.answers) {
        answers.push({value: answer.value, correct: answer.correct, weight: answer.weight, formName: answer.formName, column: answer.column})
      }
      const question = new TestQuestion(testQuestion.question, answers, testQuestion.type);
      question.required = testQuestion.required;
      questions.push(question);
    }
    return questions;
  }
}
