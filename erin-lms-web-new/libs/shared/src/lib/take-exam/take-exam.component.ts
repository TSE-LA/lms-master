import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, ViewChild} from '@angular/core';
import {LearnerQuestionModel} from "../../../../core/src/lib/exam/model/exam.model";
import {QuestionListComponent} from "../question-list/question-list.component";
import {CountdownTimerComponent} from "../countdown-timer/countdown-timer.component";

@Component({
  selector: 'jrs-take-exam',
  template: `
    <div class="container">
      <div class="header-wrapper">
        <div class="header">
          <span>{{title}}</span>
        </div>
        <div class="gadgets">
          <jrs-countdown-timer
            #countdownTimer
            [remainingTime]="remainingTime"
            [durationInSeconds]="durationInSeconds"
            (timeOut)="this.timeOut.emit($event)">
          </jrs-countdown-timer>
          <jrs-question-navigator
            *ngIf="questions != null"
            [selectedQuestionCount]="selectedQuestionCount"
            [questions]="navigatorQuestions"
            [totalQuestions]="navigatorQuestions.length"
            (scroll)="scrollToQuestion($event)">
          </jrs-question-navigator>
        </div>
      </div>
      <jrs-question-list
        #questionList
        [questions]="questions"
        (update)="update($event)"
        (updateNavigator)="updateNavigator($event)">
      </jrs-question-list>
      <div class="finish-exam">
        <jrs-button
          (clicked)="finishExam()"
          [color]="'primary'"
          [size]="'medium'">
          ДУУСГАХ
        </jrs-button>
      </div>
    </div>
    <jrs-page-loader [show]="loading"></jrs-page-loader>
  `,
  styleUrls: ['./take-exam.component.scss']
})
export class TakeExamComponent implements OnInit, OnChanges {
  @Input() title: string;
  @Input() durationInSeconds: number;
  @Input() questions: LearnerQuestionModel[] = [];
  @Input() loading: boolean;
  @Input() remainingTime: number;
  @Output() endExam = new EventEmitter<string>();
  @Output() timeOut = new EventEmitter<string>();
  @Output() updateExam = new EventEmitter<LearnerQuestionModel[]>();
  @ViewChild('questionList') questionList: QuestionListComponent;
  @ViewChild('countdownTimer') countdownTimer: CountdownTimerComponent;
  startTime: Date;
  navigatorQuestions: LearnerQuestionModel[];
  selectedQuestionCount;

  ngOnInit() {
    this.startTime = new Date();
  }

  ngOnChanges(changes: SimpleChanges) {
    for (let prop in changes) {
      if (prop == 'questions') {
        this.navigatorQuestions = this.questions;
        this.updateCount();
      }
    }
  }

  finishExam(): void {
    this.endExam.emit('fromTakeExam');
  }

  scrollToQuestion(index: string): void {
    this.questionList.scrollInto(index);
  }

  update(event: LearnerQuestionModel[]): void {
    this.updateExam.emit(event)
  }

  updateNavigator(id: string): void {
    this.navigatorQuestions.forEach(question => {
      if (question.id == id) {
        question.selected = true;
      }
    })
    this.updateCount();
  }

  updateCount(): void {
    let i = 0;
    this.navigatorQuestions.forEach( question => {
      if(question.selected){
        i++;
      }
    })
    this.selectedQuestionCount = i;
  }

  clearInterval(): void{
    this.countdownTimer.clearInterval();
  }
}
