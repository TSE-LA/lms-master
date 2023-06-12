import {Component, Input} from '@angular/core';

@Component({
  selector: 'jrs-event-details',
  template: `
    <div class="event-row" *ngIf="!load">
      <div class="description-name">
        <div
          class="name"
          *ngFor="let name of descriptionName">
          {{name}}:
        </div>
      </div>
      <div class="description-value">
        <div
          class="value"
          *ngFor="let value of descriptionValues">
          <span
            jrsTooltip="{{tooltip?value: null}}"
            placement="{{tooltip}}"
            delay="500">
            {{value}}
          </span>
        </div>
      </div>
    </div>
    <div class="loader" *ngIf="load">
      <div>
        <div *ngFor="let item of [].constructor(5)" class="shimmer left"></div>
      </div>
      <div class="right-container">
        <div *ngFor="let item of [].constructor(5)" class="shimmer right"></div>
      </div>
    </div>
  `,
  styleUrls: ['./event-details.component.scss']
})
export class EventDetailsComponent {

  @Input() descriptionName = [];
  @Input() descriptionValues = [];
  @Input() load: boolean;
  @Input() tooltip: string;

}
