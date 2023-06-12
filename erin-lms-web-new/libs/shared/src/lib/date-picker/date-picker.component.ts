import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";
import {DateFormatter} from "../utilities/date-formatter.util";

@Component({
  selector: 'jrs-date-picker',
  template: `
    <form [formGroup]="formGroup">
      <div class="wrapper" [ngStyle]="{'width': width, 'min-width': width}" [class.margin-top]="margin">
        <label *ngIf="label"  class="{{labelStyleName}}">
          {{label}}
          <span *ngIf="required" class="required-star-style">*</span>
        </label>
        <mat-form-field [class.error-style-form]="getError()" appearance="outline" [ngStyle]="{'width': width, 'min-width': width}">
          <input matInput class="{{datePickerSize}}" [placeholder]="placeholder" [matDatepicker]="picker" [formControlName]="formType" (dateChange)="dateChanged($event)" [required]="required">
          <mat-datepicker-toggle *ngIf="icon" matSuffix [for]="picker"></mat-datepicker-toggle>
          <mat-datepicker #picker></mat-datepicker>
        </mat-form-field>
        <span *ngIf="getError() && errorText" class="date-error-style" aria-live="polite">&#9888; {{errorText}}</span>
      </div>
    </form>
  `,
  styleUrls: ['./date-picker.component.scss']
})
export class DatePickerComponent implements OnChanges {
  @Input() formGroup = new FormGroup({
    empty: new FormControl(''),
  });
  @Input() formType = 'empty';
  @Input() icon = true;
  @Input() class: string;
  @Input() size = 'medium';
  @Input() labelStyleName = 'label-style';
  @Input() startDate: boolean;
  @Input() monthInterval: boolean;
  @Input() weekInterval: boolean;
  @Input() datePickerSize = 'standard'
  @Input() width = '100%';
  @Input() load: boolean;
  @Input() label: string;
  @Input() placeholder: string;
  @Input() padding = true;
  @Input() style: string;
  @Input() defaultDate: any;
  @Input() required = false;
  @Input() margin = true
  @Input() error: boolean;
  @Input() errorText: string;
  @Output() onDateChange = new EventEmitter();

  ngOnChanges(changes: SimpleChanges): void {
    for (const prop in changes) {
      if (prop == "defaultDate") {
        const date = new Date(this.defaultDate);
        this.formGroup.controls[this.formType].setValue(date);
        this.onDateChange.emit(DateFormatter.toISODateString(date));
      }
    }
  }

  dateChanged(event): void {
    const date: Date = event.value;
    this.onDateChange.emit(DateFormatter.toISODateString(date));
  }

  getError(): boolean {
    if( this.formGroup.controls[this.formType].invalid) {
      return this.error;
    }
  }
}
