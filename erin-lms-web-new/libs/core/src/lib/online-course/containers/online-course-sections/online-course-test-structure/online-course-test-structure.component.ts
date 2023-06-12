import {Component, Input, OnChanges, SimpleChanges, ViewChild} from '@angular/core';
import {OnlineCourseSandboxService} from "../../../online-course-sandbox.service";
import {OnlineCourseModel, TestModel} from "../../../models/online-course.model";
import {AnswerChoice, TestQuestion} from "../../../../../../../shared/src/lib/shared-model";
import {TestStructureComponent} from "../../../../../../../shared/src/lib/structures/test-structure/test-structure.component";
import {catchError, map} from "rxjs/operators";
import {throwError} from "rxjs";

@Component({
  selector: 'jrs-online-course-test-structure',
  template: `
    <ng-container *ngIf="hasTest">
      <jrs-section>
        <div class="inline-flex center">
          <jrs-header-text [size]="'medium'">Сорил</jrs-header-text>
        </div>
        <jrs-test-structure
          #testStructure
          [questions]="testModel.questions"
          [attempt]="testModel.attempt"
          [threshold]="testModel.threshold"
          [load]="load"
          (modelChange)="assignModel($event)">
        </jrs-test-structure>
      </jrs-section>
    </ng-container>
  `,
  styles: []
})
export class OnlineCourseTestStructureComponent implements OnChanges {
  @ViewChild("testStructure") testStructure: TestStructureComponent;
  @Input() course: OnlineCourseModel;
  @Input() hasTest = true;
  initialTestModel: TestModel = {testId: "", testName: "", attempt: 1, threshold: 1, questions: []};
  testModel: TestModel = {testId: "", testName: "", attempt: 1, threshold: 1, questions: []};
  savingTest: boolean;
  loaded: boolean;
  load = false;
  private firstTimeSave = true;

  constructor(private sb: OnlineCourseSandboxService) {
  }

  ngOnChanges(changes: SimpleChanges): void {
    for (const prop in changes) {
      if (prop == "course" && this.course && !this.loaded && this.course.hasTest) {
        this.loaded = true;
        this.load = true;
        this.sb.getOnlineCourseTest(this.course.id).subscribe(res => {
          this.initialTestModel = this.mapTest(res);
          this.testModel = this.mapTest(res);
          this.load = false;
        }, () => {
          this.load = false;
        });
      }
    }
  }

  isDataChanged(): boolean {
    return JSON.stringify(this.testModel) !== JSON.stringify(this.initialTestModel);
  }

  hasErrors(): boolean {
    return this.testStructure && !this.testStructure.isValid();
  }

  saveTest(order: any[]): any[] {
    if (!this.hasTest) {
      return order;
    }

    this.savingTest = true;
    if (this.firstTimeSave) {
      order.push(this.sb.saveCourseTest(this.course.id, this.testModel).pipe(map(res => {
        this.savingTest = false;
        this.initialTestModel = this.mapTest(res);
        this.testModel = this.mapTest(res);
        this.sb.openSnackbar("Цахим сургалтын сорил амжилттай хадгаллаа.", true);
      }), catchError(err => {
        this.savingTest = false;
        this.sb.openSnackbar("Цахим сургалтын сорил хадгалахад алдаа гарлаа!");
        return throwError(err);
      })));
    } else {
      order.push(this.sb.updateCourseTest(this.course.id, this.testModel).pipe(map(res => {
        this.savingTest = false;
        this.initialTestModel = this.mapTest(res);
        this.testModel = this.mapTest(res);
        this.sb.openSnackbar("Цахим сургалтын сорил амжилттай шинэчлэгдлээ.", true);
      }), catchError(err => {
        this.sb.openSnackbar("Цахим сургалтын сорил засварлахад алдаа гарлаа!");
        return throwError(err);
      })));
    }
    return order;
  }

  isPublishReady(): boolean {
    if (!this.testStructure) {
      return true;
    }
    return this.testStructure.isValid();
  }

  assignModel($event: any): void {
    this.testModel.questions = $event.questions;
    this.testModel.attempt = $event.attempt;
    this.testModel.threshold = $event.threshold;
  }

  private mapTest(testModel: TestModel): TestModel {
    this.firstTimeSave = false;
    const questions: TestQuestion[] = [];
    for (const question of testModel.questions) {
      const answers: AnswerChoice[] = [];
      for (const answer of question.answers) {
        answers.push({value: answer.value, correct: answer.correct, weight: answer.weight, formName: answer.formName, column: answer.column})
      }
      questions.push(new TestQuestion(question.question, answers, question.type))
    }
    return {testId: testModel.testId, attempt: testModel.attempt, threshold: testModel.threshold, questions: questions, testName: testModel.testName};
  }
}
