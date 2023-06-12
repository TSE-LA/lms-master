import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {LearnerQuestionModel} from "../../../../core/src/lib/exam/model/exam.model";

@Component({
  selector: 'jrs-question-navigator',
  template: `
    <div class="navigator">
      <div class="counter">
        <span>{{selectedQuestionCount}}/{{totalQuestions}}</span>
      </div>
      <div class="question-swiper">
        <jrs-button
          class="swipe-left"
          [color]="'transparent'"
          [iconName]="'chevron_left'"
          [isMaterial]="true"
          [iconColor]="'dark'"
          [outline]="false"
          [size]="'icon-medium-responsive'"
          (clicked)="swipeLeft($event)">
        </jrs-button>
        <div
          class="questions"
          (mousewheel)="mouseWheel($event)"
          (mousedown)="mouseDown($event)"
          (mouseleave)="mouseLeave()"
          (mouseup)="mouseUp()"
          (mousemove)="mouseMove($event)">
          <div
            *ngFor="let question of questions"
            class="question"
            [ngClass]="{'selected': question.selected}"
            (click)="selectQuestion(question.index)">
            {{question.index}}
          </div>
        </div>
        <jrs-button
          class="swipe-right"
          [color]="'transparent'"
          [iconName]="'chevron_right'"
          [isMaterial]="true"
          [iconColor]="'dark'"
          [outline]="false"
          [size]="'icon-medium-responsive'"
          (clicked)="swipeRight($event)">
        </jrs-button>
      </div>
    </div>
  `,
  styleUrls: ['./question-navigator.component.scss']
})
export class QuestionNavigatorComponent implements OnChanges {
  @Input() totalQuestions: number;
  @Input() questions: LearnerQuestionModel[];
  @Input() selectedQuestionCount: number = 0;
  @Output() scroll = new EventEmitter<string>();
  isDown = false;
  startX: number;
  scrollLeft: number;
  swiper: Element;

  ngOnChanges(changes: SimpleChanges) {
    for (let prop in changes) {
      if (prop == 'questions') {
        this.swiper = document.querySelector('.questions');
        this.scrollLeft = this.swiper.scrollLeft;
      }
    }
  }

  swipeLeft(event): void {
      event.preventDefault();
      this.scrollLeft -= 100;
      this.swiper.scrollLeft = this.scrollLeft;
  }

  swipeRight(event): void {
      event.preventDefault();
      this.scrollLeft += 100;
      this.swiper.scrollLeft = this.scrollLeft;
  }

  mouseDown(event): void {
      this.isDown = true;
      this.swiper.classList.add('active');
      this.startX = event.pageX - this.swiper.getBoundingClientRect().left;
  }

  mouseLeave(): void {
      this.isDown = false;
      this.scrollLeft = this.swiper.scrollLeft;
  }


  mouseUp(): void {
      this.swiper.classList.remove('active');
  }

  mouseMove(event): void {
    if (!this.isDown) {
      return;
    }
      event.preventDefault();
      const x = event.pageX - this.swiper.getBoundingClientRect().left;
      const walk = x - this.startX;
      this.swiper.scrollLeft = this.scrollLeft - walk;
  }

  mouseWheel(event): void {
      event.preventDefault();
      this.scrollLeft += event.deltaY;
      this.swiper.scrollLeft = this.scrollLeft;
  }

  selectQuestion(index): void {
    this.scroll.emit(index.toString());
  }
}
