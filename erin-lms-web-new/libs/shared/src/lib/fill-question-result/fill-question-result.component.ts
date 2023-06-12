import {Component, Input} from '@angular/core';
import {ChartDataModel} from "../chart/chart.model";

@Component({
  selector: 'jrs-fill-question-result',
  template: `
    <div class="container">
      <span class="question">{{data.index + '-р асуулт: ' + data.text}}</span>
      <div *ngFor="let answer of data.labels">
        <div *ngIf="answer != ''" class="answer">
          <span>{{answer}}</span>
        </div>
      </div>
    </div>
  `,
  styleUrls: ['./fill-question-result.component.scss']
})
export class FillQuestionResultComponent {
  @Input() data: ChartDataModel;
}
