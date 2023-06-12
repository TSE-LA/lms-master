import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from "@angular/core";
import {FormControl, FormGroup} from "@angular/forms";
import {DropdownModel} from "../dropdown/dropdownModel";
import {TimeUtil} from "../utilities/time.util";

export class Timepicker {
  constructor(
    public hour: string,
    public minute: string,
  ) {
  }
}

@Component({
  selector: 'jrs-time-picker',
  template: `
    <div *ngIf="!load" [formGroup]="formGroup" [class.error-style]="getError() || this.error">
      <span *ngIf="label" class="time-picker-label">{{label}}</span>
      <div [class]="size" [class.bottom-padding]="padding" class="time-picker-style">
        <form [formGroup]="formGroup">
          <input class="time-input-style hour"
                 type="number"
                 placeholder="HH"
                 onKeyPress="if(this.value.length==2) return false;"
                 [formControlName]="hourFormType"
                 [(ngModel)]="hour"
                 (input)="onInputChanges(hourInput.value, 'hour')"
                 #hourInput>
          <span class="timepicker-spacer">:</span>
          <input class="time-input-style"
                 type="number"
                 placeholder="MM"
                 onKeyPress="if(this.value.length==2) return false;"
                 [formControlName]="minuteFormType"
                 [(ngModel)]="minute"
                 (input)="onInputChanges(minuteInput.value, 'minute')"
                 #minuteInput>
        </form>
        <span class="trigger">
                  <jrs-dropdown [values]="values" (selectionChange)="onDropdownClick($event)">
        <jrs-icon [color]="'primary'"
                  [mat]="true"
                  [size]="'medium'">
          schedule
        </jrs-icon>
                  </jrs-dropdown>
      </span>

      </div>
    </div>
    <div class="loading" *ngIf="load">
      <div class="shimmer"></div>
    </div>
  `,
  styleUrls: ['./time-picker.component.scss'],
})
export class TimePickerComponent implements OnInit, OnChanges {
  @Input() formGroup = new FormGroup({
    hour: new FormControl(''),
    min: new FormControl('')
  });
  @Input() hourFormType = "hour";
  @Input() minuteFormType = "min";
  @Input() showOptions: boolean;
  @Input() inputFromKeyboard = false;
  @Input() defaultValue: string;
  @Input() initialValue = {name: '00 : 00', id: '00'};
  @Input() size = 'long';
  @Input() label: string;
  @Input() load: boolean;
  @Input() error: boolean;
  @Input() padding = true;
  @Input() values: DropdownModel[] = [];
  @Output() formChanged = new EventEmitter<FormGroup>();

  now: string;
  hour: string;
  minute: string;
  times: Timepicker[] = [];

  hours = [
    '08', '09', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19',
    '20', '21', '22', '23', '00', '01', '02', '03', '04', '05', '06', '07'
  ];

  ngOnInit(): void {
    for (const hour of this.hours) {
      this.values.push({id: hour, name: hour + ' : 00'});
      this.values.push({id: hour, name: hour + ' : 30'});
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    for (const prop in changes) {
      if (prop == "initialValue") {
        this.onDropdownClick(this.initialValue);
      }
      if (prop == "formGroup") {
        this.hour = this.formGroup.controls[this.hourFormType].value;
        this.minute = this.formGroup.controls[this.minuteFormType].value;
      }
    }
  }

  onInputChanges(value, type): void {
    if (value && value.length === 2) {
      if (type === 'hour' && value < 24) {
        this.formGroup.controls[this.hourFormType].setValue(value.toString());
        this.error = false;
      } else if (type === 'minute' && value < 60) {
        this.formGroup.controls[this.minuteFormType].setValue(value.toString());
        this.error = false;
      }
    } else if ((type === 'hour' && value > 24) || (type === 'minute' && value > 60)) {
      this.error = true;
    } else {
      this.error = true;
    }
    if (this.formGroup[this.hourFormType] && this.formGroup[this.minuteFormType] || this.inputFromKeyboard) {
      this.formChanged.emit(this.formGroup);
    }
  }

  onDropdownClick(value: any): void {
    if (value) {
      this.error = false
    }
    this.defaultValue = value.name;
    this.hour = TimeUtil.getTimePickerHour(value.name);
    this.minute = TimeUtil.getTimePickerMinute(value.name);
    this.formGroup.controls[this.hourFormType].setValue(this.hour);
    this.formGroup.controls[this.minuteFormType].setValue(this.minute);
    this.formChanged.emit(this.formGroup);
  }

  getError(): boolean {
    return (
      (!this.formGroup.controls[this.hourFormType].pristine && this.formGroup.controls[this.hourFormType].invalid &&
        (!this.formGroup.controls[this.hourFormType].value && this.formGroup.controls[this.hourFormType].value !== 0)) ||
      (!this.formGroup.controls[this.minuteFormType].pristine && this.formGroup.controls[this.minuteFormType].invalid &&
        (!this.formGroup.controls[this.minuteFormType].value && this.formGroup.controls[this.minuteFormType].value !== 0))
    )
  }
}
