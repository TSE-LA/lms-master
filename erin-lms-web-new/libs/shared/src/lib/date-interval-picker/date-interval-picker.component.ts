import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {DateFormatter} from "../utilities/date-formatter.util";

@Component({
  selector: 'jrs-date-interval-picker',
  template: `
    <div class="date-interval-style">
      <jrs-date-picker
        class="margin-left"
        [label]="'Эхлэх'"
        [defaultDate]="startDate"
        [width]="width"
        (onDateChange)="startDateChange.emit($event)">
      </jrs-date-picker>

      <jrs-date-picker
        class="margin-left"
        [label]="'Дуусах'"
        [defaultDate]="endDate"
        [width]="width"
        (onDateChange)="endDateChange.emit($event)">
      </jrs-date-picker>
    </div>
  `,
  styleUrls: ['./date-interval-picker.component.scss']
})
export class DateIntervalPickerComponent implements OnChanges {
  @Input() week: boolean;
  @Input() month = true;
  @Input() startDate: string;
  @Input() width = '120px';
  @Input() endDate: string;
  @Output() startDateChange = new EventEmitter<string>();
  @Output() endDateChange = new EventEmitter<string>();
  defaultDay = new Date();

  ngOnChanges(changes: SimpleChanges): void {
    for (const prop in changes) {
      if (prop == 'week') {
        this.selectWeekDates();
      }
      if (prop == 'month') {
        this.selectMonthDates();
      }
    }
  }

  private selectWeekDates(): void {
    if (!this.startDate && !this.endDate) {
      const weekDate = new Date();
      weekDate.setDate(weekDate.getDate() + 7);
      this.startDate = DateFormatter.dateFormat(this.defaultDay, '.');
      this.endDate = DateFormatter.dateFormat(weekDate, '.');
    }
  }

  private selectMonthDates(): void {
    if (!this.startDate && !this.endDate) {
      const firstDay = new Date(this.defaultDay.getFullYear(), this.defaultDay.getMonth(), 1)
      const lastDay = new Date(this.defaultDay.getFullYear(), this.defaultDay.getMonth() + 1, 0)
      this.startDate = DateFormatter.dateFormat(firstDay, '.');
      this.endDate = DateFormatter.dateFormat(lastDay, '.');
    }
  }
}
