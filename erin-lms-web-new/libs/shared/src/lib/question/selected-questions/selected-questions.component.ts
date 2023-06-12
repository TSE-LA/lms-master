import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {QuestionBank} from "../../../../../core/src/lib/exam/model/question.model";

@Component({
  selector: 'jrs-selected-questions',
  template: `
    <div class="margin-bottom-large">
      <div class="flex">
        <jrs-header-text [margin]="false"
                         [size]="'medium'"
                         class="margin-bottom-medium">
          Нэмсэн асуултууд ({{questions.length}})
        </jrs-header-text>
        <span class="spacer"></span>
        <jrs-circle-button
          *ngIf="addButton"
          [size]="'medium'"
          [isMaterial]="true"
          [iconName]="'add'"
          [color]="'gray'"
          [iconColor]="'light'"
          (clicked)="this.openAddQuestion()">
        </jrs-circle-button>
      </div>
      <div class="question-container" *ngIf="questions.length>0">
        <div *ngFor="let question of questions;let index = index" class="flex">
          <div class="flex chosen-questions">
            <span class="margin-right">{{index + 1}}.</span>
            <span class="question-name">{{question.value}}</span>
            <span class="spacer"></span>
            <span class="question-score margin-left">{{question.score}}</span>
          </div>
          <jrs-button class="padding-left margin-left"
                      (clicked)="removeQuestion(index)"
                      [isMaterial]="false"
                      [iconName]="'jrs-trash-can'"
                      [color]="'warn'"
                      [size]="'icon-medium'">
          </jrs-button>
        </div>
      </div>

      <div class="flex" *ngIf="questions.length > 0">
        <span class="spacer"></span>
        <div class="grand-total-score">{{totalScore}} оноо</div>
      </div>
      <div class="flex-center">
        <jrs-label
          *ngIf="questions.length < 1"
          [size]="'large'"
          [text]="'Асуулт сонгоогүй байна ...'">
        </jrs-label>
      </div>
    </div>
  `,
  styleUrls: ['./selected-questions.component.scss']
})
export class SelectedQuestionsComponent implements OnChanges {
  @Input() questions: QuestionBank[] = [];
  @Input() addButton = true;
  @Output() totalChange = new EventEmitter<number>();
  @Output() openDialog = new EventEmitter();
  @Output() removedQuestion = new EventEmitter<QuestionBank>();
  totalScore = 0;

  ngOnChanges(changes: SimpleChanges): void {
    for (const prop in changes) {
      if (prop === "questions") {
        this.calculateTotal();
      }
    }
  }

  removeQuestion(index: number): void {
    this.removedQuestion.emit(this.questions[index]);
    this.questions.splice(index, 1);
    this.calculateTotal();
  }

  openAddQuestion(): void {
    this.openDialog.emit();
  }

  private calculateTotal(): void {
    this.totalScore = 0;
    this.questions.forEach(question => this.totalScore += Number(question.score));
    this.totalChange.emit(this.totalScore);
  }
}
