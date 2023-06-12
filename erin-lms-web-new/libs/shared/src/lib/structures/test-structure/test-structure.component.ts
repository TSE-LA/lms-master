import {Component, EventEmitter, Input, Output} from '@angular/core';
import {QuestionTypes, TestQuestion} from "../../shared-model";

@Component({
  selector: 'jrs-test-structure',
  template: `
    <div class="test-structure-container">
      <ng-container *ngIf="!load">
        <div class="row gx-1">
          <div class="test-structure-header">
            <jrs-input-field
              *ngIf="configurations"
              [selectedType]="'number'"
              [movePlaceholder]="true"
              [size]="'medium'"
              [value]="attempt"
              [errorText]="'Утга оруулна уу'"
              [placeholder]="'Оролдлогын тоо'"
              (inputChanged)="assignAttempt($event)">
            </jrs-input-field>
            <jrs-input-field
              *ngIf="configurations"
              class="margin-left"
              [selectedType]="'number'"
              [movePlaceholder]="true"
              [size]="'medium'"
              [value]="threshold"
              [placeholder]="'Босго оноо'"
              [error]="thresholdError"
              (inputChanged)="assignThreshold($event)">
            </jrs-input-field>
            <div class="spacer"></div>
            <div class=" row gx-1 margin-bottom">
              <div class="test-structure-header">
                <jrs-button
                  [size]="'long'"
                  [iconColor]="'gray'"
                  [iconName]="'radio_button_checked'"
                  [outline]="true"
                  [textColor]="'text-gray'"
                  (clicked)="addQuestion(SINGLE_CHOICE)"
                  [title]="'Нэг сонголттой'">
                </jrs-button>
                <jrs-button
                  *ngIf="multiChoice"
                  class="margin-left"
                  [size]="'long'"
                  [iconColor]="'gray'"
                  [iconName]="'check_box'"
                  [outline]="true"
                  [textColor]="'text-gray'"
                  (clicked)="addQuestion(MULTIPLE_CHOICE)"
                  [title]="'Олон сонголттой'">
                </jrs-button>
                <jrs-button
                  *ngIf="fillInTheBlank"
                  class="margin-left"
                  [size]="'long'"
                  [iconColor]="'gray'"
                  [iconName]="'contact_support'"
                  [outline]="true"
                  [textColor]="'text-gray'"
                  (clicked)="addQuestion(FILL_IN_BLANK)"
                  [title]="'Нээлттэй асуулт'">
                </jrs-button>
              </div>
            </div>
          </div>
        </div>
        <div class=" row gx-1 margin-bottom">
          <jrs-label *ngIf="thresholdError" [error]="true" [text]="'Босго оноо 0-с бага эсвэл асуултын тооноос их байж болохгүй!'"></jrs-label>
          <jrs-label *ngIf="questionTitleError" [error]="true" [text]="'Асуулт давхцаж байна! '"></jrs-label>
          <jrs-label *ngIf="answerSameValueError" [error]="true" [text]="'Хариулт давхцаж байна! '"></jrs-label>
          <jrs-label *ngIf="correctAnswerError" [error]="true" [text]="'Зөв хариулт сонгоно уу!'"></jrs-label>
          <jrs-label *ngIf="hasNothingError" [error]="true" [text]="'Сорилтой бол сорилын агуулга оруулна уу!'"></jrs-label>
          <jrs-label *ngIf="emptyAnswerError || emptyQuestionError" [error]="true" [text]="'Асуулт эсвэл хариулт хоосон байна!'"></jrs-label>
        </div>
        <ng-content></ng-content>
        <div class="row gx-1">
          <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-12">
            <div class="question-container">
              <ng-container *ngFor="let question of questions; let questionIndex=index">
                <div (drop)="drop(questionIndex, $event)"
                     (dragover)="allowDrop(questionIndex, $event)"
                     [class.drag-over]="dragIndex!== questionIndex && questionIndex === dragOverIndex">
                  <div class="question"
                       draggable="true"
                       (dragstart)="drag(questionIndex)">
                    <div class="drag-button">
                      <jrs-icon
                        [size]="'medium'"
                        [color]="'gray'"
                        [mat]="true">
                        drag_indicator
                      </jrs-icon>
                    </div>
                    <div class="question-input-area" [class.question-disabled]="disabled">
                      <span class="order-number">{{questionIndex + 1}}.</span>
                      <input
                        [(ngModel)]="question.question"
                        (input)="setQuestion(); checkQuestionName()"
                        [placeholder]="'Асуулт...'"
                        class="input">
                    </div>
                    <div class="trash-button" *ngIf="!disabled" (click)="deleteQuestion(questionIndex)">
                      <jrs-icon
                        [size]="'medium'"
                        [color]="'light'"
                        [mat]="false">
                        jrs-trash-can
                      </jrs-icon>
                    </div>
                  </div>

                  <div *ngIf="question.type !== FILL_IN_BLANK">
                    <ng-container>
                      <ng-container *ngFor="let answer of question.answers; let answerIndex= index">
                        <div class="answer">
                          <div class="answer-action-button">
                            <jrs-checkbox *ngIf="question.type === MULTIPLE_CHOICE"
                                          [check]="answer.correct"
                                          [noOutline]="true"
                                          [padding]="false"
                                          (checked)="answer.correct = $event "
                                          [disabled]="selectDisabled">
                            </jrs-checkbox>
                            <jrs-radio-button *ngIf="question.type === SINGLE_CHOICE"
                                              [group]="questionIndex.toString()"
                                              [check]="answer.correct"
                                              [disabled]="selectDisabled"
                                              (clicked)="answer.correct = $event">
                            </jrs-radio-button>
                          </div>
                          <div class="answer-input-area"
                               [class.answer-disabled]="disabled"
                               [class.reserve-space]="!disabled && answerIndex!==0">
                            <span class="order-number">{{answerIndex + 1}}.</span>
                            <input
                              [(ngModel)]="answer.value"
                              (input)="setQuestion(); checkAnswerName(questionIndex)"
                              [placeholder]="'Хариулт...'"
                              class="input">
                            <jrs-circle-button
                              *ngIf="!disabled"
                              [size]="'medium'"
                              [isMaterial]="true"
                              [iconName]="'close'"
                              [color]="'none'"
                              [iconColor]="'warn'"
                              (click)="deleteAnswer(questionIndex, answerIndex)">
                            </jrs-circle-button>
                          </div>
                          <jrs-circle-button
                            class="answer-add-btn"
                            *ngIf="!disabled && answerIndex===0"
                            [size]="'medium'"
                            [isMaterial]="true"
                            [iconName]="'add'"
                            [color]="'gray'"
                            [iconColor]="'light'"
                            (click)="addAnswer(questionIndex)">
                          </jrs-circle-button>
                        </div>
                      </ng-container>
                    </ng-container>
                  </div>
                  <div class="actions flex margin-bottom margin-right">
                    <span class="spacer"></span>
                    <jrs-slide-toggle *ngIf="hasRequired" [checked]="question.required" (clicked)="question.required = $event"></jrs-slide-toggle>
                  </div>

                </div>
              </ng-container>
              <jrs-not-found-page [show]="questions.length < 1" [size]="'smallest'" [text]="'Асуулт оруулна уу.'"></jrs-not-found-page>
            </div>
          </div>
        </div>
      </ng-container>
      <div *ngIf="load">
        <jrs-skeleton-loader [amount]="5" [load]="load"></jrs-skeleton-loader>
      </div>
    </div>
  `,
  styleUrls: ['./test-structure.component.scss']
})
export class TestStructureComponent {
  @Input() questions: TestQuestion[] = [];
  @Input() attempt = 1;
  @Input() threshold = 1;
  @Input() configurations = true;
  @Input() multiChoice = true;
  @Input() fillInTheBlank = false;
  @Input() disabled: boolean;
  @Input() load: boolean;
  @Input() hasRequired = false;
  @Input() selectDisabled = false;
  @Output() modelChange = new EventEmitter<any>();
  dragIndex: number;
  dragOverIndex: number;
  SINGLE_CHOICE = QuestionTypes.SINGLE_CHOICE;
  MULTIPLE_CHOICE = QuestionTypes.MULTIPLE_CHOICE;
  FILL_IN_BLANK = QuestionTypes.FILL_IN_BLANK;
  questionTitleError: boolean;
  answerSameValueError: boolean;
  correctAnswerError: boolean;
  hasNothingError: boolean;
  emptyQuestionError: boolean;
  emptyAnswerError: boolean;
  thresholdError = false;

