import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {AnswerChoice} from "../../shared-model";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {FormUtil} from "../../utilities/form.util";
import {DEFAULT_ANSWER} from "../../shared-constants";

@Component({
  selector: 'jrs-single-choice-question',
  template: `
    <div class="flex">
      <div class="total-score">
        <jrs-input-field
          [size]="'small'"
          [value]="totalScore"
          [errorText]="'0-с их тоо оруулна уу'"
          [label]="'Нийт оноо'"
          [selectedType]="'number'"
          [placeholder]="''"
          (inputChanged)="reDistributeScores($event)">
        </jrs-input-field>
      </div>
      <jrs-label *ngIf="sameValueError "
                 class="error-label"
                 [error]="true"
                 [size]="'small'"
                 [text]="sameValueErrorText">
      </jrs-label>
      <jrs-label *ngIf="correctAnswerError "
                 class="error-label"
                 [error]="true"
                 [size]="'small'"
                 [text]="answerCorrectErrorText">
      </jrs-label>
    </div>

    <ng-container *ngFor="let answer of answers; let answerIndex= index">
      <div class="answer">
        <div class="answer-action-button">
          <jrs-radio-button
            [group]="'answer'"
            [check]="answer.correct"
            (clicked)="reScore(answerIndex, $event)">
          </jrs-radio-button>
        </div>
        <div class="answer-input-area" [class.reserve-space]="answerIndex!==0">
          <jrs-input-field
            class="answer-input"
            [formGroup]="formGroup"
            [formType]="answer.formName"
            [errorText]="'Утга оруулна уу'"
            [size]="'small'"
            [value]="answer.value"
            [underline]="true"
            [noOutline]="true"
            [movePlaceholder]="false"
            [placeholder]="'Хариулт...'"
            [selectedType]="'text'"
            [required]="true"
            (inputChanged)="checkSameValue($event);answer.value = $event">
          </jrs-input-field>
          <jrs-circle-button
            [size]="'medium'"
            [isMaterial]="true"
            [iconName]="'close'"
            [color]="'none'"
            [iconColor]="'warn'"
            (click)="deleteAnswer(answerIndex)">
          </jrs-circle-button>
        </div>
        <jrs-circle-button
          class="answer-add-btn"
          *ngIf="answerIndex===0"
          [size]="'medium'"
          [isMaterial]="true"
          [iconName]="'add'"
          [color]="'gray'"
          [iconColor]="'light'"
          (click)="addAnswer()">
        </jrs-circle-button>
      </div>
    </ng-container>
  `,
  styleUrls: ['./single-choice-question.component.scss']
})
export class SingleChoiceQuestionComponent implements OnChanges {
  @Input() answers: AnswerChoice[] = [DEFAULT_ANSWER];
  @Input() totalScore = 0;
  @Input() formGroup: FormGroup;
  @Output() totalScoreChange = new EventEmitter<number>();
  sameValueError = false;
  correctAnswerError = false;
  sameValueErrorText = "";
  answerCorrectErrorText = "";

  constructor() {
    this.formGroup = new FormGroup({});
  }

  ngOnChanges(changes: SimpleChanges): void {
    for (const prop in changes) {
      if (prop == "answers") {
        if (this.answers.length > 0) {
          this.correctTheAnswers();
          this.setFormControls();
        }
      }
    }
  }

  addAnswer(): void {
    const newFormName = FormUtil.generateId();
    this.formGroup.addControl(newFormName, new FormControl('', [Validators.required]));
    this.answers.push({value: '', correct: false, weight: 0, column: 1, formName: newFormName});
  }

  deleteAnswer(index: number): void {
    if (this.answers.length == 1) {
      return;
    }
    const controlName = this.answers[index].formName;
    this.answers.splice(index, 1);
    this.formGroup.removeControl(controlName);
  }

  reDistributeScores(value): void {
    this.totalScore = value;
    this.totalScoreChange.emit(this.totalScore);
    this.answers.forEach(answer => {
      if (answer.correct) {
        answer.weight = this.totalScore;
      }
    });
  }

  checkSameValue(value): void {
    for (const answer of this.answers) {
      if (answer.value === value) {
        this.sameValueErrorText = "Ижил хариултууд байна өөрчилнө үү! ";
        this.sameValueError = true;
        return;
      }
    }
    this.sameValueError = false;
  }

  reScore(index, value): void {
    if (!value) {
      this.answers[index].weight = 0;
      this.answers[index].correct = value;
    } else {
      this.answers[index].weight = this.totalScore;
      this.answers[index].correct = value;
      for (let iterator = 0; iterator < this.answers.length; iterator++) {
        if (iterator != index) {
          this.answers[iterator].correct = false;
        }
      }
    }
  }

  isValid(): boolean {
    this.correctAnswerError = false;
    let hasCorrect;
    this.answers.forEach(answer => {
      if (!hasCorrect) {
        hasCorrect = answer.correct
      }
    });
    if (!hasCorrect) {
      this.correctAnswerError = true;
      this.answerCorrectErrorText = "Зөв хариулт сонгоно уу!";
    }

    return FormUtil.isFormValid(this.formGroup) && !this.correctAnswerError && !this.sameValueError;
  }

  private setFormControls(): void {
    for (const answer of this.answers) {
      answer.formName = FormUtil.generateId();
      this.formGroup.addControl(answer.formName, new FormControl(answer.value, [Validators.required]));
    }
  }

  private correctTheAnswers(): void {
    let hasCorrect = false
    this.answers.forEach(answer => {
      if (!hasCorrect) {
        hasCorrect = answer.correct;
        answer.weight = this.totalScore;
      } else {
        answer.correct = false;
        answer.weight = 0;
      }
    })
  }
}
