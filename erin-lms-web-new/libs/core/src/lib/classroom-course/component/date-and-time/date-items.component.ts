import {Component, Input, ViewChild} from "@angular/core";
import {DateItem} from "../../model/classroom-course.model";
import {DatetimeUtil} from "../../../util/date-time-util";
import {TimePickerComponent} from "../../../../../../shared/src/lib/time-picker/time-picker.component";

@Component({
  selector: 'date-items',
  template: `
      <div>
        <div *ngFor="let item of savedDates; let i = index">
          <div class="row gx-1" >
            <div class="col-media_s-6 col-media_sm-6 col-media_md-4 col-media_xl-4">
              <jrs-date-picker (onDateChange)="change($event, i, 'date')"
                               [defaultDate]="item.date"
                               [margin]="false"
                               [width]="'100%'">
              </jrs-date-picker>
            </div>
            <div class="col-media_s-6 col-media_sm-6 col-media_md-3 col-media_xl-3">
              <jrs-time-picker (formChanged)="change($event, i, 'start')"
                               [inputFromKeyboard]="true"
                               [size]="'long'">
              </jrs-time-picker>
            </div>
            <div class="col-media_s-6 col-media_sm-6 col-media_md-3 col-media_xl-3">
              <jrs-time-picker (formChanged)="change($event, i, 'end')"
                               [inputFromKeyboard]="true"
                               [load]="load"
                               [size]="'long'">
              </jrs-time-picker>
            </div>
            <div class="col-media_s-6 col-media_sm-6 col-media_md-2 col-media_xl-2" *ngIf="i !== 0">
              <jrs-button class="padding-left margin-left"
                          (clicked)="removeValue(i)"
                          [iconName]="'delete'"
                          [color]="'warn'"
                          [load]="load"
                          [size]="'icon-medium'">
              </jrs-button>
            </div>
            <div class="col-media_s-6 col-media_sm-6 col-media_md-2 col-media_xl-2" *ngIf="i === 0">
              <jrs-button class="padding-left margin-left"
                          (clicked)="addValue()"
                          [iconName]="'add'"
                          [color]="'primary'"
                          [size]="'icon-medium'">
              </jrs-button>
            </div>
          </div>
        </div>
      </div>
  `,
  styleUrls: ['/date-items.component.scss']
})
export class DateItemsComponent {
  @ViewChild('timepickerComponent') timePickerComponent: TimePickerComponent;
  @Input() load: boolean;
  savedDates: DateItem[] = [];

  constructor() {
    this.addValue();
    // this.dateItemsForm = new FormGroup({
    //   date: new FormControl('', [Validators.required]),
    //   startHour: new FormControl('', [Validators.required, Validators.min(0), Validators.max(23)]),
    //   startMinute: new FormControl('', [Validators.required, Validators.min(0), Validators.max(59)]),
    //   endHour: new FormControl('', [Validators.required, Validators.min(0), Validators.max(23)]),
    //   endMinute: new FormControl('', [Validators.required, Validators.min(0), Validators.max(59)])
    // })
  }

  change(event, i, type): void {
    if (type === 'date') {
      this.savedDates[i].date = DatetimeUtil.parse(event);
    } else if (type === 'start') {
      this.savedDates[i].startHour = parseInt(event.value.hour, 0);
      this.savedDates[i].startMinute = parseInt(event.value.min, 0);
    } else {
      this.savedDates[i].endHour = parseInt(event.value.hour, 0);
      this.savedDates[i].endMinute = parseInt(event.value.min, 0);
    }
  }

  addValue(): void {
    this.savedDates.push({
      date: new Date,
      startHour: undefined,
      startMinute: undefined,
      endHour: undefined,
      endMinute: undefined
    })
  }

  removeValue(index) {
    this.savedDates.splice(index, 1)
  }

  getDateItems(): DateItem[] {
    return this.savedDates;
  }

  checkError(): void {
    // for (const [key] of Object.entries(this.dateItemsForm.controls)) {
    //   if (this.dateItemsForm.controls[key].invalid) {
    //     this.dateItemsForm.controls[key].markAsDirty();
    //   }
    // }
  }
}
