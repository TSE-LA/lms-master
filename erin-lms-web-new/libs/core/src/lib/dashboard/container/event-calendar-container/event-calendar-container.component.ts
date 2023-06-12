import {Component, OnInit} from "@angular/core";
import {FormBuilder, FormGroup} from "@angular/forms";
import {CalendarDayProperties} from "../../../../../../shared/src/lib/shared-model";
import {CourseProperties} from "../../../classroom-course/model/classroom-course.model";
import {CoursePropertyUtil} from "../../../../../../shared/src/lib/utilities/course-property.util";
import {MONTHS} from "../../../classroom-course/model/classroom-course.constants";
import {DashboardService} from "../../service/dashboard.service";
import {CALENDAR_FILTERS_STATE, CALENDAR_FILTERS_TYPE} from "../../../../../../shared/src/lib/shared-constants";

@Component({
  selector: 'jrs-event-calendar',
  template: `
    <div class="calendar-container">
      <div class="calendar-frames">
        <div class="flex">
          <jrs-header-text>СУРГАЛТЫН НЭГДСЭН ХУАНЛИ</jrs-header-text>
          <div class="spacer"></div>
          <form [formGroup]="pickerFormGroup" class="date-picker-style">
            <div class="flex">
              <jrs-button
                class="margin-x-5"
                [iconName]="'chevron_left'"
                [isMaterial]="true"
                [outline]="true"
                [iconColor]="'dark'"
                [size]="'icon-medium'"
                (clicked)="previousMonth()">
              </jrs-button>
              <div class="text-header-style">
                <jrs-underscore-dropdown
                  [className]="'year-picker-style'"
                  [formGroup]="pickerFormGroup"
                  [formType]="'year'"
                  [values]="getPickerValues('year')"
                  [defaultValue]="currentYear.toString()"
                  (selectionChange)="onEventSelect($event, 'year')">
                </jrs-underscore-dropdown>
                <p class="margin-x-10">oн</p>
                <jrs-underscore-dropdown
                  [className]="'month-picker-style'"
                  [formGroup]="pickerFormGroup"
                  [formType]="'month'"
                  [values]="getPickerValues('month')"
                  [defaultValue]="currentMonth.toString()"
                  (selectionChange)="onEventSelect($event, 'month')">
                </jrs-underscore-dropdown>
                <p class="margin-x-10">сар</p>
              </div>
              <jrs-button
                class="margin-x-5"
                [iconName]="'chevron_right'"
                [isMaterial]="true"
                [iconColor]="'dark'"
                [outline]="true"
                [size]="'icon-medium'"
                (clicked)="nextMonth()">
              </jrs-button>
            </div>
          </form>
        </div>
        <jrs-calendar-frame
          [ngClass]="{'loading': loading}"
          [calendarProperties]="calendarDays"
          [mobile]="mobile"
          (eventClicked)="eventClicked($event)">
        </jrs-calendar-frame>
      </div>
      <div>
        <jrs-calendar-filters (onCheck)="checked($event)">
        </jrs-calendar-filters>
      </div>
    </div>
    <jrs-calendar-sidenav
      [drawerToggle]='showDrawer'
      [data]="courseProperties">
    </jrs-calendar-sidenav>
  `,
  styleUrls: ['./event-calendar-container.component.scss']
})
export class EventCalendarContainerComponent implements OnInit {
  pickerFormGroup: FormGroup;
  loading = false;
  mobile: boolean;
  showDrawer = false;
  date = new Date();
  todayFullDate: string;
  month: string;
  months = MONTHS;
  currentDate: string;
  role: string;
  currentYear = new Date().getFullYear();
  currentMonth = new Date().getMonth() + 1;
  calendarDays: CalendarDayProperties[] = [];
  calendarEvents: CalendarDayProperties[] = [];
  courseProperties: CourseProperties = CoursePropertyUtil.getEmptyEvent();
  previousMonthDays = new Map();
  nextMonthDays = new Map();
  monthDays = new Map();
  private hasSurvey: boolean;
  private isTablet: boolean;
  state = CALENDAR_FILTERS_STATE;
  type = CALENDAR_FILTERS_TYPE;

