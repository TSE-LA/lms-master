import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {ExamDetailKey} from "../model";

@Component({
  selector: 'jrs-exam-detail',
  template: `
    <div class="container">
      <div class="column">
        <span class="info"> Шалгалтын мэдээлэл</span>
        <div class="exam-detail">
          <div class="keys">
            <div *ngFor="let key of examDetailsKey" class="key">
              <span class="name">{{key.name}}</span>
            </div>
          </div>
          <div class="values">
            <span *ngFor="let value of examDetailsValue">{{value}}</span>
          </div>
        </div>
      </div>
      <div class="column" [ngClass]="{'show': show}">
        <span class="completed-info"> Шалгалт өгсөн мэдээлэл
          <jrs-icon
            [mat]="true"
            [color]="loading? 'transparent' : 'gray'"
            (click)="toggleVisibility()"
            placement="{{placement}}"
            delay="0"
            jrsTooltip="{{tooltip}}">
            {{show ? invisible : visible}}
          </jrs-icon>
        </span>
        <div class="completed-exam-detail blur">
          <div class="completed-keys">
            <div *ngFor="let key of completedExamDetailsKey" class="completed-key">
              <jrs-icon [mat]="true" [size]="'medium'" [color]="loading? 'transparent' : key.color">{{key.icon}}</jrs-icon>
              <span>{{key.name}}</span>
            </div>
          </div>
          <div class="completed-values">
            <span *ngFor="let value of completedExamDetailsValue" class="value">{{value}}</span>
            <div class="certificate">{{certificateId? 'Татах' : 'Сертификатгүй'}}</div>
          </div>
        </div>
      </div>
    </div>
  `,
  styleUrls: ['./exam-detail.component.scss']
})
export class ExamDetailComponent implements OnChanges {
  @Input() state: string
  @Input() examDetailsKey: ExamDetailKey[] = [];
  @Input() examDetailsValue = [];
  @Input() completedExamDetailsKey: ExamDetailKey[] = [];
  @Input() completedExamDetailsValue = [];
  @Input() certificateId: string;
  @Input() celebrate: boolean;
  @Input() loading: boolean;
  tooltip: string;
  visible = 'visibility';
  invisible = 'visibility_off';
  placement = 'top';
  show = false;

  ngOnChanges(changes: SimpleChanges) {
    this.checkVisibility();
  }

  checkVisibility(): void {
    if (this.celebrate && !this.loading) {
      this.tooltip = 'Нуух';
      setTimeout(() => {
        this.show = true;
      }, 500)
    } else {
      this.tooltip = 'Шалгалтын хугацаа дууссаны дараа нүдэн дээр дарж харна уу.'
    }
  }

  toggleVisibility(): void {
    if(this.state == 'complete') {
      this.show = !this.show;
      this.tooltip = this.show ? 'Нуух' : 'Харах';
    }
  }

}