  drop(dropIndex: number, e): void {
    e.preventDefault();
    const temp = this.questions[this.dragIndex];
    this.questions[this.dragIndex] = this.questions[dropIndex];
    this.questions[dropIndex] = temp;
    this.dragIndex = null;
    this.dragOverIndex = null;
    this.modelChange.emit({questions: this.questions, attempt: this.attempt, threshold: this.threshold});
  }

  allowDrop(dragOverIndex, e): void {
    e.preventDefault();
    this.dragOverIndex = dragOverIndex;
  }

  drag(dragIndex: number): void {
    this.dragIndex = dragIndex;
  }

  addAnswer(index: number): void {
    this.questions[index].answers.push({value: null, correct: false, weight: 1, column: 1});
    this.setQuestion();
  }

  deleteQuestion(moduleIndex: number): void {
    if (this.questions.length > 1) {
      this.questions.splice(moduleIndex, 1);
    }
    this.setQuestion();
  }

  deleteAnswer(moduleIndex: number, sectionIndex: number): void {
    const sections = this.questions[moduleIndex].answers;
    if (sections.length > 2) {
      sections.splice(sectionIndex, 1);
    } else {
      this.deleteQuestion(moduleIndex);
    }
    this.setQuestion();
  }

  addQuestion(type: QuestionTypes): void {
    const question = new TestQuestion('', [],
      type);
    if (type != QuestionTypes.FILL_IN_BLANK) {
      question.answers = [
        {value: null, correct: false, weight: 1, column: 1, formName: '0'},
        {value: null, correct: false, weight: 1, column: 1, formName: '1'}];
    }
    this.questions.push(question);
    this.checkThreshold(this.threshold);
    this.setQuestion();
  }