  constructor(private fb : FormBuilder, private sb: DashboardService) {
    this.sb.userRole$.subscribe(res => this.role = res);
    this.sb.getMediaBreakPointChange().subscribe(media => {
      if (media == 'media_md' || media == 'media_s' || media == 'media_sm' || media == 'media_xs') {
        this.isTablet = true;
      } else {
        this.isTablet = false;
      }
      this.mobile = media == 'media_s' || media == 'media_sm' || media == 'media_xs';
    });
  }
  ngOnInit():void {
    this.pickerFormGroup =  this.fb.group({
      year: this.currentYear,
      month: this.currentMonth
    });
    const today = new Date();
    const month = today.getMonth() + 1;
    this.todayFullDate = today.getFullYear() + "." + month + "." + today.getDate();
    this.renderCalendar();
  }

  checked({ id, isChecked }: {id: string, isChecked: boolean}): void {
    for (const s of this.state) {
      if (s.id === id) {
        s.checked = isChecked;
      }
    }

    for (const t of this.type) {
      if (t.id === id) {
        t.checked = isChecked;
      }
    }

    this.renderCalendar();
  }
  private renderCalendar(): void {
    this.loading = true;
    this.previousMonthDays.clear();
    this.nextMonthDays.clear();
    this.monthDays.clear();
    this.date.setDate(1);
    const lastDay = new Date(
      this.date.getFullYear(),
      this.date.getMonth() + 1,
      0
    ).getDate();
    const prevLastDay = new Date(
      this.date.getFullYear(),
      this.date.getMonth(),
      0
    ).getDate();

    let firstDayIndex = this.date.getDay() - 1;
    const lastDayIndex = new Date(
      this.date.getFullYear(),
      this.date.getMonth() + 1,
      0
    ).getDay() - 1;
    const nextDays = 14 - lastDayIndex - 1;
    this.month = this.months[this.date.getMonth()];
    let prevMonth = this.months[this.date.getMonth() - 1];
    let nextMonth = this.months[this.date.getMonth() + 1];
    if (this.month == 'January') {
      prevMonth = 'December';
    }
    if (this.month == 'December') {
      nextMonth = 'January';
    }
    const currMonth = this.date.getMonth() + 1;
    this.currentDate = this.date.getFullYear().toString() + '\xa0\xa0' + 'он' + '\xa0\xa0' + this.getMonth(this.date.getMonth());
    const date = this.date.getFullYear() + "-" + currMonth + "-" + this.date.getDate();
    this.sb.getCalendarData(date).subscribe(res => {
      this.calendarEvents = res;
      const checkedCourses = this.type.filter(({ checked }) => checked).map(({ id }) => id);
      const checkedStates = this.state.filter(({ checked }) => checked).map(({ id }) => id);
      const result = res.reduce((result, { month, index, events }) => {
        const filteredEvents = events.filter(({ eventType, state }) =>
          checkedCourses.includes(eventType) && checkedStates.includes(state),
        );
        const prevEvents = result[index];
        if (prevEvents && prevEvents.month === month) {
          return {
            ...result,
            [index]: {
              ...prevEvents,
              events: prevEvents.events.concat(filteredEvents),
            }
          }
        }
        return ({...result, [index]: { month, events: filteredEvents } });
      }, {});
      const eventData = Object.keys(result).map((index) =>
        ({ index: Number(index), month: result[index].month, events: result[index].events }));
      const sortedData = eventData.map((data) => ({...data, events: data.events.sort((a,b)=>(b.title.toLowerCase() < a.title.toLowerCase() ? 1 : b.title.toLowerCase() > a.title.toLowerCase() ? -1 : 0))}));
      for (const day of sortedData) {
        if (day.month.toLowerCase() === prevMonth.toLowerCase()) {
          this.previousMonthDays.set(day.index, day.events);
        } else if (day.month.toLowerCase() === this.month.toLowerCase()) {
          this.monthDays.set(day.index, day.events);
        } else if (day.month.toLowerCase() === nextMonth.toLowerCase()) {
          this.nextMonthDays.set(day.index, day.events);
        }
      }

      const days: CalendarDayProperties[] = [];
      const pickedMonth = this.date.getMonth() + 1;
      const pickedYear = this.date.getFullYear();
      if (firstDayIndex == -1) {
        firstDayIndex = 6;
      }
      for (let x = firstDayIndex - 1; x >= 0; x--) {
        const prev = prevLastDay - x;
        if (this.previousMonthDays.has(prev)) {
          days.push({
            month: 'prev',
            index: prev,
            events: this.previousMonthDays.get(prev)
          })
        } else {
          days.push({
            month: 'prev',
            index: prev,
            events: []
          })
        }
      }
      for (let i = 1; i <= lastDay; i++) {
        const day = pickedYear + "." + pickedMonth + "." + i;
        if (this.monthDays.has(i) && this.todayFullDate == day) {
          days.push({
            index: i,
            events: this.monthDays.get(i),
            month: 'today'
          })
        } else if (!this.monthDays.has(i) && this.todayFullDate == day) {
          days.push({
            index: i,
            events: [],
            month: 'today'
          })
        } else if (this.monthDays.has(i)) {
          days.push({
            index: i,
            events: this.monthDays.get(i)
          })
        } else {
          days.push({
            index: i,
            events: []
          })
        }
      }
      for (let j = 1; j <= nextDays; j++) {
        if (this.nextMonthDays.has(j)) {
          days.push({
            month: 'next',
            index: j,
            events: this.nextMonthDays.get(j)
          })
        } else {
          days.push({
            month: 'next',
            index: j,
            events: []
          })
        }

      }
      if (days.length >= 42) {
        for (let i = days.length; i > 42; i--) {
          days.pop();
        }
      }
      this.calendarDays = days;
      this.loading = false;
    }, () => {
      this.loading = false;
    })
  }

