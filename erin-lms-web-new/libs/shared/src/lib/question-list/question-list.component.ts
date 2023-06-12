import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {LearnerQuestionModel} from "../../../../core/src/lib/exam/model/exam.model";

@Component({
  selector: 'jrs-question-list',
  template: `
    <div class="container">
      <div class="questions" *ngFor="let question of questions" id="{{question.index}}">
        <div class="question-wrapper">
          <div class="question">
            <span class="question-index">АСУУЛТ {{question.index}}</span>
            <div
              *ngIf="question.imagePath"
              class="image"
              [ngStyle]="{'background-image':' url(' + question.imagePath+ ')'}">
            </div>
            <p class="question-value">{{question.value}}</p>
          </div>
          <div class="answer-wrapper">
            <div *ngFor="let answer of question.answers">
              <jrs-radio-button
                [group]="question.id"
                [label]="(answer.index +1) + ' .' + answer.value"
                [check]="answer.selected"
                (clicked)="selectRadioAnswer(question, answer.value); updateNavigate(question.id)">
              </jrs-radio-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styleUrls: ['./question-list.component.scss']
})
export class QuestionListComponent implements OnInit, OnDestroy {
  @Input() questions: LearnerQuestionModel[] = [];
  @Input() scrollIndex: number;
  @Output() update = new EventEmitter<LearnerQuestionModel[]>();
  @Output() updateNavigator = new EventEmitter<any>();
  AUTOSAVE_INTERVAL = 60000;
  autosave;

  ngOnInit() {
    this.triggerAutoSave();
  }

  scrollInto(index: string): void {
    if (document.getElementById(index)) {
      document.getElementById(index).scrollIntoView();
    }
  }

  selectRadioAnswer(selectedQuestion: LearnerQuestionModel, value: string): void {
    this.questions.forEach(question => {
      question.answers.forEach(answer => {
        if (question.id == selectedQuestion.id) {
          answer.selected = answer.value == value;
        }
      })
    });
    this.update.emit(this.questions);
  }

  updateNavigate(id: string): void {
    this.updateNavigator.emit(id);
  }

  triggerAutoSave(): void {
    this.autosave = setInterval(() => {
      this.update.emit(this.questions);
    }, this.AUTOSAVE_INTERVAL);
  }

  ngOnDestroy() {
    clearInterval(this.autosave);
  }
}
