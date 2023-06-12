import {Component} from '@angular/core';
import { DUMMY_CALENDAR_DATA } from '../../constants/calendar-constants';

@Component({
  selector: 'jrs-calendar-skeleton-loader',
  template: `
    <div class="calendar-skeleton-loader">
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
          <div class="day" *ngFor="let day of days"></div>
      </div>
    </div>
  `,
  styleUrls: ['./calendar-skeleton-loader.component.scss']
})
export class CalendarSkeletonLoaderComponent {
  days = DUMMY_CALENDAR_DATA;

}