  previousMonth() {
    this.date.setMonth(this.date.getMonth() - 1);
    this.pickerFormGroup.controls['year'].setValue(this.date.getFullYear());
    this.pickerFormGroup.controls['month'].setValue(this.date.getMonth() + 1);
    this.renderCalendar();
  }

  getPickerValues(type: string) {
    const values: number[] = [];
    let min = 2019;
    if (type === 'year') {
      for (min; min <= this.currentYear; min++) {
        values.push(min)
      }
    } else {
      for (let i = 1; i <= 12; i++) {
        values.push(i)
      }
    }
    return values;
  }

  onEventSelect(event: any, type: string) {
    type === 'year' ?
      this.date.setFullYear(event)
      : this.date.setMonth(event - 1);
    this.renderCalendar();
  }

  getMonth(month: number): string {
    return (month + 1) + ' сар';
  }

  nextMonth() {
    this.date.setMonth(this.date.getMonth() + 1);
    this.pickerFormGroup.controls['year'].setValue(this.date.getFullYear());
    this.pickerFormGroup.controls['month'].setValue(this.date.getMonth() + 1);
    this.renderCalendar();
  }

  private openEventNode(id: string): void {
    this.sb.getCourseById(id).subscribe(res => {
      this.hasSurvey = res.hasSurvey;
      this.courseProperties = CoursePropertyUtil.mapToCourseProperties(res, this.calendarEvents);
      this.courseProperties.actions = CoursePropertyUtil.getEventActions(res.state, this.role);
      this.showDrawer = !this.showDrawer;
    }, () => {
      this.showDrawer = !this.showDrawer;
      this.sb.openSnackbar('Хуанли ачааллахад алдаа гарлаа!', false);
    })
  }

  eventClicked(event: any) {
    this.courseProperties = CoursePropertyUtil.getEmptyEvent();
    this.openEventNode(event.id);
  }
}
