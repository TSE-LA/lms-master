import {Component, Input, Output, EventEmitter} from '@angular/core';
import {CalendarDayProperties} from "../../shared-model";

@Component({
  selector: 'jrs-calendar-frame',
  template: `
    <div id="calendar-frame" class="calendar-wrapper">
      <div class="weekdays">
        <div>Даваа</div>
        <div>Мягмар</div>
        <div>Лхагва</div>
        <div>Пүрэв</div>
        <div>Баасан</div>
        <div>Бямба</div>
        <div>Ням</div>
      </div>
      <div class="days">
        <jrs-calendar-node
          class="day"
          *ngFor="let day of calendarProperties"
          [day]="day.index"
          [style]="day.month"
          [events]="day.events"
          [mobile]="mobile"
          [class.today]="day.month === 'today'"
          (eventHover)="onEventHovered($event)"
          (eventClick)="onEventClicked($event)"
          (eventSelect)="onEventSelect($event)">
        </jrs-calendar-node>
      </div>
    </div>
  `,
  styleUrls: ['./calendar-frame.component.scss']
})
export class CalendarFrameComponent {
  @Input() calendarProperties: CalendarDayProperties[];
  @Input() selectedEventProperties: any;
  @Input() mobile = false;
  @Output() eventHovered = new EventEmitter();
  @Output() eventClicked = new EventEmitter();
  @Output() eventSelected = new EventEmitter<string>();

  getMonth(value): void {
    return value;
  }

  onEventHovered(event): void {
    this.eventHovered.emit(event);
  }

  onEventClicked(event): void {
    this.eventClicked.emit(event);
  }

  onEventSelect(event: string): void {
    this.eventSelected.emit(event);
  }
}
