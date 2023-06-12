import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {AnswerChoice, QuestionTypes} from "../../shared-model";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {FormUtil} from "../../utilities/form.util";
import {DEFAULT_ANSWER} from "../../shared-constants";

@Component({
  selector: 'jrs-multi-choice-question',
  template: `
    <div class="margin-bottom">
      <div class="flex">
        <div class="total-score">
          <jrs-input-field
            [size]="'small'"
            [value]="totalScore"
            [errorText]="'0-с их тоо оруулна уу'"
            [label]="'Нийт оноо'"
            [padding]="false"
            [selectedType]="'number'"
            [placeholder]="''"
            (inputChanged)="reDistributeScores($event)">
          </jrs-input-field>
        </div>
        <jrs-label *ngIf="sameValueError"
                   class="error-label"
                   [error]="true"
                   [size]="'small'"
                   [text]="sameValueErrorText">
        </jrs-label>
        <jrs-label *ngIf="correctAnswerError"
                   class="error-label"
                   [error]="true"
                   [size]="'small'"
                   [text]="answerCorrectErrorText">
        </jrs-label>
      </div>

      <ng-container *ngFor="let answer of answers; let answerIndex = index">
        <div class="answer">
          <div class="answer-section">
            <div class="answer-action-button">
              <jrs-checkbox [check]="answer.correct"
                            (checked)="reScore(answerIndex, $event); answer.correct = $event "
                            [noOutline]="true"
                            [padding]="false">
              </jrs-checkbox>
            </div>
            <div class="answer-input-area">
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
          </div>

          <div class="answer-weight" [class.reserve-space]="answerIndex!==0">
            <jrs-input-field
              [size]="'small'"
              [value]="answer.weight"
              [label]="'Оноо'"
              [errorText]="'0-с их тоо оруулна уу'"
              [selectedType]="'number'"
              (inputChanged)="reCalculateTotal($event, answerIndex)"
              [padding]="false"
              [disabled]="!answer.correct"
              [required]="true"
              [jrsTooltip]="!answer.correct? 'Зөв хариулт сонгоно уу': null">
            </jrs-input-field>
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
    </div>
  `,
  styleUrls: ['./multi-choice-question.component.scss']
})
export class MultiChoiceQuestionComponent implements OnChanges {
  @Input() answers: AnswerChoice[] = [DEFAULT_ANSWER];
  @Input() type = QuestionTypes.SINGLE_CHOICE;
  @Input() totalScore = 0;
  @Output() totalScoreChange = new EventEmitter<number>();
  formGroup: FormGroup;
  sameValueError = false;
  correctAnswerError = false;
  sameValueErrorText = "";
  answerCorrectErrorText = "";

  ngOnChanges(changes: SimpleChanges): void {
    for (const prop in changes) {
      if (prop == "answers") {
        if (this.answers.length > 0) {
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

  reCalculateTotal(score: number, index): void {
    this.totalScore = 0;
    this.answers[index].weight = Number(score);
    this.answers.forEach(answer => this.totalScore += Number(answer.weight));
    this.totalScoreChange.emit(this.totalScore);
  }

  reDistributeScores(value): void {
    let correctAnswers = 0;
    this.answers.forEach(answer => {
      if (answer.correct) {
        correctAnswers += 1;
      }
    });
    this.totalScore = value;
    const answerScore = this.totalScore / correctAnswers;
    this.answers.forEach(answer => {
      if (answer.correct) {
        answer.weight = answerScore;
      }
    });
  }

  reScore(index, value): void {
    if (!value) {
      this.totalScore -= this.answers[index].weight;
      this.answers[index].weight = 0;
    }
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
    this.formGroup = new FormGroup({});
    for (const answer of this.answers) {
      answer.formName = FormUtil.generateId();
      this.formGroup.addControl(answer.formName, new FormControl(answer.value, [Validators.required]));
    }
  }
}
