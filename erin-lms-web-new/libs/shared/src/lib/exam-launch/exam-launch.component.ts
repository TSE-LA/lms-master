import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {ExamDetail} from "./model";

@Component({
  selector: 'jrs-exam-launch',
  template: `
    <div class="container" [ngClass]="{'loading': loading}">
      <jrs-confetti *ngIf="igniteConfetti"></jrs-confetti>
      <div class="section">
        <jrs-header-text [load]="loading">{{title}}</jrs-header-text>
        <jrs-divider [marginTop]="'0'" [marginBottom]="'0'" [marginLeft]="'30px'" [marginRight]="'30px'"></jrs-divider>
        <div class="description">{{description}}</div>
        <div class="details">
          <jrs-exam-detail
            [state]="checkState()"
            [celebrate]="celebrate"
            [examDetailsKey]="examDetail.keys"
            [completedExamDetailsKey]="completedExamDetail.keys"
            [examDetailsValue]="examDetail.values"
            [certificateId]="certificateId"
            [loading]="loading"
            [completedExamDetailsValue]="completedExamDetail.values">
          </jrs-exam-detail>
        </div>
        <div class="button">
          <jrs-button
            *ngIf="canStart()"
            (clicked)="startExam()"
            [color]="'primary'"
            [size]="'medium'">
            ЭХЛҮҮЛЭХ
          </jrs-button>
          <jrs-button
            *ngIf="this.isOngoing"
            (clicked)="startExam()"
            [color]="'primary'"
            [size]="'medium'">
            ҮРГЭЛЖЛҮҮЛЭХ
          </jrs-button>
        </div>
      </div>
      <jrs-image-viewer [imageSrc]="imageName"></jrs-image-viewer>
    </div>
  `,
  styleUrls: ['./exam-launch.component.scss']
})
export class ExamLaunchComponent implements OnChanges {
  @Input() status: string;
  @Input() title: string;
  @Input() description: string;
  @Input() remainingAttempt: number;
  @Input() completedExamDetail: ExamDetail;
  @Input() examDetail: ExamDetail;
  @Input() certificateId: string;
  @Input() loading: boolean;
  @Input() maxAttempt: number;
  @Input() celebrate: boolean;
  @Input() isOngoing: boolean;
  @Output() start = new EventEmitter<boolean>();
  imageName = 'assets/images/exam-image.png';
  igniteConfetti = false;


  ngOnChanges(changes: SimpleChanges) {
    for (let prop in changes) {
      if (prop == 'loading') {
        if (this.celebrate && !this.loading) {
          setTimeout(() => {
            this.igniteConfetti = true;
            this.stopConfetti();
          }, 500)
        }
      }
    }
  }

  startExam(): void {
    if (this.isOngoing) {
      this.start.emit(true);
    } else {
      this.start.emit(false);
    }
  }

  stopConfetti(): void {
    setTimeout(() => {
      this.igniteConfetti = false;
    }, 7500)
  }

  canStart(): boolean {
    return this.status == 'STARTED' && !this.loading && !this.isOngoing && this.remainingAttempt > 0;
  }

  canContinue(): boolean {
    return this.isOngoing && this.status == 'STARTED' && !this.loading;
  }

  checkState(): string {
    if(this.status == 'FINISHED'){
      return 'complete';
    }
    else {
      return  'incomplete';
    }
  }
}