  setQuestion(): void {
    this.modelChange.emit({questions: this.questions, attempt: this.attempt, threshold: this.threshold});
  }

  checkQuestionName(): void {
    const list = new Set(this.questions.map(question => question.question));
    this.questionTitleError = list.size != this.questions.length;
  }

  checkAnswerName(index: number): void {
    const list = new Set(this.questions[index].answers.map(answer => answer.value));
    this.answerSameValueError = list.size != this.questions[index].answers.length;
  }

  isValid(): boolean {
    this.hasNothingError = false;
    this.correctAnswerError = false;
    this.emptyQuestionError = false;
    this.emptyAnswerError = false;
    if (this.questions.length < 1) {
      this.hasNothingError = true;
      return false;
    }
    let hasCorrect;
    for (const question of this.questions) {
      if (!question.question || question.question == "") {
        this.emptyQuestionError = true;
        break;
      }
      for (const answer of question.answers) {
        if (!answer.value || answer.value == "") {
          this.emptyAnswerError = true;
          break;
        }
        if (!hasCorrect) {
          hasCorrect = answer.correct;
        }
      }
      if (!this.selectDisabled && !hasCorrect) {
        this.correctAnswerError = true;
      }
    }
    return !this.thresholdError && !this.emptyQuestionError && !this.emptyAnswerError && !this.correctAnswerError && !this.answerSameValueError && !this.questionTitleError;
  }

  assignAttempt(value: number): void {
    this.attempt = Number(value);
    this.modelChange.emit({questions: this.questions, attempt: this.attempt, threshold: this.threshold});
  }

  assignThreshold(value: number): void {
    this.checkThreshold(value);
    this.threshold = Number(value);
    this.modelChange.emit({questions: this.questions, attempt: this.attempt, threshold: this.threshold});
  }

  private checkThreshold(value: number): void {
    this.thresholdError = false;
    if (value > this.questions.length || value < 0) {
      this.thresholdError = true;
    }
  }
}
